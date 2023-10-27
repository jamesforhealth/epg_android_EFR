package com.flowehealth.efr_version.features.configure.advertiser.enums

enum class AdvertisingMode {
    CONNECTABLE_SCANNABLE,
    CONNECTABLE_NON_SCANNABLE,
    NON_CONNECTABLE_SCANNABLE,
    NON_CONNECTABLE_NON_SCANNABLE;

    fun isConnectable(): Boolean {
        return this == CONNECTABLE_SCANNABLE || this == CONNECTABLE_NON_SCANNABLE
    }

    fun isScannable(): Boolean {
        return this == CONNECTABLE_SCANNABLE || this == NON_CONNECTABLE_SCANNABLE
    }
}