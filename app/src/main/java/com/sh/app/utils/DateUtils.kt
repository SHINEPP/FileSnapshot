package com.sh.app.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDatetimeString(): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA)
    return sdf.format(Date(this))
}