package com.flowehealth.efr_version.features.scan.browser.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.utils.SharedPrefUtils
import kotlinx.android.synthetic.main.dialog_info_ok_cancel.view.*

class ManualUnbondDeviceDialog(val callback: Callback) : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_info_ok_cancel, container, false).apply {

            tv_dialog_title.text = context.getString(R.string.device_services_title_unbond_device_manual)
            tv_dialog_content.text = context.getString(R.string.device_services_note_unbond_device_manual)
            btn_ok.text = context.getString(R.string.button_proceed)

            btn_ok.setOnClickListener {
                if (cb_dont_show_again.isChecked) SharedPrefUtils(requireContext()).setShouldDisplayManualUnbondDeviceDialog(false)
                callback.onOkClicked()
                dismiss()
            }
            btn_cancel.setOnClickListener { dismiss() }
        }
    }

    interface Callback {
        fun onOkClicked()
    }
}
