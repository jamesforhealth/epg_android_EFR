package com.flowehealth.efr_version.bluetooth.ble

class ConnectedDeviceInfo(val connection: GattConnection) {
    var bluetoothInfo = BluetoothDeviceInfo(connection.gatt!!.device)
}