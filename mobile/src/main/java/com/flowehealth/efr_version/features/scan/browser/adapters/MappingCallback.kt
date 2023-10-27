package com.flowehealth.efr_version.features.scan.browser.adapters

import com.flowehealth.efr_version.features.scan.browser.models.Mapping

interface MappingCallback {
    fun onNameChanged(mapping: Mapping)
}
