package com.flowehealth.efr_version.features.demo.thunderboard_demos.base.models

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile

@SuppressLint("MissingPermission")
class ThunderBoardDevice(device: BluetoothDevice) {
    var name: String = device.name
    var batteryLevel = 0
    var powerSource = PowerSource.UNKNOWN
    var modelNumber: String? = null
    var firmwareVersion: String? = null

    var state = BluetoothProfile.STATE_DISCONNECTED

    val boardType: Type
        get() = when (modelNumber) {
            THUNDERBOARD_MODEL_SENSE -> Type.THUNDERBOARD_SENSE
            THUNDERBOARD_MODEL_BLUE_V1,
            THUNDERBOARD_MODEL_BLUE_V2 -> Type.THUNDERBOARD_BLUE
            THUNDERBOARD_MODEL_DEV_KIT_V1,
            THUNDERBOARD_MODEL_DEV_KIT_V2 -> Type.THUNDERBOARD_DEV_KIT
            else -> Type.UNKNOWN
        }

    enum class PowerSource(val value: Int) {
        USB(1),
        COIN_CELL(4),
        UNKNOWN(0);

        companion object {
            fun fromInt(code: Int) : PowerSource {
                for (source in values()) {
                    if (source.value == code ) return source
                }
                return UNKNOWN
            }
        }
    }

    enum class Type {
        THUNDERBOARD_SENSE,
        THUNDERBOARD_BLUE,
        THUNDERBOARD_DEV_KIT,
        UNKNOWN
    }

    companion object {
        const val THUNDERBOARD_MODEL_SENSE = "BRD4166A"
        const val THUNDERBOARD_MODEL_BLUE_V1 = "BRD4184A"
        const val THUNDERBOARD_MODEL_BLUE_V2 = "BRD4184B"
        const val THUNDERBOARD_MODEL_DEV_KIT_V1 = "BRD2601A"
        const val THUNDERBOARD_MODEL_DEV_KIT_V2 = "BRD2601B"
    }

}