package com.flowehealth.efr_version.home_screen.views

import android.content.Context
import android.util.AttributeSet
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.home_screen.dialogs.WarningBarInfoDialog

class BluetoothPermissionsBar(context: Context, attrs: AttributeSet?) : NoServiceWarningBar(context, attrs) {

    override fun initTexts() {
        _binding.apply { with(context) {
            warningBarMessage.text = getString(R.string.bluetooth_permissions_denied)
            warningBarActionButton.text = getString(R.string.action_settings)
            warningBarInfoButton.text = getString(R.string.warning_bar_additional_info)
        } }
    }

    override fun initClickListeners() {
        _binding.apply {
            warningBarActionButton.setOnClickListener {
                showAppSettingsScreen()
            }
            warningBarInfoButton.setOnClickListener {
                showInfoDialog(WarningBarInfoDialog.Type.BLUETOOTH_PERMISSIONS)
            }
        }
    }
}