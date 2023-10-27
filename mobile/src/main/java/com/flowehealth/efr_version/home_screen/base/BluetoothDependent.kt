package com.flowehealth.efr_version.home_screen.base

interface BluetoothDependent {

    fun onBluetoothStateChanged(isBluetoothOn: Boolean)
    fun onBluetoothPermissionsStateChanged(arePermissionsGranted: Boolean)
    fun refreshBluetoothDependentUi(isBluetoothOperationPossible: Boolean)
    fun setupBluetoothPermissionsBarButtons()
}