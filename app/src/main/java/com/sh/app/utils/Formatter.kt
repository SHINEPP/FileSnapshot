package com.sh.app.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.roundToInt

fun Long.formatFileSize(): String {
    var unit = "B"
    var result = toFloat()
    if (result > 900) {
        unit = "KB"
        result /= 1024f
    }
    if (result > 900) {
        unit = "MB"
        result /= 1024f
    }
    if (result > 900) {
        unit = "GB"
        result /= 1024f
    }
    if (result > 900) {
        unit = "TB"
        result /= 1024f
    }
    if (result > 900) {
        unit = "PB"
        result /= 1024f
    }

    val size: String
    when {
        result <= 0 -> {
            size = "0"
        }
        result < 10 -> {
            val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
            decimalFormat.roundingMode = RoundingMode.HALF_UP
            size = decimalFormat.format(result.toDouble())
        }
        result < 100 -> {
            val decimalFormat = DecimalFormat("###0.0", DecimalFormatSymbols(Locale.ENGLISH))
            decimalFormat.roundingMode = RoundingMode.HALF_UP
            size = decimalFormat.format(result.toDouble())
        }
        else -> {
            size = result.roundToInt().toString()
        }
    }
    return "$size $unit"
}