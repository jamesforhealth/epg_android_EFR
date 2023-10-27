package com.flowehealth.efr_version.features.demo.epg.charts

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class FreqRawDataChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        //  Log.d(TAG, "setRawFFTChart")
        val sdata = data.sortedBy { it.x }

        val set1 = LineDataSet(sdata, "Data Set 1")
        set1.color = Color.YELLOW
        set1.setDrawCircles(false)

        val cdata= LineData(set1)
        cdata.setDrawValues(false)
        chartView.data = cdata

        chartView.invalidate()
    }

    override fun configChart() {
        // 設定圖表的實現
        chartView.setBackgroundColor(Color.BLACK)
        chartView.setNoDataTextColor(Color.YELLOW)

        chartView.axisRight.isEnabled = false
        chartView.setPinchZoom(false)
        chartView.isHighlightPerDragEnabled = false
        chartView.isHighlightPerTapEnabled = false
        chartView.setDoubleTapToZoomEnabled(false)
        chartView.legend.isEnabled = false
        chartView.setDrawBorders(false)
        chartView.setAutoScaleMinMaxEnabled(false)

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

        //axisMaximum 跟 axisMinimum 應該動態歲著輸入值變化，設定為動態的
        yAxis.axisMaximum = 60f
        yAxis.axisMinimum = 0f

        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 10f
        xAxis.axisMinimum = 0f

    }
}

class FreqHeartRateChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        // Log.d(TAG, "setHeartRateChart")
        val sdata = data.sortedBy { it.x }
        val set = LineDataSet(sdata, "Data Set")
        set.color = Color.YELLOW
        set.setDrawCircles(false)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        val cdata = LineData(set)
        cdata.setDrawValues(false)
        chartView.data = cdata

        chartView.invalidate()
    }


    override fun configChart() {
        // 設定圖表的實現
        chartView.setBackgroundColor(Color.BLACK)
        chartView.setNoDataTextColor(Color.YELLOW)

        chartView.axisRight.isEnabled = false
        chartView.setPinchZoom(false)
        chartView.isHighlightPerDragEnabled = false
        chartView.isHighlightPerTapEnabled = false
        chartView.setDoubleTapToZoomEnabled(false)
        chartView.legend.isEnabled = false
        chartView.setDrawBorders(false)
        chartView.setAutoScaleMinMaxEnabled(false)

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.axisMaximum = 300f
        yAxis.axisMinimum = 0f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 1f
        xAxis.axisMinimum = 0f

    }
}

