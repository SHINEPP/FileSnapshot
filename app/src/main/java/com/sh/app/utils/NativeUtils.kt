package com.sh.app.utils

import android.util.Log

object NativeUtils {

    private const val TAG = "NATIVE_UTILS"

    fun getFileSize(path: String): Long {
        return try {
            nativeGetFileSize(path)
        } catch (e: Throwable) {
            Log.d(TAG, "getFileSize(), path = $path, e = $e")
            0L
        }
    }

    fun printDir(path: String) {
        try {
            nativePrintDir(path)
        } catch (e: Throwable) {
            Log.d(TAG, "printDir(), path = $path, e = $e")
        }
    }

    private external fun nativeGetFileSize(path: String): Long

    private external fun nativePrintDir(path: String)

    init {
        try {
            System.loadLibrary("appFileTools")
        } catch (e: Throwable) {
        }
    }
}