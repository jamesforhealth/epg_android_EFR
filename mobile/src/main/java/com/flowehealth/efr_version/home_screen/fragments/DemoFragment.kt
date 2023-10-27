package com.flowehealth.efr_version.home_screen.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.flowehealth.efr_version.BuildConfig.TAG
import com.flowehealth.efr_version.home_screen.dialogs.SelectDeviceDialog
import com.flowehealth.efr_version.home_screen.adapters.DemoAdapter
import com.flowehealth.efr_version.home_screen.menu_items.*
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.bluetooth.services.BluetoothService
import com.flowehealth.efr_version.databinding.FragmentDemoBinding
import com.flowehealth.efr_version.features.demo.range_test.activities.RangeTestActivity
import com.flowehealth.efr_version.home_screen.base.BaseServiceDependentMainMenuFragment
import com.flowehealth.efr_version.home_screen.base.BluetoothDependent
import com.flowehealth.efr_version.home_screen.base.LocationDependent
import com.flowehealth.efr_version.home_screen.viewmodels.DeviceDataViewModel

class DemoFragment : BaseServiceDependentMainMenuFragment(), DemoAdapter.OnDemoItemClickListener, DialogInterface.OnDismissListener {

    private val binding by viewBinding(FragmentDemoBinding::bind)
    private var demoAdapter: DemoAdapter? = null
    private val list: ArrayList<DemoMenuItem> = ArrayList()
    private var selectDeviceDialog: SelectDeviceDialog? = null
    private lateinit var dataViewModel: DeviceDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        list.apply {
            add(EPG(R.drawable.redesign_ic_demo_range_test, "EPG Raw Charts", "See the EPG data from our device"))
            add(HealthThermometer(R.drawable.redesign_ic_demo_health_thermometer, getString(R.string.title_Health_Thermometer), getString(R.string.main_menu_description_thermometer)))
            add(ConnectedLighting(R.drawable.redesign_ic_demo_connected_lighting, getString(R.string.title_Connected_Lighting), getString(R.string.main_menu_description_connected_lighting)))
            add(RangeTest(R.drawable.redesign_ic_demo_range_test, getString(R.string.title_Range_Test), getString(R.string.main_menu_description_range_test)))
            add(Blinky(R.drawable.redesign_ic_demo_blinky, getString(R.string.title_Blinky), getString(R.string.main_menu_description_blinky)))
            add(Throughput(R.drawable.redesign_ic_demo_throughput, getString(R.string.title_Throughput), getString(R.string.main_menu_description_throughput)))
            add(Motion(R.drawable.redesign_ic_demo_motion, getString(R.string.motion_demo_title), getString(R.string.motion_demo_description)))
            add(Environment(R.drawable.redesign_ic_demo_environment, getString(R.string.environment_demo_title), getString(R.string.environment_demo_description)))
            add(WifiCommissioning(R.drawable.redesign_ic_demo_wifi_commissioning, getString(R.string.wifi_commissioning_label), getString(R.string.wifi_commissioning_description)))
            add(EslDemo(R.drawable.redesign_ic_demo_esl, getString(R.string.demo_item_title_esl_demo), getString(R.string.demo_item_description_esl_demo)))
        }
//        dataViewModel = ViewModelProvider(this)[DeviceDataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_demo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.main_navigation_demo_title)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        demoAdapter = DemoAdapter(list, this@DemoFragment)
        binding.rvDemoMenu.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = demoAdapter
        }
        demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
    }

    override val bluetoothDependent = object : BluetoothDependent {

        override fun onBluetoothStateChanged(isBluetoothOn: Boolean) {
            toggleBluetoothBar(isBluetoothOn, binding.bluetoothBar)
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
            if (!isBluetoothOn) selectDeviceDialog?.dismiss()
        }
        override fun onBluetoothPermissionsStateChanged(arePermissionsGranted: Boolean) {
            toggleBluetoothPermissionsBar(arePermissionsGranted, binding.bluetoothPermissionsBar)
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
            if (!arePermissionsGranted) selectDeviceDialog?.dismiss()
        }
        override fun refreshBluetoothDependentUi(isBluetoothOperationPossible: Boolean) {
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible)
        }
        override fun setupBluetoothPermissionsBarButtons() {
            binding.bluetoothPermissionsBar.setFragmentManager(childFragmentManager)
        }
    }

    override val locationDependent = object : LocationDependent {

        override fun onLocationStateChanged(isLocationOn: Boolean) {
            toggleLocationBar(isLocationOn, binding.locationBar)
        }
        override fun onLocationPermissionStateChanged(isPermissionGranted: Boolean) {
            toggleLocationPermissionBar(isPermissionGranted, binding.locationPermissionBar)
            demoAdapter?.toggleItemsEnabled(isPermissionGranted)
            if (!isPermissionGranted) selectDeviceDialog?.dismiss()
        }
        override fun setupLocationBarButtons() {
            binding.locationBar.setFragmentManager(childFragmentManager)
        }

        override fun setupLocationPermissionBarButtons() {
            binding.locationPermissionBar.setFragmentManager(childFragmentManager)
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onDemoItemClicked(demoItem: DemoMenuItem) {
        if (demoItem.connectType == BluetoothService.GattConnectType.RANGE_TEST) {
            startActivity(Intent(requireContext(), RangeTestActivity::class.java))
        } else {
            Log.d(TAG, "onDemoItemClicked: demoItem.connectType = ${demoItem.connectType}, demoItem: $demoItem")
            selectDeviceDialog = SelectDeviceDialog.newDialog(demoItem.connectType)
            selectDeviceDialog?.show(childFragmentManager, "select_device_dialog")
        }
//        EPG-related device/activities only

    }

    override fun onDismiss(dialog: DialogInterface) {
        selectDeviceDialog = null
    }
}