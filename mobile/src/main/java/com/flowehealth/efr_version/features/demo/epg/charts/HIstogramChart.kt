package com.flowehealth.efr_version.features.demo.epg.charts
import android.graphics.Color
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.formatter.ValueFormatter

class HistRawDataChart() : Chart<Entry, LineChart> {
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
        yAxis.axisMaximum = 150f
        yAxis.axisMinimum = 0f

        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 8f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = -0.7f  // Modify x-axis maximum to -0.7
        xAxis.axisMinimum = -3.5f  // Modify x-axis minimum to -3.5
        xAxis.labelCount = 281  // (3.5 - 0.7) / 0.01 + 1 = 281

    }
}

class HistHeartRateChart() : Chart<Entry, LineChart> {
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
        yAxis.axisMaximum = 30f
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

        xAxis.axisMaximum = 240f
        xAxis.axisMinimum = 0f

    }
}


class IntegralChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        // Log.d(TAG, "setRawFFTChart")
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
        yAxis.axisMaximum = 1.5f
        yAxis.axisMinimum = -1.5f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f

    }
}


class DoubleIntegralChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        // Log.d(TAG, "setRawFFTChart")
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
        yAxis.axisMaximum = 0.15f
        yAxis.axisMinimum = -0.15f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f
    }
}


class ReturnChart() : Chart<Entry, LineChart> {
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
        chartView.description.text = "Time (sec)"
        chartView.description.setPosition(chartView.width.toFloat() - 100, chartView.height.toFloat() - 20)
        chartView.description.textSize = 12f
        chartView.description.textColor = Color.WHITE

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.axisMaximum = 0f
        yAxis.axisMinimum = -4f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE
//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return "$value V"  // 添加 "V" 单位
//            }
//        }
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f

    }
}

class FixRawChart() : Chart<LineDataSet, LineChart> {
    override lateinit var chartView: LineChart
    val startColor = Color.YELLOW
    val endColor = Color.MAGENTA // 深綠色

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

//    override fun setChartData(data: List<Entry>) {
//        // 更新資料的實現
//        // Log.d(TAG, "setHeartRateChart")
//        val sdata = data.sortedBy { it.x }
//        val set = LineDataSet(sdata, "Data Set")
//        set.color = Color.YELLOW
//        set.setDrawCircles(false)
//        set.mode = LineDataSet.Mode.CUBIC_BEZIER
//        val cdata = LineData(set)
//        cdata.setDrawValues(false)
//        chartView.data = cdata
//
//        chartView.invalidate()
//    }
    override fun setChartData(data: List<LineDataSet>) {
        // 更新資料的實現
        val size = data.size
        for (i in 0 until size) {
            val fraction = i.toFloat() / size
            val r = (Color.red(startColor) + fraction * (Color.red(endColor) - Color.red(startColor))).toInt()
            val g = (Color.green(startColor) + fraction * (Color.green(endColor) - Color.green(startColor))).toInt()
            val b = (Color.blue(startColor) + fraction * (Color.blue(endColor) - Color.blue(startColor))).toInt()
            val interpolatedColor = Color.rgb(r, g, b)
            val set = data[i]
            set.color = interpolatedColor
            set.setDrawCircles(false)
            set.mode = LineDataSet.Mode.CUBIC_BEZIER

        }
        val cdata = LineData(data)
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
        chartView.description.text = "Time (sec)"
        chartView.description.setPosition(chartView.width.toFloat() - 100, chartView.height.toFloat() - 20)
        chartView.description.textSize = 12f
        chartView.description.textColor = Color.WHITE

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//        yAxis.axisMaximum = 0f
//        yAxis.axisMinimum = -4f

        yAxis.axisMaximum = 1.4f
        yAxis.axisMinimum = -1.4f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE
//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return "$value V"  // 添加 "V" 单位
//            }
//        }
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f


    }

}


class DoubleIntegralCalibrateChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart

    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        // Log.d(TAG, "setRawFFTChart")
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
        yAxis.axisMaximum = 0.15f
        yAxis.axisMinimum = -0.15f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f
    }
}


class PulseEyeDiagramChart() : Chart<LineDataSet, LineChart> {
    override lateinit var chartView: LineChart
    val startColor = Color.parseColor("#FFFFE0") // 淡黄色
    val endColor = Color.YELLOW // 深綠色


    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<LineDataSet>) {
        // 更新資料的實現
        val size = data.size
        for (i in 0 until size) {
            val fraction = i.toFloat() / size
            val r = (Color.red(startColor) + fraction * (Color.red(endColor) - Color.red(startColor))).toInt()
            val g = (Color.green(startColor) + fraction * (Color.green(endColor) - Color.green(startColor))).toInt()
            val b = (Color.blue(startColor) + fraction * (Color.blue(endColor) - Color.blue(startColor))).toInt()
            val interpolatedColor = Color.rgb(r, g, b)
            // Log.d(TAG, "interpolatedColor:$interpolatedColor")
            val set = data[i]
            set.color = interpolatedColor
            set.setDrawCircles(false)
            set.mode = LineDataSet.Mode.CUBIC_BEZIER

        }
        // Log.d(TAG, "setEyeDiagramChart")
        val cdata = LineData(data)
        cdata.setDrawValues(false)
        chartView.data = cdata
        // Log.d(TAG, "setEyeDiagramChart data = $data")
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
        yAxis.axisMaximum = 0.15f
        yAxis.axisMinimum = -0.15f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE
//        yAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return "$value V"  // 添加 "V" 单位
//            }
//        }
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 1.5f
        xAxis.axisMinimum = -0.5f

        xAxis.setDrawGridLines(false)

        for (i in 0..20 step 1) {
            val line = LimitLine(i / 10.0f - 0.5f)
            if(i % 2 == 0) {
                line.lineWidth = 0.4f
                line.lineColor = Color.WHITE
            } else {
                line.lineWidth = 0.3f
                line.lineColor = Color.argb(150, 255, 255, 255)
            }
            chartView.xAxis.addLimitLine(line)
        }
    }
}


class CombinedChart(override val chartView: CombinedChart) : CombinedChartInterface {

    init {
        configChart()
    }

    override fun setChartData(data: CombinedData) {
        chartView.data = data
        chartView.invalidate()
    }

    override fun configChart() {
        chartView.setBackgroundColor(Color.BLACK)
        chartView.setNoDataTextColor(Color.YELLOW)

        chartView.axisRight.isEnabled = false
        chartView.setPinchZoom(true)  // 允許縮放
        chartView.isHighlightPerDragEnabled = false
        chartView.isHighlightPerTapEnabled = false
        chartView.setDoubleTapToZoomEnabled(true)
        chartView.legend.isEnabled = true
        chartView.legend.textColor = Color.WHITE
        chartView.setDrawBorders(false)
        chartView.setAutoScaleMinMaxEnabled(true)
        chartView.description.isEnabled = false
        chartView.isScaleYEnabled = true
        chartView.isScaleXEnabled = true
        // Y-Axis settings
        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.axisMaximum = 0.3f
        yAxis.axisMinimum = -0.3f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE

        // X-Axis settings
        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE
        xAxis.axisMaximum = 7f
        xAxis.axisMinimum = 0f
    }
}
