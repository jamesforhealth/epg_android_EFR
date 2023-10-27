package com.flowehealth.efr_version.home_screen.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.flowehealth.efr_version.base.activities.BaseActivity
import com.flowehealth.efr_version.bluetooth.services.BluetoothService
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.home_screen.dialogs.PermissionsDialog
import com.flowehealth.efr_version.home_screen.viewmodels.DeviceDataViewModel
import com.flowehealth.efr_version.home_screen.viewmodels.MainActivityViewModel
import com.flowehealth.efr_version.home_screen.views.HidableBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
open class MainActivity : BaseActivity(),
        BluetoothService.ServicesStateListener
{
    private lateinit var dataViewModel: DeviceDataViewModel
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var binding: BluetoothService.Binding
    var bluetoothService: BluetoothService? = null
        private set

    private val neededPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val android12Permissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainAppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.show()

        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DeviceDataViewModel::class.java)

        handlePermissions()
        setupMainNavigationListener()
    }

    override fun onResume() {
        super.onResume()
        if (mainViewModel.getIsSetupFinished()) {
            mainViewModel.setIsLocationPermissionGranted(isPermissionGranted(neededPermissions[0]))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mainViewModel.setAreBluetoothPermissionsGranted(areBluetoothPermissionsGranted())
            }
        }
    }

    private fun setupMainNavigationListener() {
        val navFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        val navController = navFragment.navController
        NavigationUI.setupWithNavController(main_navigation, navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun toggleMainNavigation(isOn: Boolean) {
        if(isOn) {
            main_navigation.show(instant = true)
        } else {
            main_navigation.hide(instant = true)
        }
    }

    fun toggleHomeIcon(isOn: Boolean) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(isOn)
            if (isOn) setHomeAsUpIndicator(R.drawable.redesign_ic_close)
        }
    }

    private fun bindBluetoothService() {
        binding = object : BluetoothService.Binding(this) {
            override fun onBound(service: BluetoothService?) {
                this@MainActivity.bluetoothService = service
                bluetoothService?.servicesStateListener = this@MainActivity
                setServicesInitialState()
            }
        }
        binding.bind()
    }

    private fun setServicesInitialState() {
        mainViewModel.setIsLocationPermissionGranted(isPermissionGranted(neededPermissions[0]))
        mainViewModel.setAreBluetoothPermissionsGranted(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) areBluetoothPermissionsGranted()
            else true /* No runtime permissions needed for bluetooth operation in Android 11- */
        )
        bluetoothService?.let {
            mainViewModel.setIsBluetoothOn(it.isBluetoothOn())
            mainViewModel.setIsLocationOn(it.isLocationOn())

            it.setViewModelfromMain(dataViewModel)
        }
        observeChanges()
        mainViewModel.setIsSetupFinished(isSetupFinished = true)
    }

    private fun observeChanges() {
        mainViewModel.areBluetoothPermissionGranted.observe(this) {
            bluetoothService?.setAreBluetoothPermissionsGranted(
                mainViewModel.getAreBluetoothPermissionsGranted())
        }
    }

    fun getMainNavigation(): HidableBottomNavigationView? {
        return main_navigation
    }

    override fun onBluetoothStateChanged(isOn: Boolean) {
        mainViewModel.setIsBluetoothOn(isOn)
    }

    override fun onLocationStateChanged(isOn: Boolean) {
        mainViewModel.setIsLocationOn(isOn)
    }


    private fun handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            neededPermissions.addAll(android12Permissions)
        }

        if (neededPermissions.any { !isPermissionGranted(it) }) askForPermissions()
        else bindBluetoothService()
    }

    private fun askForPermissions() {
        val rationalesToShow = neededPermissions.filter { shouldShowRequestPermissionRationale(it) }
        val permissionsToRequest = neededPermissions.toTypedArray()

        if (rationalesToShow.isNotEmpty()) {
            PermissionsDialog(rationalesToShow, object : PermissionsDialog.Callback {
                override fun onDismiss() {
                    requestPermissions(permissionsToRequest, PERMISSIONS_REQUEST_CODE)
                }
            }).show(supportFragmentManager, "permissions_dialog")
        } else {
            requestPermissions(permissionsToRequest, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun isPermissionGranted(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun areBluetoothPermissionsGranted() : Boolean {
        return android12Permissions.all { isPermissionGranted(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> bindBluetoothService()
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 400
        // private const val IMPORT_EXPORT_CODE_VERSION = 20
    }
//TODO: handle migration. See BTAPP-1285 for clarification.
/*
    private fun migrateGattDatabaseIfNeeded() {
        if (BuildConfig.VERSION_CODE <= IMPORT_EXPORT_CODE_VERSION - 1) {
            Migrator(this).migrate()
        }
    }
*/

}