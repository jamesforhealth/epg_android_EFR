package com.flowehealth.efr_version.features.demo.range_test.models

/**
 * @author Comarch S.A.
 */
enum class RangeTestMode(val code: Int) {
    Rx(1),
    Tx(2);

    companion object {
        fun fromCode(code: Int): RangeTestMode {
            return when (code) {
                1 -> Rx
                2 -> Tx
                else -> throw IllegalArgumentException("No mode for code: $code")
            }
        }
    }

}