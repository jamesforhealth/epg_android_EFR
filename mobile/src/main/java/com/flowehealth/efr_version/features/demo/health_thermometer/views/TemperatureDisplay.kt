package com.flowehealth.efr_version.features.demo.health_thermometer.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.flowehealth.efr_version.features.demo.health_thermometer.models.TemperatureReading
import com.flowehealth.efr_version.R
import kotlin.math.round

class TemperatureDisplay : LinearLayout {
    private var mainTempText: TextView? = null
    private var decimalText: TextView? = null
    private var degreeSymbol: TextView? = null
    private var defaultTextSize = 0f
    private var largeTextSize = 0f
    private var smallTextSize = 0f
    private var temp: Double? = null
    private var currentType: TemperatureReading.Type? = null
    private var currentReading: TemperatureReading? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        defaultTextSize = if (isInEditMode) 15f else context.resources.getDimension(R.dimen.thermo_graph_time_text_size)
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemperatureDisplay)
            largeTextSize = typedArray.getDimension(R.styleable.TemperatureDisplay_large_text_size, defaultTextSize)
            smallTextSize = typedArray.getDimension(R.styleable.TemperatureDisplay_small_text_size, defaultTextSize)
            typedArray.recycle()
        } else {
            largeTextSize = defaultTextSize
            smallTextSize = largeTextSize
        }
        currentType = TemperatureReading.Type.FAHRENHEIT
        View.inflate(context, R.layout.temperature_display, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mainTempText = findViewById(R.id.temp_display_primary)
        mainTempText?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, largeTextSize)
            it.setTextColor(context.getColor(R.color.blue_primary))
        }
        degreeSymbol = findViewById(R.id.temp_degree_symbol)
        degreeSymbol?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, largeTextSize)
            it.setTextColor(context.getColor(R.color.blue_primary))
        }
        decimalText = findViewById(R.id.temp_display_secondary)
        decimalText?.let {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize)
            it.setTextColor(context.getColor(R.color.blue_primary))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTemperature(temperature: Double) {
        val rounded = round(temperature * 10) / 10
        mainTempText?.text = (rounded.toString().split(".").elementAtOrNull(0) ?: "0") + "."
        decimalText?.text = (rounded.toString().split(".").elementAtOrNull(1) ?: "0")

    }

    fun setFontFamily(familyName: String?, style: Int) {
        val typeface = Typeface.create(familyName, style)
        mainTempText?.typeface = typeface
        decimalText?.typeface = typeface
        degreeSymbol?.typeface = typeface
    }

    fun setTemperature(reading: TemperatureReading?) {
        if (reading != null) {
            currentReading = reading
            setTemperature(reading.getTemperature(currentType!!))
        }
    }

    fun setCurrentType(currentType: TemperatureReading.Type?) {
        this.currentType = currentType
        setTemperature(currentReading)
    }
}