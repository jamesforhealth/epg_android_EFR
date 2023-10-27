package com.flowehealth.efr_version.features.iop_test.test_cases.ota

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.databinding.DialogOtaFileSelectionIopBinding

class OtaFileSelectionDialog(private val listener: FileSelectionListener) : BaseDialogFragment(
        hasCustomWidth = true,
        isCanceledOnTouchOutside = false
) {

    private lateinit var _binding: DialogOtaFileSelectionIopBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = DialogOtaFileSelectionIopBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiListeners()
    }

    fun setupUiListeners() {
        _binding.apply {
            selectAppFileBtn.setOnClickListener { listener.onSelectFileButtonClicked() }
            otaCancel.setOnClickListener { listener.onCancelButtonClicked() }
            otaProceed.setOnClickListener { listener.onOtaButtonClicked() }
        }
    }

    fun changeFileName(newName: String?) {
        _binding.selectAppFileBtn.text = newName
    }

    fun enableUploadButton() {
        _binding.otaProceed.isEnabled = true
    }

    interface FileSelectionListener {
        fun onSelectFileButtonClicked()
        fun onOtaButtonClicked()
        fun onCancelButtonClicked()
    }
}