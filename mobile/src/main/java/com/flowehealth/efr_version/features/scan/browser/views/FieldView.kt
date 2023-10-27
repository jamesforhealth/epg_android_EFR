package com.flowehealth.efr_version.features.scan.browser.views

import android.content.Context
import com.flowehealth.efr_version.bluetooth.data_types.Field

abstract class FieldView(
        context: Context?,
        protected val field: Field,
        fieldValue: ByteArray
) : ValueView(context, fieldValue)