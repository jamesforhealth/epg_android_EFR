package com.flowehealth.efr_version.bluetooth.beacon_utils.eddystone

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
object Constants {
    /**
     * Eddystone-UID frame type value.
     */
    const val UID_FRAME_TYPE: Byte = 0x00

    /**
     * Eddystone-URL frame type value.
     */
    const val URL_FRAME_TYPE: Byte = 0x10

    /**
     * Eddystone-TLM frame type value.
     */
    const val TLM_FRAME_TYPE: Byte = 0x20

    /**
     * Minimum expected Tx power (in dBm) in UID and URL frames.
     */
    const val MIN_EXPECTED_TX_POWER = -100

    /**
     * Maximum expected Tx power (in dBm) in UID and URL frames.
     */
    const val MAX_EXPECTED_TX_POWER = 20
}