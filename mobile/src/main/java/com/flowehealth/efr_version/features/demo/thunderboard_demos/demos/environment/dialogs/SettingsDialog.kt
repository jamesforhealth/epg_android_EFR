package com.flowehealth.efr_version.features.demo.thunderboard_demos.demos.environment.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.databinding.DialogSettingsBinding
import com.flowehealth.efr_version.features.demo.thunderboard_demos.demos.environment.model.TemperatureScale
import com.flowehealth.efr_version.features.demo.thunderboard_demos.demos.environment.utils.PreferenceManager
import kotlinx.android.synthetic.main.dialog_settings.*

class SettingsDialog(
        context: Context,
        private val settingsHandler: SettingsHandler
) : BaseDialogFragment(
        hasCustomWidth = true,
        isCanceledOnTouchOutside = true
) {

    private val prefsManager = PreferenceManager(context)

    private lateinit var _binding: DialogSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogSettingsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPersonalize()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        handleSave()
    }

    private fun handleSave() {
        saveSettings()
        settingsHandler.onSettingsSaved(prefsManager.retrievePreferences())
    }

    private fun loadPersonalize() {
        prefsManager.preferences.let {
            if (it.scale == TemperatureScale.CELSIUS) {
                temperature_toggle.check(R.id.celsius)
            } else if (it.scale == TemperatureScale.FAHRENHEIT) {
                temperature_toggle.check(R.id.fahrenheit)
            }
        }
    }

    private fun saveSettings() {
        prefsManager.preferences.let {
            it.scale =
                    if (temperature_toggle.checkedRadioButtonId == R.id.celsius) TemperatureScale.CELSIUS
                    else TemperatureScale.FAHRENHEIT
            prefsManager.savePreferences(it)
        }
    }

    interface SettingsHandler {
        fun onSettingsSaved(scale: TemperatureScale)
    }
}