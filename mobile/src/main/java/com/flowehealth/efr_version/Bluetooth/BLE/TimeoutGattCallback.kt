package com.flowehealth.efr_version.bluetooth.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback

abstract class TimeoutGattCallback : BluetoothGattCallback() {
    open fun onTimeout() {}
    open fun onMaxRetriesExceeded(gatt: BluetoothGatt) {}
}