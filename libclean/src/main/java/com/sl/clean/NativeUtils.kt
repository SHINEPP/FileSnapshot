package com.sl.clean

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

    private external fun nativeGetFileSize(path: String): Long

    init {
        try {
            System.loadLibrary("sl2clean")
        } catch (e: Throwable) {
        }
    }
}