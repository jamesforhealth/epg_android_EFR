package com.flowehealth.efr_version.features.demo.epg.charts

import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
//import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.flowehealth.efr_version.BuildConfig.TAG
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CombinedData

interface Chart<T, U> {

    fun setChartData(data: List<T>)
    fun clearChartData(){
        setChartData(emptyList())
    }
    fun configChart()
    val chartView: U
}
interface RealtimeChart<T, U> {
    fun setChartData(data: MutableList<T>)
    fun clearChartData(){
        setChartData(mutableListOf())
    }
    fun configChart()
    val chartView: U
}

interface CombinedChartInterface {
    fun setChartData(data: CombinedData)
    fun clearChartData() {
        setChartData(CombinedData())
    }
    fun configChart()
    val chartView: CombinedChart
}

class RawDataChart() : RealtimeChart<Entry, CombinedChart> {
    override lateinit var chartView: CombinedChart

    constructor(chartView: CombinedChart) : this() {
        this.chartView = chartView
        configChart()
    }

    // Update 1 point each time
    fun updateChartData(updatedPoint: Pair<Int, Double>) {
        val combinedData = chartView.data
        val lineData = combinedData.lineData
        lineData?.let {
            val dataSet = lineData.getDataSetByIndex(0) as? LineDataSet
//        val dataSet = chartView.data?.getDataSetByIndex(0) as? LineDataSet
            dataSet?.let{
                val entry :Entry? = dataSet.getEntryForIndex(updatedPoint.first)
                // Log.d(TAG, "updateChartData updatedPoint: $updatedPoint, entry: $entry")
                entry?.y = updatedPoint.second.toFloat()
                chartView.data?.notifyDataChanged()
                chartView.notifyDataSetChanged()
                chartView.invalidate()
            }
        }
    }

    // Update all points each time version
    override fun setChartData(data: MutableList<Entry>) {
        // 更新資料的實現
//        Log.d(TAG, "setRawDataChart")
        val sdata = data.sortedBy { it.x }
        val set1 = LineDataSet(sdata, "Data Set 1")
        set1.color = Color.YELLOW
        set1.setDrawCircles(false)
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER

        val setdata = LineData(set1)
        setdata.setDrawValues(false)

        val combinedData = CombinedData()
        combinedData.setData(setdata)
        chartView.data = combinedData
        // 清空数据
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
        xAxis.setDrawGridLines(false)

        for (i in 0..7 step 1) { 
            val thickLine = LimitLine(i.toFloat())
            thickLine.lineWidth = 0.4f
            thickLine.lineColor = Color.WHITE
            chartView.xAxis.addLimitLine(thickLine)
        }

        for (i in 0..35 step 1) {
            val thinLine = LimitLine(i / 5.0f)
            thinLine.lineWidth = 0.3f
//            thinLine.lineColor = Color.LTGRAY
            thinLine.lineColor = Color.argb(150, 255, 255, 255)
            chartView.xAxis.addLimitLine(thinLine)
        }

    }

    fun setPointsDataSet(pointIndices: List<Int>){
        val combinedData = chartView.data
        val lineData = combinedData?.lineData
        val dataSet = lineData?.getDataSetByIndex(0) as? LineDataSet
//        Log.d(TAG, "setPointsDataSet pointIndices: $pointIndices,lineData: $lineData, dataSet: $dataSet")
        dataSet?.let{
            val pointEntries = pointIndices.mapNotNull { index ->
                dataSet.getEntryForIndex(index)
            }
            Log.d(TAG, "setPointsDataSet pointEntries: $pointEntries")
            // 創建一個新的 ScatterDataSet 來保存峰值點
            val scatterDataSet = ScatterDataSet(pointEntries, "Peak points")
            scatterDataSet.axisDependency = YAxis.AxisDependency.LEFT
            scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            scatterDataSet.scatterShapeSize = 30f  // 設定峰值點的大小
            scatterDataSet.color = Color.RED  // 設定峰值點的顏色
            // 添加 ScatterDataSet 到圖表
            combinedData.addDataSet(scatterDataSet)
//            Log.d(TAG, "setPointsDataSet scatterDataSet: $scatterDataSet")
            chartView.data = combinedData
            // Log.d(TAG, "combinedData.dataSetCount: ${combinedData.dataSetCount}, combinedData.dataSets: ${combinedData.dataSets}")
            chartView.invalidate()
        }
    }

}

class PulseChart() : Chart<LineDataSet, LineChart> {
    override lateinit var chartView: LineChart
    val startColor = Color.parseColor("#FFFFE0") // 淡黄色
    val endColor = Color.YELLOW // 深綠色


    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<LineDataSet>) {
        val colors = arrayOf(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE, Color.GRAY)
        val size = data.size
        for (i in 0 until size) {
            val set = data[i]
            set.color = colors[i]
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

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.axisMaximum = 0.01f
        yAxis.axisMinimum = 0.0f
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

        xAxis.axisMaximum = 2f
        xAxis.axisMinimum = -0.3f


    }

    fun adjustAxis(max: Float){
        val yAxis: YAxis = chartView.axisLeft
        yAxis.axisMaximum = max
        yAxis.axisMinimum = max * -0.5f
    }
}

class LogPulseChart() : Chart<LineDataSet, LineChart> {
    override lateinit var chartView: LineChart
    constructor(chartView: LineChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<LineDataSet>) {
        val colors = arrayOf(Color.YELLOW, Color.RED)
        val size = data.size
        for (i in 0 until size) {
            val set = data[i]
            set.color = colors[i]
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

        val yAxis: YAxis = chartView.axisLeft
        yAxis.textSize = 12f
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yAxis.axisMaximum = -1.0f
        yAxis.axisMinimum = -4.0f
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

        xAxis.axisMaximum = 2f
        xAxis.axisMinimum = -0.3f


    }

    fun adjustAxis(max: Float, min: Float){
        val yAxis: YAxis = chartView.axisLeft
        yAxis.axisMaximum = max
        yAxis.axisMinimum = min
    }
}

class OneWaveformChart() : Chart<Entry, LineChart> {
    override lateinit var chartView: LineChart
    val startColor = Color.parseColor("#FFFFE0") // 淡黄色
    val endColor = Color.YELLOW // 深綠色


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
        yAxis.axisMaximum = -0.7f
        yAxis.axisMinimum = -3.5f
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
    }

    fun setPeakAndTroughIndices(samplingRate: Float, indices: List<Pair<Int, Int>>) {
        // 清除之前的所有LimitLine（如果有的話）
        chartView.xAxis.removeAllLimitLines()

        // 添加新的LimitLine到x軸
        for (indexPair in indices) {
            val peakX = indexPair.first.toFloat() / samplingRate
            val troughX = indexPair.second.toFloat() / samplingRate
            Log.d(TAG, "peakX: $peakX, troughX: $troughX")
            chartView.xAxis.addLimitLine(LimitLine(peakX, "Peak").apply {
                lineWidth = 0.6f
                lineColor = Color.WHITE // 可根據你的需求更改顏色和線寬
            })

            chartView.xAxis.addLimitLine(LimitLine(troughX, "Trough").apply {
                lineWidth = 0.6f
                lineColor = Color.BLUE // 可根據你的需求更改顏色和線寬
            })
        }

        // 強制圖表重新繪製以顯示新的LimitLines
        chartView.invalidate()
    }
}


class EyeDiagramChart() : Chart<LineDataSet, LineChart> {
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
        yAxis.axisMaximum = -0.7f
        yAxis.axisMinimum = -3.5f
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

class PoincarePlotChart() : Chart<Entry, ScatterChart> {
    override lateinit var chartView: ScatterChart

    constructor(chartView: ScatterChart) : this() {
        this.chartView = chartView
        configChart()
    }

    override fun setChartData(data: List<Entry>) {
        // 更新資料的實現
        // Sort the data
        val sdata = data.sortedBy { it.x }
        // Log.d(TAG, "setPoincareChart data = $sdata")
        // Create a list of Entry objects
        val entries = sdata.map { Entry(it.x, it.y) }

        // Create the ScatterDataSet
        val set = ScatterDataSet(entries, "Data Set")
        set.color = Color.YELLOW
        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        set.scatterShapeSize = 2f

        // Create the ScatterData and set it to the chart
        val cdata = ScatterData(set)
        cdata.setDrawValues(false)
        chartView.data = cdata

        // Refresh the chart
        chartView.invalidate()
        // Log.d(TAG, "PoincareChart Refreshed")
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
        yAxis.axisMaximum = 2.5f
        yAxis.axisMinimum = 0f
        yAxis.gridColor = Color.WHITE
        yAxis.axisLineColor = Color.WHITE
        yAxis.textColor = Color.WHITE
        yAxis.setLabelCount(11, true)
//        yAxis.valueFormatter = formatter

        val xAxis: XAxis = chartView.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.axisLineColor = Color.WHITE
        xAxis.gridColor = Color.WHITE

        xAxis.axisMaximum = 3.5f
        xAxis.axisMinimum = 0f
        xAxis.setLabelCount(15, true)
//        xAxis.valueFormatter = formatter

    }
}

class HeartRateChart() : Chart<Entry, LineChart> {
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
        yAxis.axisMaximum = 200f
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

        xAxis.axisMaximum = 30f
        xAxis.axisMinimum = 0f

    }
}


//private val formatter = object : ValueFormatter() {
//    private val mFormat: DecimalFormat = DecimalFormat("#.##")
//
//    override fun getFormattedValue(value: Float): String {
//        return mFormat.format(value)
//    }
//}
