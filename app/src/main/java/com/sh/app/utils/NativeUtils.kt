package com.sh.app.utils

object NativeUtils {

    fun getFileSize(path: String): Long {
        return try {
            nativeGetFileSize(path)
        } catch (e: Throwable) {
            0L
        }
    }

    private external fun nativeGetFileSize(path: String): Long

    init {
        try {
            System.loadLibrary("appFileTools")
        } catch (e: Throwable) {
        }
    }
}