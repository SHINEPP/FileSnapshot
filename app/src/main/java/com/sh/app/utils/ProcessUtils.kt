package com.sh.app.utils

import android.app.Application
import android.os.Build
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset

private var curProcessName: String? = null

fun getCurrentProcessName(): String {
    if (curProcessName != null) {
        return curProcessName!!
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        curProcessName = Application.getProcessName() ?: ""
        return curProcessName!!
    }
    var inputStream: FileInputStream? = null
    try {
        val fn = "/proc/self/cmdline"
        inputStream = FileInputStream(fn)
        val buffer = ByteArray(256)
        var len = 0
        var b: Int
        while (inputStream.read().also { b = it } > 0 && len < buffer.size) {
            buffer[len++] = b.toByte()
        }
        if (len > 0) {
            curProcessName = String(buffer, 0, len, Charset.forName("UTF-8"))
            return curProcessName!!
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    curProcessName = ""
    return curProcessName!!
}