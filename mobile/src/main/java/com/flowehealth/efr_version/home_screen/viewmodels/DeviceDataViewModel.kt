package com.flowehealth.efr_version.home_screen.viewmodels
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.concurrent.ConcurrentLinkedQueue

class DeviceDataViewModel  : ViewModel() {
    // LiveData，用於保存從 BLE 裝置接收到的資料, 存於每個address對應的queue中
    private val deviceDataMap: MutableLiveData<MutableMap<String, ConcurrentLinkedQueue<List<Double>>>> = MutableLiveData(mutableMapOf())

    //add device to map
    fun addDevice(deviceAddress: String) {
        val currentMap = deviceDataMap.value ?: mutableMapOf()
        if (currentMap.containsKey(deviceAddress)) {
            return
        }
        currentMap[deviceAddress] = ConcurrentLinkedQueue<List<Double>>()
        deviceDataMap.value = currentMap
    }

    //delete device from map
    fun deleteDevice(deviceAddress: String) {
        val currentMap = deviceDataMap.value ?: mutableMapOf()
        if (!currentMap.containsKey(deviceAddress)) {
            return
        }
        currentMap.remove(deviceAddress)
        deviceDataMap.value = currentMap
    }

    // push data to device's queue
    fun addRawData(deviceAddress: String, data: List<Double>) {
        val currentMap = deviceDataMap.value ?: mutableMapOf()
        val currentQueue = currentMap[deviceAddress] ?: ConcurrentLinkedQueue()
        currentQueue.offer(data)
    }

    //pop data from device's queue
    fun consumeRawData(deviceAddress: String): List<Double>? {
        val currentMap = deviceDataMap.value ?: mutableMapOf()
        val currentQueue = currentMap[deviceAddress] ?: ConcurrentLinkedQueue()
        return currentQueue.poll()
    }

}
