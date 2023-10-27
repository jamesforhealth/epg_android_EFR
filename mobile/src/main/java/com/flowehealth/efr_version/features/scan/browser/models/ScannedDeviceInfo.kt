package com.flowehealth.efr_version.features.scan.browser.models

import android.bluetooth.BluetoothDevice
import com.flowehealth.efr_version.bluetooth.ble.BluetoothDeviceInfo

data class ScannedDeviceInfo(
        var bluetoothInfo: BluetoothDeviceInfo,
        val graphInfo: GraphInfo,
        var isBluetoothInfoExpanded: Boolean = false
) {

    constructor(device: BluetoothDevice,
                isFavorite: Boolean,
                graphDataColor: Int
    ) : this(
            BluetoothDeviceInfo(device, isFavorite),
            GraphInfo(mutableListOf(), graphDataColor)
    )

}