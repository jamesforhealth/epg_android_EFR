package com.flowehealth.efr_version.features.scan.browser.adapters

import android.bluetooth.BluetoothDevice
import com.flowehealth.efr_version.bluetooth.ble.BluetoothDeviceInfo

interface DebugModeCallback {
    fun connectToDevice(position: Int, deviceInfo: BluetoothDeviceInfo)
    fun disconnectDevice(position: Int, device: BluetoothDevice)
    fun addToFavorites(deviceAddress: String)
    fun removeFromFavorites(deviceAddress: String)
    fun toggleViewExpansion(position: Int)
}
