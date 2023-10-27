package com.flowehealth.efr_version.features.scan.browser.view_states

import com.flowehealth.efr_version.home_screen.viewmodels.ScanFragmentViewModel

data class ScannerFragmentViewState(
        val isScanningOn: Boolean,
        val devicesToShow: List<ScanFragmentViewModel.BluetoothInfoViewState>
)
