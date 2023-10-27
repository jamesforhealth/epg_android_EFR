package com.flowehealth.efr_version.bluetooth.ble.values

import android.bluetooth.BluetoothGattCharacteristic

interface ValueFactory<T> {
    fun create(value: BluetoothGattCharacteristic): T
}