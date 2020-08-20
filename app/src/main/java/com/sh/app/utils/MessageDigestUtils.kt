package com.sh.app.utils

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

fun File.getSHA1(): String {
    var inputStream: InputStream? = null
    return try {
        inputStream = FileInputStream(this)
        val buffer = ByteArray(1024)
        val digest = MessageDigest.getInstance("SHA-1")
        var size = 0
        while (size != -1) {
            size = inputStream.read(buffer)
            if (size > 0) {
                digest.update(buffer, 0, size)
            }
        }
        digest.digest().toHexString()
    } catch (e: Throwable) {
        ""
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: Throwable) {
            }
        }
    }
}

fun ByteArray.getSHA1(): String {
    val digest = MessageDigest.getInstance("SHA-1")
    digest.update(this)
    return digest.digest().toHexString()
}

private fun ByteArray.toHexString(): String {
    val stringBuilder = StringBuilder("")
    for (element in this) {
        val v = element.toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}
