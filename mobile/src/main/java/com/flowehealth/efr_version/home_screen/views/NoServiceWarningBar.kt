package com.flowehealth.efr_version.home_screen.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.flowehealth.efr_version.databinding.NoServiceWarningBarBinding
import com.flowehealth.efr_version.home_screen.dialogs.WarningBarInfoDialog

abstract class NoServiceWarningBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    protected val _binding = NoServiceWarningBarBinding.inflate(LayoutInflater.from(context), this, true)
    private var fragmentManager: FragmentManager? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        initTexts()
        initClickListeners()
    }

    abstract fun initTexts()
    abstract fun initClickListeners()

    protected fun showAppSettingsScreen() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .apply { data = Uri.fromParts("package", context.packageName, null) }
            .also { context.startActivity(it) }
    }

    protected fun showInfoDialog(type: WarningBarInfoDialog.Type) {
        fragmentManager?.let {
            WarningBarInfoDialog(type).show(it, "warning_bar_info_dialog")
        }
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager
    }
}