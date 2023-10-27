package com.flowehealth.efr_version.home_screen.menu_items

import androidx.annotation.DrawableRes
import com.flowehealth.efr_version.bluetooth.services.BluetoothService

class EslDemo(
    @DrawableRes imageResId: Int,
    title: String,
    description: String
) : DemoMenuItem(imageResId, title, description
) {

    override val connectType: BluetoothService.GattConnectType = BluetoothService.GattConnectType.ESL_DEMO
}