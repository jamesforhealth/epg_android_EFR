package com.flowehealth.efr_version.home_screen.menu_items

import com.flowehealth.efr_version.bluetooth.services.BluetoothService

class ConnectedLighting(imageResId: Int, title: String, description: String) : DemoMenuItem(imageResId, title, description) {

    override val connectType = BluetoothService.GattConnectType.LIGHT
}