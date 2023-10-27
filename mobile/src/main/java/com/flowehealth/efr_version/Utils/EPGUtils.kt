package com.flowehealth.efr_version.utils
import android.annotation.SuppressLint
import android.util.Log
import com.flowehealth.efr_version.BuildConfig.TAG
import com.flowehealth.efr_version.home_screen.viewmodels.DeviceDataViewModel

@SuppressLint("LogNotTimber")
fun handlePacket(data: ByteArray, viewModel: DeviceDataViewModel, address: String){
    for (index in data.indices step 2) {
        if (index + 1 < data.size) {
            // convert bytes to unsigned
            // notice the order has changed due to LSB first
            val rawData = mutableListOf<Double>()
            val bytes: UShort = ((data[index + 1].toUByte().toInt() shl 8) or (data[index].toUByte().toInt())).toUShort()
            var voltage = bytes.toDouble() / 1000
            voltage *= -1.0
            rawData.add(voltage)
            viewModel.addRawData(address, rawData )
            Log.d(TAG, "rawData: $rawData")
        }
    }
}