package com.flowehealth.efr_version.home_screen.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.flowehealth.efr_version.bluetooth.ble.ScanResultCompat
import com.flowehealth.efr_version.bluetooth.services.BluetoothService
import com.flowehealth.efr_version.features.scan.browser.fragments.FilterFragment
import com.flowehealth.efr_version.home_screen.viewmodels.ScanFragmentViewModel
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.home_screen.activities.MainActivity
import com.flowehealth.efr_version.home_screen.viewmodels.MainActivityViewModel
import com.flowehealth.efr_version.home_screen.base.ViewPagerFragment
import com.flowehealth.efr_version.home_screen.utils.SettingsStorage
import com.flowehealth.efr_version.home_screen.viewmodels.DeviceDataViewModel

class ScanFragment : Fragment(), BluetoothService.ScanListener {

    private lateinit var settingsPreferences: SettingsStorage

    private var btService: BluetoothService? = null
    lateinit var scanViewModel: ScanFragmentViewModel
        private set
    val dataViewModel: DeviceDataViewModel by activityViewModels()

    private var activityViewModel: MainActivityViewModel? = null

    private var scanFragmentListener: ScanFragmentListener? = null
    private val viewPagerFragment = ViewPagerFragment()

    private var isFilterViewOn = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scanViewModel = ViewModelProvider(this, ScanFragmentViewModel.Factory(context))
                .get(ScanFragmentViewModel::class.java)
//        dataViewModel = ViewModelProvider(this, DeviceDataViewModel.Factory(context))
//                .get(DeviceDataViewModel::class.java)
        activity?.let {
            activityViewModel = ViewModelProvider(it).get(MainActivityViewModel::class.java)
        }
        settingsPreferences = SettingsStorage(context)
        setupBackStackCallbacks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_scan, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.fragment_scan_label)

        observeChanges()

        if (!viewPagerFragment.isAdded) {
            childFragmentManager.beginTransaction().apply {
                add(R.id.child_fragment_container, viewPagerFragment)
                commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toggleMainNavigation(!isFilterViewOn)
        scanViewModel.updateActiveConnections(btService?.getActiveConnections())
    }

    override fun onPause() {
        super.onPause()
        scanViewModel.setIsScanningOn(false)
    }

    fun setScanFragmentListener(listener: ScanFragmentListener) {
        scanFragmentListener = listener
    }

    private fun setupBackStackCallbacks() {
        activity?.onBackPressedDispatcher?.addCallback(this, backPressedCallback)
    }

    private fun observeChanges() {
        scanViewModel.isScanningOn.observe(viewLifecycleOwner) {
            if (activityViewModel?.isLocationPermissionGranted?.value == true) {
                toggleScannerState(it)
                if (it) scanViewModel.setTimestamps()
                scanFragmentListener?.onScanningStateChanged(it)
            }
            activityViewModel?.isSetupFinished?.observe(viewLifecycleOwner) {
                btService = (activity as? MainActivity)?.bluetoothService
            }
        }
    }

    fun toggleScannerState(isOn: Boolean) {
        if (isOn) startDiscovery()
        else stopDiscovery()
    }

    private fun startDiscovery() {
        (activity as MainActivity).bluetoothService?.let {
            it.removeListener(this)
            it.addListener(this)
            it.startDiscovery(emptyList(), convertScanSetting())
        }
    }

    private fun stopDiscovery() {
        (activity as MainActivity).bluetoothService?.let {
            it.removeListener(this)
            it.stopDiscovery()
        }
    }

    private fun convertScanSetting() : Int? {
        val scanPreference = settingsPreferences.loadScanSetting()
        return if (scanPreference != 0) scanPreference else null

    }

    fun toggleFilterFragment(shouldShowFilterFragment: Boolean) {
        if (shouldShowFilterFragment) {
            childFragmentManager.beginTransaction().apply {
                hide(viewPagerFragment)
                add(R.id.child_fragment_container, FilterFragment())
                addToBackStack(null)
                commit()
            }
        } else {
            childFragmentManager.popBackStack()
            activity?.title = getString(R.string.fragment_scan_label)
        }
        (activity as MainActivity).apply {
            toggleMainNavigation(!shouldShowFilterFragment)
            toggleHomeIcon(shouldShowFilterFragment)
        }
        isFilterViewOn = !isFilterViewOn
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFilterViewOn) {
                toggleFilterFragment(shouldShowFilterFragment = false)
            }
            else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun handleScanResult(scanResult: ScanResultCompat) {
        scanViewModel.handleScanResult(scanResult)
    }

    override fun onDiscoveryFailed() {
        scanViewModel.setIsScanningOn(false)
    }

    override fun onDiscoveryTimeout() {
        scanViewModel.setIsScanningOn(false)
    }

    interface ScanFragmentListener {
        fun onScanningStateChanged(isOn: Boolean)
    }

}