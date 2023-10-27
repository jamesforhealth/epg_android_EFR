package com.flowehealth.efr_version.features.demo.epg.charts
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import java.lang.reflect.Field

class CustomLineChart : LineChart {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        val customRenderer = object : XAxisRenderer(viewPortHandler, xAxis, getTransformer(YAxis.AxisDependency.LEFT)) {
            override fun drawGridLine(c: Canvas?, x: Float, y: Float, gridlinePath: Path?) {
                Log.d("GeniusPudding", "CustomLineChart: drawGridLine: x:${x},y:${y}")
                if (x % 1 == 0f) { // 每秒
                    mGridPaint.color = Color.WHITE
                    mGridPaint.strokeWidth = 5f // 設定粗一些的線
                    xAxis.setDrawLabels(true)
                } else {
                    mGridPaint.color = Color.WHITE//LTGRAY
                    mGridPaint.strokeWidth = 0.5f // 設定較細的線
                    xAxis.setDrawLabels(false)
                }
                super.drawGridLine(c, x, y, gridlinePath)
            }
        }
        try {
            val rendererField: Field = BarLineChartBase::class.java.getDeclaredField("mXAxisRenderer")
            rendererField.isAccessible = true
            rendererField.set(this, customRenderer)
            Log.d("GeniusPudding", "CustomLineChart: init success")
        } catch (e: Exception) {
            Log.d("GeniusPudding", "CustomLineChart: init error")
            e.printStackTrace()
        }

    }
}