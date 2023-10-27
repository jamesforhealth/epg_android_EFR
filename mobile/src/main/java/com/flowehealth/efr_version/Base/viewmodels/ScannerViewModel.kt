package com.flowehealth.efr_version.base.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flowehealth.efr_version.bluetooth.ble.BluetoothDeviceInfo
import com.flowehealth.efr_version.bluetooth.ble.ScanResultCompat
import com.flowehealth.efr_version.bluetooth.parsing.ScanRecordParser

abstract class ScannerViewModel : ViewModel() {


    protected val _isAnyDeviceDiscovered: MutableLiveData<Boolean> = MutableLiveData()
    val isAnyDeviceDiscovered: LiveData<Boolean> = _isAnyDeviceDiscovered
    private val _isScanningOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val isScanningOn: LiveData<Boolean> = _isScanningOn


    abstract fun handleScanResult(result: ScanResultCompat)


    fun toggleScanningState() {
        _isScanningOn.value = _isScanningOn.value?.not() ?: false
    }

    fun getIsScanningOn() = _isScanningOn.value ?: false

    fun setIsScanningOn(isOn: Boolean) {
        if (_isScanningOn.value == true && isOn) {
            /* Don't start scanner twice */
            return
        }

        _isScanningOn.value = isOn
    }

    protected fun updateScanInfo(currentInfo: BluetoothDeviceInfo, result: ScanResultCompat) : BluetoothDeviceInfo {
        return currentInfo.apply {
            device = result.device!!
            scanInfo = result
            bleFormat = getBleFormat(shouldCheckAgain = true)
            rawData = ScanRecordParser.getRawAdvertisingDate(result.scanRecord?.bytes)
            isConnectable = result.isConnectable
            count++
            if (timestampLast != 0L) {
                setIntervalIfLower(result.timestampNanos - timestampLast)
            }
            timestampLast = result.timestampNanos
        }
    }

}