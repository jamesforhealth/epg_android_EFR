package com.flowehealth.efr_version.home_screen.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.databinding.DialogScanTimeoutHelpBinding

class ScanTimeoutHelpDialog : BaseDialogFragment(
    hasCustomWidth = true,
    isCanceledOnTouchOutside = true,
) {

    private val binding by viewBinding(DialogScanTimeoutHelpBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.dialog_scan_timeout_help, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener { dismiss() }
    }
}