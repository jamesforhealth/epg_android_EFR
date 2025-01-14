package com.flowehealth.efr_version.home_screen.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.flowehealth.efr_version.base.fragments.BaseDialogFragment
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.databinding.DialogLocationInfoBinding
import kotlinx.android.synthetic.main.dialog_location_info.*

class WarningBarInfoDialog(private val type: Type) : BaseDialogFragment(
    hasCustomWidth = true,
    isCanceledOnTouchOutside = true,
) {
    private val binding by viewBinding(DialogLocationInfoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.dialog_location_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_ok.setOnClickListener { dismiss() }
        initTexts()
    }

    private fun initTexts() {
        binding.infoDialogTitle.text = getString( when(type) {
            Type.LOCATION -> R.string.Location_service
            Type.LOCATION_PERMISSION -> R.string.location_permission
            Type.BLUETOOTH_PERMISSIONS -> R.string.bluetooth_permissions
        })
        binding.infoDialogDescription.text = getString( when(type) {
            Type.LOCATION -> R.string.location_service_info
            Type.LOCATION_PERMISSION -> R.string.location_permission_info
            Type.BLUETOOTH_PERMISSIONS -> R.string.bluetooth_permissions_info
        })
    }

    enum class Type {
        LOCATION,
        LOCATION_PERMISSION,
        BLUETOOTH_PERMISSIONS
    }

}