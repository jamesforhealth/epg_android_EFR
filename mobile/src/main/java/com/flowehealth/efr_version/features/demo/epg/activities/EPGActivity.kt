package com.flowehealth.efr_version.features.demo.epg.activities
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.flowehealth.efr_version.BuildConfig
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.base.activities.BaseDemoActivity
import com.flowehealth.efr_version.bluetooth.ble.TimeoutGattCallback
import com.flowehealth.efr_version.features.demo.epg.charts.RawDataChart

@SuppressLint("MissingPermission")
class EPGActivity : BaseDemoActivity() {
    // 定義控件
    private lateinit var rawDataChart: RawDataChart
    // 定義其他圖表的控件和類，如eyeDiagramChart, pulseEyeDiagramChart, heartRateChart等

    private lateinit var btnClear: Button
    private lateinit var btnRecord: Button
    private lateinit var subjectID: EditText
    private lateinit var note: EditText
    private lateinit var modeSpinner: Spinner
    // ... 其他控件 ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epg)

        btnClear = findViewById(R.id.btn_clear)
        btnRecord = findViewById(R.id.btn_record)
        subjectID = findViewById(R.id.subjectID)
        note = findViewById(R.id.note)
        modeSpinner = findViewById(R.id.modeSpinner)

        // 初始化圖表
        rawDataChart = RawDataChart(findViewById(R.id.rawDataChart))
        // 初始化其他圖表...

        // 設置控件的事件監聽，例如btnClear的點擊事件等
        btnClear.setOnClickListener {
            // 清除圖表數據或執行其他操作
            rawDataChart.clearChartData()
            // 清除其他圖表的數據...
        }

        btnRecord.setOnClickListener {
            // 執行錄制或其他操作
        }

        // ... 其他初始化和設置操作 ...
    }

    private val gattCallback: TimeoutGattCallback = object : TimeoutGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            // 實現EPG特有的藍牙連接狀態改變的處理邏輯
            // ...
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            // 實現EPG特有的藍牙服務發現的處理邏輯
            // ...
        }

        @SuppressLint("LogNotTimber")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.d(BuildConfig.TAG, "onCharacteristicChanged in EPGActivity: value = ${value.contentToString()}")
        }

    }


    override fun onBluetoothServiceBound() {
        // 藍牙服務綁定時的EPG特有邏輯
        service?.registerGattCallback(true, gattCallback)
        gatt?.discoverServices()
    }

}
