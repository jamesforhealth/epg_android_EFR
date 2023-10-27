package com.flowehealth.efr_version.features.scan.browser.models

import com.flowehealth.efr_version.features.scan.rssi_graph.model.GraphPoint

data class GraphInfo(
        val data: MutableList<GraphPoint> = mutableListOf(),
        val dataColor: Int
)