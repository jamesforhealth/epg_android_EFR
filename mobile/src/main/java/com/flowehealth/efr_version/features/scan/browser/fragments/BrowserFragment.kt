/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.flowehealth.efr_version.features.scan.browser.fragments

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.flowehealth.efr_version.BuildConfig.TAG
import com.flowehealth.efr_version.bluetooth.services.BluetoothService
import com.flowehealth.efr_version.bluetooth.ble.BluetoothDeviceInfo
import com.flowehealth.efr_version.bluetooth.ble.ErrorCodes.getDeviceDisconnectedMessage
import com.flowehealth.efr_version.bluetooth.ble.ErrorCodes.getFailedConnectingToDeviceMessage
import com.flowehealth.efr_version.bluetooth.ble.TimeoutGattCallback
import com.flowehealth.efr_version.features.scan.browser.adapters.DebugModeDeviceAdapter
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.utils.SharedPrefUtils
import com.flowehealth.efr_version.features.scan.browser.view_states.ScannerFragmentViewState
import com.flowehealth.efr_version.databinding.FragmentBrowserBinding
import com.flowehealth.efr_version.home_screen.activities.MainActivity
import com.flowehealth.efr_version.common.other.CardViewListDecoration
import com.flowehealth.efr_version.home_screen.base.BaseServiceDependentMainMenuFragment
import com.flowehealth.efr_version.home_screen.fragments.ScanFragment
import com.flowehealth.efr_version.home_screen.base.ViewPagerFragment
import com.flowehealth.efr_version.features.scan.browser.activities.DeviceServicesActivity
import com.flowehealth.efr_version.features.scan.browser.activities.UuidDictionaryActivity
import com.flowehealth.efr_version.home_screen.viewmodels.ScanFragmentViewModel
import com.flowehealth.efr_version.features.scan.browser.adapters.DebugModeCallback
import com.flowehealth.efr_version.home_screen.base.BluetoothDependent
import com.flowehealth.efr_version.home_screen.base.LocationDependent
import com.flowehealth.efr_version.home_screen.viewmodels.DeviceDataViewModel

import kotlinx.android.synthetic.main.fragment_browser.view.*

class BrowserFragment : BaseServiceDependentMainMenuFragment(),
        OnRefreshListener {

    private lateinit var scanViewModel: ScanFragmentViewModel
    private lateinit var dataViewModel: DeviceDataViewModel
    private lateinit var viewBinding: FragmentBrowserBinding
    private var bluetoothService: BluetoothService? = null

    private lateinit var sharedPrefUtils: SharedPrefUtils
    private var devicesAdapter: DebugModeDeviceAdapter? = null
    private lateinit var handler: Handler

    private var deviceToConnect: BluetoothDeviceInfo? = null
    private var blockConnectionAttempts = false

    override val bluetoothDependent = object : BluetoothDependent {

        override fun onBluetoothStateChanged(isBluetoothOn: Boolean) {
            toggleBluetoothBar(isBluetoothOn, viewBinding.bluetoothBar)
            viewBinding.btnScanning.isEnabled = isBluetoothOperationPossible()

            getScanFragment().setScanFragmentListener(scanFragmentListener)
            scanViewModel.let {
                it.setIsScanningOn(isBluetoothOperationPossible())
                if (!isBluetoothOn) {
                    it.reset()
                    it.shouldResetChart = true
                }
            }
        }

        override fun onBluetoothPermissionsStateChanged(arePermissionsGranted: Boolean) {
            toggleBluetoothPermissionsBar(arePermissionsGranted, viewBinding.bluetoothPermissionsBar)
            viewBinding.btnScanning.isEnabled = isBluetoothOperationPossible()

            scanViewModel.let {
                it.setIsScanningOn(isBluetoothOperationPossible())
                if (!arePermissionsGranted) {
                    it.reset()
                    it.shouldResetChart = true
                }
            }
        }

        override fun refreshBluetoothDependentUi(isBluetoothOperationPossible: Boolean) {
            /* Not needed */
        }

        override fun setupBluetoothPermissionsBarButtons() {
            viewBinding.bluetoothPermissionsBar.setFragmentManager(childFragmentManager)
        }
    }

    override val locationDependent = object : LocationDependent {

        override fun onLocationStateChanged(isLocationOn: Boolean) {
            toggleLocationBar(isLocationOn, viewBinding.locationBar)
        }
        override fun onLocationPermissionStateChanged(isPermissionGranted: Boolean) {
            viewBinding.apply {
                toggleLocationPermissionBar(isPermissionGranted, locationPermissionBar)
                btnScanning.isEnabled = isPermissionGranted
            }
        }
        override fun setupLocationBarButtons() {
            viewBinding.locationBar.setFragmentManager(childFragmentManager)
        }

        override fun setupLocationPermissionBarButtons() {
            viewBinding.locationPermissionBar.setFragmentManager(childFragmentManager)
        }
    }

    private val scanFragmentListener = object: ScanFragment.ScanFragmentListener{
        override fun onScanningStateChanged(isOn: Boolean) {
            toggleMainView(isOn, scanViewModel.isAnyDeviceDiscovered.value ?: false)
            toggleScanningButton(isOn)
            toggleRefreshInfoRunnable(isOn)

            if (!isOn) handler.removeCallbacks(refreshScanRunnable)
        }
    }


    private val refreshScanRunnable = Runnable {
        scanViewModel.let {
            if (!it.getIsScanningOn()) {
                it.setIsScanningOn(true)
            } else {
                getScanFragment().toggleScannerState(true)
            }
        }
    }

    private fun getScanFragment() = (parentFragment as ViewPagerFragment).getScanFragment()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        scanViewModel = getScanFragment().scanViewModel
        dataViewModel = getScanFragment().dataViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        viewBinding = FragmentBrowserBinding.inflate(inflater)
        hidableActionButton = viewBinding.btnScanning
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.fragment_scan_label)
        sharedPrefUtils = SharedPrefUtils(requireContext())
        handler = Handler(Looper.getMainLooper())

        observeChanges()
        setUiListeners()

        initFullPageInfoViews()
        initDevicesRecyclerView()
        initSwipeRefreshLayout()

        getScanFragment().setScanFragmentListener(scanFragmentListener) // for initial app enter

        bluetoothService?.apply {
            registerGattServerCallback(gattServerCallback)
            registerGattCallback(gattCallback)
        }
    }


    private fun initFullPageInfoViews() {
        viewBinding.lookingForDevicesBackground.apply {
            image.apply {
                setImageResource(R.drawable.redesign_ic_main_view_browser_scanning_spinner)
                (drawable as? AnimatedVectorDrawable)?.start()
            }
            textPrimary.text = getString(R.string.device_scanning_background_message)
            textSecondary.visibility = View.GONE
        }
    }

    private fun observeChanges() {
        scanViewModel.apply {
            filteredDevices.observe(viewLifecycleOwner) {
                devicesAdapter?.updateDevices(getBluetoothInfoViewsState())
            }
            isAnyDeviceDiscovered.observe(viewLifecycleOwner) {
                toggleMainView(getIsScanningOn(), it)
            }
            deviceToInsert.observe(viewLifecycleOwner) {
                devicesAdapter?.addNewDevice(it)
            }
            activeFiltersDescription.observe(viewLifecycleOwner) {
                toggleFilterDescriptionView(it)
            }
            activityViewModel?.isSetupFinished?.observe(viewLifecycleOwner) {
                if (it) {
                    bluetoothService = (activity as? MainActivity)?.bluetoothService
                    (parentFragment as ViewPagerFragment).setBluetoothService(bluetoothService)
                    viewBinding.btnScanning.visibility = View.VISIBLE
                    refreshViewState(scanViewModel.getScannerFragmentViewState())
                }
            }
        }
    }

    private fun toggleMainView(isScanningOn: Boolean, isAnyDeviceDiscovered: Boolean) {
        viewBinding.apply {
            if (isAnyDeviceDiscovered) {
                rvDebugDevices.visibility = View.VISIBLE
                lookingForDevicesBackground.root.visibility = View.GONE
            } else {
                rvDebugDevices.visibility = View.GONE
                lookingForDevicesBackground.apply {
                    root.visibility = View.VISIBLE
                    if (isScanningOn) {
                        image.setImageResource(R.drawable.redesign_ic_main_view_browser_scanning_spinner)
                        (image.drawable as AnimatedVectorDrawable).start()
                        textPrimary.text = getString(R.string.device_scanning_background_message)
                    } else {
                        image.setImageResource(R.drawable.graphic_loading)
                        textPrimary.text = getString(R.string.no_devices_found_title_copy)
                    }
                }
            }
        }
    }

    private fun toggleFilterDescriptionView(description: String?) {
        viewBinding.activeFiltersDescription.apply {
            description?.let {
                visibility = View.VISIBLE
                text = it
            } ?: run { visibility = View.GONE }
        }
    }

    private fun toggleRefreshInfoRunnable(isOn: Boolean) {
        handler.let {
            if (isOn) {
                it.removeCallbacks(updateScanInfoRunnable)
                it.postDelayed(updateScanInfoRunnable, SCAN_UPDATE_PERIOD)
            }
            else it.removeCallbacks(updateScanInfoRunnable)
        }
    }

    private val updateScanInfoRunnable = object : Runnable {
        override fun run() {
            devicesAdapter?.updateDevices(scanViewModel.getBluetoothInfoViewsState())
            handler.postDelayed(this, SCAN_UPDATE_PERIOD)
        }
    }

    private fun setUiListeners() {
        viewBinding.btnScanning.setOnClickListener { scanViewModel.toggleScanningState() }
    }

    override fun onResume() {
        super.onResume()
        bluetoothService?.apply {
            registerGattServerCallback(gattServerCallback)
            registerGattCallback(gattCallback)
        }

        if (activityViewModel?.getIsSetupFinished() == true) {
            scanViewModel.updateConnectionStates()
            getScanFragment().setScanFragmentListener(scanFragmentListener)
            refreshViewState(scanViewModel.getScannerFragmentViewState())
        }
    }

    private fun refreshViewState(viewState: ScannerFragmentViewState) {
        toggleMainView(viewState.isScanningOn, viewState.devicesToShow.isNotEmpty())
        toggleScanningButton(viewState.isScanningOn)
        devicesAdapter?.updateDevices(viewState.devicesToShow)
    }

    private fun toggleScanningButton(isScanningOn: Boolean) {
        viewBinding.btnScanning.apply {
            text = getString(
                    if (isScanningOn) R.string.button_stop_scanning
                    else R.string.button_start_scanning
            )
            setIsActionOn(isScanningOn)
        }
    }

    override fun onPause() {
        super.onPause()
        bluetoothService?.apply {
            unregisterGattServerCallback()
            unregisterGattCallback()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_browser, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                getScanFragment().toggleFilterFragment(shouldShowFilterFragment = true)
                true
            }
            R.id.menu_sort -> {
                scanViewModel.let { model ->
                    devicesAdapter?.let { adapter ->
                        model.sortDevices()
                        adapter.updateDevices(model.getBluetoothInfoViewsState(), withMoves = true)
                        viewBinding.rvDebugDevices.scrollToPosition(0)
                        Toast.makeText(requireContext(), getString(R.string.devices_sorted_by_descending_rssi),
                                Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.menu_uuid_dictionary -> {
                startActivity(Intent(requireContext(), UuidDictionaryActivity::class.java))
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRefresh() {
        if (isBluetoothOperationPossible() &&
                activityViewModel?.getIsLocationPermissionGranted() == true) {
            getScanFragment().toggleScannerState(false)

            scanViewModel.apply {
                reset()
                shouldResetChart = true
                setTimestamps()
            }

            toggleScanningButton(true)
            toggleMainView(isScanningOn = true, isAnyDeviceDiscovered = false)

            handler.removeCallbacks(refreshScanRunnable)
            handler.postDelayed(refreshScanRunnable, RESTART_SCAN_TIMEOUT)
        } else {
            if (activityViewModel?.getIsBluetoothOn() != true) {
                Toast.makeText(requireContext(), getString(R.string.bluetooth_disabled), Toast.LENGTH_SHORT).show()
            } else if (activityViewModel?.getAreBluetoothPermissionsGranted() != true) {
                Toast.makeText(requireContext(), getString(R.string.bluetooth_permissions_denied), Toast.LENGTH_SHORT).show()
            } else if (activityViewModel?.getIsLocationPermissionGranted() != true) {
                Toast.makeText(requireContext(), getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
        viewBinding.swipeRefreshContainer.post { viewBinding.swipeRefreshContainer.isRefreshing = false }
    }

    private fun initDevicesRecyclerView() {
        devicesAdapter = DebugModeDeviceAdapter(mutableListOf(), debugModeCallback)
        viewBinding.rvDebugDevices.apply {
            layoutManager = getLayoutManagerWithHidingUIElements(requireContext())
            addItemDecoration(CardViewListDecoration())
            adapter = devicesAdapter
        }
    }

    private fun initSwipeRefreshLayout() {
        viewBinding.swipeRefreshContainer.apply {
            setOnRefreshListener(this@BrowserFragment)
            setColorSchemeColors(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
            )
        }
    }

    private fun showConnectingAnimation() {
        activity?.runOnUiThread {
            viewBinding.apply {
                btnScanning.visibility = View.GONE
                flyInBar.visibility = View.VISIBLE
                flyInBar.startFlyInAnimation(getString(R.string.debug_mode_device_selection_connecting_bar))
            }
        }
    }

    private fun hideConnectingAnimation() {
        activity?.runOnUiThread {
            devicesAdapter?.notifyDataSetChanged()
            viewBinding.apply {
                flyInBar.clearBarAnimation()
                flyInBar.visibility = View.GONE
                btnScanning.visibility = View.VISIBLE
            }
        }
    }

    private val gattCallback = object : TimeoutGattCallback() {
        override fun onTimeout() {
            Toast.makeText(requireContext(), getString(R.string.toast_connection_timed_out), Toast.LENGTH_SHORT).show()
            hideConnectingAnimation()
            deviceToConnect = null
        }

        override fun onMaxRetriesExceeded(gatt: BluetoothGatt) {
            handleDisconnection(gatt, status = 133)
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            activity?.runOnUiThread {
                scanViewModel.updateActiveConnections(bluetoothService?.getActiveConnections())
            }

            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    if (status == BluetoothGatt.GATT_SUCCESS) handleSuccessfulConnection(gatt)
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    when (status) {
                        133 -> {
                            (activity as MainActivity).showMessage(R.string.connection_failed_reconnecting)
                            showConnectingAnimation()
                        }
                        else -> handleDisconnection(gatt, status)
                    }
                }
            }
        }

        @SuppressLint("LogNotTimber")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)


        }

    }

    private fun handleSuccessfulConnection(gatt: BluetoothGatt) {
        hideConnectingAnimation()
        if (bluetoothService?.isGattConnected(deviceToConnect?.address) == true) {
            deviceToConnect?.let { bluetoothService?.updateConnectionInfo(it) }
            scanViewModel.setDeviceConnectionState(gatt.device.address, BluetoothDeviceInfo.ConnectionState.CONNECTED)
            handler.removeCallbacks(startActivityRunnable)
            handler.postDelayed(startActivityRunnable, START_ACTIVITY_DELAY)
            blockConnectionAttempts = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleDisconnection(gatt: BluetoothGatt, status: Int) {
        hideConnectingAnimation()
        scanViewModel.setDeviceConnectionState(gatt.device.address, BluetoothDeviceInfo.ConnectionState.DISCONNECTED)
        activity?.runOnUiThread {
            val deviceName = if (TextUtils.isEmpty(gatt.device.name)) getString(R.string.not_advertising_shortcut) else gatt.device.name
            if (deviceToConnect != null && deviceToConnect?.address!! == gatt.device.address) {
                deviceToConnect = null
                Toast.makeText(requireContext(), getFailedConnectingToDeviceMessage(deviceName, status), Toast.LENGTH_LONG).show()
            } else {
                (activity as MainActivity).showLongMessage(
                        if (status == 0) getString(R.string.device_disconnected_successfully, getDeviceName(gatt))
                        else getDeviceDisconnectedMessage(deviceName, status)
                )
            }
        }
    }

    private val startActivityRunnable = Runnable {
        deviceToConnect?.let {
//            val navController = findNavController(this)
//            navController.navigate(R.id.demoFragment)
//            Intent(requireContext(), DeviceServicesActivity::class.java).apply {
//                putExtra(DeviceServicesActivity.CONNECTED_DEVICE, it.device)
//            }.also {
//                activityResultCallback.launch(it)
//            }
        }
        deviceToConnect = null
        blockConnectionAttempts = false
    }

    private val activityResultCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == DeviceServicesActivity.REFRESH_INFO_RESULT_CODE) {
                result.data?.let { intent ->
                    val device = intent.getParcelableExtra<BluetoothDevice>(
                            DeviceServicesActivity.CONNECTED_DEVICE)
                    val connectionState = intent.getIntExtra(
                            DeviceServicesActivity.CONNECTION_STATE,
                            BluetoothGatt.STATE_DISCONNECTED)
                    device?.let { scanViewModel.refreshConnectedDeviceInfo(device, connectionState) }
                }
            }
        }

    /* Getting connection from other device when on BrowserFragment */
    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                if (bluetoothService?.isAnyConnectionPending() == false && !blockConnectionAttempts) {
                    deviceToConnect = BluetoothDeviceInfo(device)
                    bluetoothService?.connectGatt(device, requestRssiUpdates = true)
                }
            }
        }
    }

    private val debugModeCallback = object : DebugModeCallback {
        override fun connectToDevice(position: Int, deviceInfo: BluetoothDeviceInfo) {
            if (scanViewModel.getIsScanningOn()) {
                scanViewModel.setIsScanningOn(false)
            }

            scanViewModel.setDeviceConnectionState(position, connectionState = BluetoothDeviceInfo.ConnectionState.CONNECTING)
            deviceToConnect = deviceInfo
            showConnectingAnimation()

            handler.postDelayed({
                bluetoothService?.let {
                    it.isNotificationEnabled = false
                    bluetoothService?.connectGatt(deviceInfo.device, true, gattCallback)
                }
            }, ANIMATION_DELAY)
        }

        override fun disconnectDevice(position: Int, device: BluetoothDevice) {
            scanViewModel.setDeviceConnectionState(position, connectionState = BluetoothDeviceInfo.ConnectionState.DISCONNECTED)
            bluetoothService?.disconnectGatt(device.address)
        }

        override fun addToFavorites(deviceAddress: String) {
            scanViewModel.toggleIsFavorite(deviceAddress)
            sharedPrefUtils.addDeviceToFavorites(deviceAddress)
        }

        override fun removeFromFavorites(deviceAddress: String) {
            scanViewModel.toggleIsFavorite(deviceAddress)
            sharedPrefUtils.removeDeviceFromFavorites(deviceAddress)
        }


        override fun toggleViewExpansion(position: Int) {
            scanViewModel.toggleViewExpansion(position)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceName(gatt: BluetoothGatt) : String {
        return gatt.device.name ?: getString(R.string.not_advertising_shortcut)
    }


    companion object {
        private const val RESTART_SCAN_TIMEOUT = 1000L
        private const val SCAN_UPDATE_PERIOD = 1000L //ms
        private const val START_ACTIVITY_DELAY = 250L
        private const val ANIMATION_DELAY = 1000L // give time to display animation
    }
}
