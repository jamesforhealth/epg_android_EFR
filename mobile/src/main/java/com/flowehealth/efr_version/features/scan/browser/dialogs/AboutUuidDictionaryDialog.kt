package com.flowehealth.efr_version.features.scan.browser.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.R
import kotlinx.android.synthetic.main.dialog_about_uuid_dictionary.*

class AboutUuidDictionaryDialog : BaseDialogFragment(
        hasCustomWidth = true,
        isCanceledOnTouchOutside = true
) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_about_uuid_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_ok.setOnClickListener { dismiss() }
    }

}
