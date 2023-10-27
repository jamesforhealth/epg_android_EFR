package com.flowehealth.efr_version.home_screen.base

interface LocationDependent {

    fun onLocationStateChanged(isLocationOn: Boolean)
    fun onLocationPermissionStateChanged(isPermissionGranted: Boolean)
    fun setupLocationBarButtons()
    fun setupLocationPermissionBarButtons()
}