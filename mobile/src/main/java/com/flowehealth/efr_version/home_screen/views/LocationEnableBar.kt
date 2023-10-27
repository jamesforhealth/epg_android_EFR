package com.flowehealth.efr_version.home_screen.views

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.AttributeSet
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.home_screen.dialogs.WarningBarInfoDialog

class LocationEnableBar(context: Context, attrs: AttributeSet) : NoServiceWarningBar(context, attrs) {


    override fun initTexts() {
        _binding.apply { with(context) {
            warningBarMessage.text = getString(R.string.location_disabled)
            warningBarActionButton.text = getString(R.string.action_settings)
            warningBarInfoButton.text = getString(R.string.warning_bar_additional_info)
        } }
    }

    override fun initClickListeners() {
        _binding.apply {
            warningBarActionButton.setOnClickListener {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            warningBarInfoButton.setOnClickListener {
                showInfoDialog(WarningBarInfoDialog.Type.LOCATION)
            }
        }
    }

}