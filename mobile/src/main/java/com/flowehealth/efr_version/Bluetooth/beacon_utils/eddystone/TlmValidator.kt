package com.flowehealth.efr_version.bluetooth.beacon_utils.eddystone

import android.util.Log
import com.flowehealth.efr_version.utils.Converters
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

// Copyright 2015 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// see https://github.com/google/eddystone
/**
 * Basic validation of an Eddystone-TLM frame.
 *
 *
 *
 * @see [TLM frame specification](https://github.com/google/eddystone/eddystone-tlm)
 */
object TlmValidator {
    private val TAG = TlmValidator::class.java.simpleName

    // TLM frames only support version 0x00 for now.
    private const val EXPECTED_VERSION: Byte = 0x00

    // Minimum expected voltage value in beacon telemetry in millivolts.
    private const val MIN_EXPECTED_VOLTAGE = 500

    // Maximum expected voltage value in beacon telemetry in millivolts.
    private const val MAX_EXPECTED_VOLTAGE = 10000

    // Value indicating temperature not supported. temp[0] == 0x80, temp[1] == 0x00.
    private const val TEMPERATURE_NOT_SUPPORTED = -128.0f

    // Minimum expected temperature value in beacon telemetry in degrees Celsius.
    private const val MIN_EXPECTED_TEMP = 0.0f

    // Maximum expected temperature value in beacon telemetry in degrees Celsius.
    private const val MAX_EXPECTED_TEMP = 60.0f

    // Maximum expected PDU count in beacon telemetry.
    // The fastest we'd expect to see a beacon transmitting would be about 10 Hz.
    // Given that and a lifetime of ~3 years, any value above this is suspicious.
    private const val MAX_EXPECTED_PDU_COUNT = 10 * 60 * 60 * 24 * 365 * 3

    // Maximum expected time since boot in beacon telemetry.
    // Given that and a lifetime of ~3 years, any value above this is suspicious.
    private const val MAX_EXPECTED_SEC_COUNT = 10 * 60 * 60 * 24 * 365 * 3

    // The service data for a TLM frame should vary with each broadcast, but depending on the
    // firmware implementation a couple of consecutive TLM frames may be broadcast. Store the
    // frame only if few seconds have passed since we last saw one.
    private const val STORE_NEXT_FRAME_DELTA_MS = 3000
    fun validate(deviceAddress: String, serviceData: ByteArray?, beacon: Beacon) {
        beacon.hasTlmFrame = true
        var previousTlm: ByteArray? = null
        if (beacon.tlmServiceData == null) {
            beacon.tlmServiceData = serviceData
            beacon.timestamp = System.currentTimeMillis()
        } else if (System.currentTimeMillis() - beacon.timestamp > STORE_NEXT_FRAME_DELTA_MS) {
            beacon.timestamp = System.currentTimeMillis()
            previousTlm = beacon.tlmServiceData?.clone()
            if (Arrays.equals(beacon.tlmServiceData, serviceData)) {
                val err = "TLM service data was identical to recent TLM frame:\n" + Converters.bytesToHex(serviceData!!)
                beacon.tlmStatus.errIdentialFrame = err
                logDeviceError(deviceAddress, err)
                beacon.tlmServiceData = serviceData
            }
        }
        val buf = ByteBuffer.wrap(serviceData)
        buf.get() // We already know the frame type byte is 0x20.

        // The version should be zero.
        val version = buf.get()
        beacon.tlmStatus.version = String.format("0x%02X", version)
        if (version != EXPECTED_VERSION) {
            val err = String.format("Bad TLM version, expected 0x%02X, got %02X",
                    EXPECTED_VERSION, version)
            beacon.tlmStatus.errVersion = err
            logDeviceError(deviceAddress, err)
        }

        // Battery voltage should be sane. Zero is fine if the device is externally powered, but
        // it shouldn't be negative or unreasonably high.
        val voltage = buf.short
        beacon.tlmStatus.voltage = voltage.toString()
        if (voltage.toInt() != 0 && (voltage < MIN_EXPECTED_VOLTAGE || voltage > MAX_EXPECTED_VOLTAGE)) {
            val err = String.format("Expected TLM voltage to be between %d and %d, got %d",
                    MIN_EXPECTED_VOLTAGE, MAX_EXPECTED_VOLTAGE, voltage)
            beacon.tlmStatus.errVoltage = err
            logDeviceError(deviceAddress, err)
        }

        // Temp varies a lot with the hardware and the margins appear to be very wide. USB beacons
        // in particular can report quite high temps. Let's at least check they're partially sane.
        val tempIntegral = buf.get()
        val tempFractional: Int = buf.get().toInt() and 0xff
        val temp = tempIntegral + tempFractional / 256.0f
        beacon.tlmStatus.temp = temp.toString()
        if (temp != TEMPERATURE_NOT_SUPPORTED) {
            if (temp < MIN_EXPECTED_TEMP || temp > MAX_EXPECTED_TEMP) {
                val err = String.format("Expected TLM temperature to be between %.2f and %.2f, got %.2f",
                        MIN_EXPECTED_TEMP, MAX_EXPECTED_TEMP, temp)
                beacon.tlmStatus.errTemp = err
                logDeviceError(deviceAddress, err)
            }
        }

        // Check the PDU count is increasing from frame to frame and is neither too low or too high.
        val advCnt = buf.int
        beacon.tlmStatus.advCnt = advCnt.toString()
        if (advCnt <= 0) {
            val err = "Expected TLM ADV count to be positive, got $advCnt"
            beacon.tlmStatus.errPduCnt = err
            logDeviceError(deviceAddress, err)
        }
        if (advCnt > MAX_EXPECTED_PDU_COUNT) {
            val err = String.format("TLM ADV count %d is higher than expected max of %d",
                    advCnt, MAX_EXPECTED_PDU_COUNT)
            beacon.tlmStatus.errPduCnt = err
            logDeviceError(deviceAddress, err)
        }
        if (previousTlm != null) {
            val previousAdvCnt = ByteBuffer.wrap(previousTlm, 6, 4).int
            if (previousAdvCnt == advCnt) {
                val err = "Expected increasing TLM PDU count but unchanged from $advCnt"
                beacon.tlmStatus.errPduCnt = err
                logDeviceError(deviceAddress, err)
            }
        }

        // Check that the time since boot is increasing and is neither too low nor too high.
        val uptime = buf.int
        beacon.tlmStatus.deciSecondsCntVal = uptime.toDouble()
        beacon.tlmStatus.secCnt = String.format("%d (%d days)", uptime, TimeUnit.SECONDS.toDays(uptime / 10.toLong()))
        if (uptime <= 0) {
            val err = "Expected TLM time since boot to be positive, got $uptime"
            beacon.tlmStatus.errSecCnt = err
            logDeviceError(deviceAddress, err)
        }
        if (uptime > MAX_EXPECTED_SEC_COUNT) {
            val err = String.format("TLM time since boot %d is higher than expected max of %d",
                    uptime, MAX_EXPECTED_SEC_COUNT)
            beacon.tlmStatus.errSecCnt = err
            logDeviceError(deviceAddress, err)
        }
        if (previousTlm != null) {
            val previousUptime = ByteBuffer.wrap(previousTlm, 10, 4).int
            if (previousUptime == uptime) {
                val err = "Expected increasing TLM time since boot but unchanged from $uptime"
                beacon.tlmStatus.errSecCnt = err
                logDeviceError(deviceAddress, err)
            }
        }
        val rfu = Arrays.copyOfRange(serviceData, 14, 20)
        for (b in rfu) {
            if (b.toInt() != 0x00) {
                val err = "Expected TLM RFU bytes to be 0x00, were " + Converters.bytesToHex(rfu)
                beacon.tlmStatus.errRfu = err
                logDeviceError(deviceAddress, err)
                break
            }
        }
    }

    private fun logDeviceError(deviceAddress: String, err: String) {
        Log.e(TAG, "$deviceAddress: $err")
    }
}
