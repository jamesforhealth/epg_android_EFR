package com.flowehealth.efr_version.bluetooth.beacon_utils.altbeacon

import com.flowehealth.efr_version.bluetooth.beacon_utils.BleFormat
import com.flowehealth.efr_version.bluetooth.ble.BluetoothDeviceInfo

class AltBeacon(deviceInfo: BluetoothDeviceInfo) {
    var manufacturerId: String
    var altBeaconId: String
    var altBeaconReferenceRssi: Byte
    var deviceAddress: String = deviceInfo.address
    var rssi: Int = deviceInfo.rssi

    init {
        manufacturerId = parseManufacturerId(deviceInfo)
        altBeaconId = parseBeaconId(deviceInfo)
        altBeaconReferenceRssi = parseBeaconReferenceRssi(deviceInfo)
    }

    private fun parseManufacturerId(deviceInfo: BluetoothDeviceInfo): String {
        val bytes = deviceInfo.scanInfo?.scanRecord?.bytes
        val mfgIdBytes = bytes?.copyOfRange(2, 4)!!

        // reverse the order of the bytes, data received in little endian
        val lessSignificant = mfgIdBytes[0]
        mfgIdBytes[0] = mfgIdBytes[1]
        mfgIdBytes[1] = lessSignificant
        val mfgId = BleFormat.bytesToHex(mfgIdBytes)
        return "0x$mfgId"
    }

    private fun parseBeaconId(deviceInfo: BluetoothDeviceInfo): String {
        val bytes = deviceInfo.scanInfo?.scanRecord?.bytes
        val beaconIdBytes = bytes?.copyOfRange(6,26)!!
        val beaconId = BleFormat.bytesToHex(beaconIdBytes)
        return "0x$beaconId"
    }

    private fun parseBeaconReferenceRssi(deviceInfo: BluetoothDeviceInfo): Byte {
        val bytes = deviceInfo.scanInfo?.scanRecord?.bytes!!
        return bytes[26]
    }
}