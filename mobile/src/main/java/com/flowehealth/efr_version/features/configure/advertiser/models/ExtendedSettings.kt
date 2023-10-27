package com.flowehealth.efr_version.features.configure.advertiser.models

import android.os.Parcelable
import com.flowehealth.efr_version.features.configure.advertiser.enums.Phy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtendedSettings(
        val includeTxPower: Boolean = false,
        val anonymous: Boolean = false,
        val primaryPhy: Phy? = null,
        val secondaryPhy: Phy? = null
) : Parcelable