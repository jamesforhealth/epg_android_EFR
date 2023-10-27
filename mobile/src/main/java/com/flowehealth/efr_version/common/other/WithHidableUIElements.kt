package com.flowehealth.efr_version.common.other

import android.content.Context

interface WithHidableUIElements {
    fun getLayoutManagerWithHidingUIElements(context: Context?): LinearLayoutManagerWithHidingUIElements
}