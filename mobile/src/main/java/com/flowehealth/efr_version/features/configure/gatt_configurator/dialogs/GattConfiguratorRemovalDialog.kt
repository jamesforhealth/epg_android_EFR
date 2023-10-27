package com.flowehealth.efr_version.features.configure.gatt_configurator.dialogs

import androidx.annotation.StringRes
import com.flowehealth.efr_version.utils.RemovalDialog
import com.flowehealth.efr_version.features.configure.gatt_configurator.utils.GattConfiguratorStorage

class GattConfiguratorRemovalDialog(
    @StringRes name: Int,
    onOkClicked: () -> Unit
) : RemovalDialog(name, onOkClicked) {

    override fun blockDisplayingRemovalDialog() {
        context?.let { GattConfiguratorStorage(it).setDisplayRemovalDialog(false) }
    }
}
