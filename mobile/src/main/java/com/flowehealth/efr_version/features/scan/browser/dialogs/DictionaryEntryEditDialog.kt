package com.flowehealth.efr_version.features.scan.browser.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.features.scan.browser.adapters.MappingCallback
import com.flowehealth.efr_version.features.scan.browser.models.Mapping
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.databinding.DialogDictionaryEntryEditBinding

class DictionaryEntryEditDialog(
        private val name: String,
        private val UUID: String,
        private val type: Mapping.Type,
        private val callback: MappingCallback
) : BaseDialogFragment(
        hasCustomWidth = true,
        isCanceledOnTouchOutside = true
) {

    private lateinit var _binding: DialogDictionaryEntryEditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogDictionaryEntryEditBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiListeners()
        initViews()

    }

    private fun setupUiListeners() {
        _binding.apply {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener {
                val newName = etNameHint.text.toString()
                if (newName.isNotBlank()) {
                    callback.onNameChanged(Mapping(UUID, newName))
                    dismiss()
                } else {
                    Toast.makeText(context, getString(R.string.name_field_cannot_be_empty), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initViews() {
        _binding.apply {
            tvMappingType.text = getString( when (type) {
                Mapping.Type.SERVICE -> R.string.rename_service
                Mapping.Type.CHARACTERISTIC -> R.string.rename_characteristic
            })
            tvUuid.text = UUID
            etNameHint.setText(name)
            etNameHint.setSelection(etNameHint.text.length)
        }
    }
}
