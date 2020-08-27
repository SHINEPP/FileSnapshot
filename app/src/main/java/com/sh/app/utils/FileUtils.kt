package com.sh.app.utils

import java.io.File

fun File.available(): Long {
    if (!isFile) {
        return 0L
    }
    val inputStream = inputStream()
    val size = inputStream.available().toLong()
    inputStream.close()
    return size
}