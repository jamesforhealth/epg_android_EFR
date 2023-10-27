package com.flowehealth.efr_version.features.demo.throughput.models

import com.flowehealth.efr_version.R

enum class PhyStatus(val stringResId: Int) {
    PHY_1M(R.string.throughput_phy_1m),
    PHY_2M(R.string.throughput_phy_2m),
    PHY_CODED_125K(R.string.throughput_phy_coded_125k),
    PHY_CODED_500K(R.string.throughput_phy_coded_500k)
}