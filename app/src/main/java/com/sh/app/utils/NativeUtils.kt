package com.sh.app.utils

import android.util.Log

object NativeUtils {

    const val TAG = "NATIVE_UTILS"

    fun getFileSize(path: String): Long {
        return try {
            nativeGetFileSize(path)
        } catch (e: Throwable) {
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

    // test

    fun openDir(path: String): Long {
        return try {
            nativeOpenDir(path)
        } catch (e: Throwable) {
            -1L
        }
    }

    fun chDir(path: String) {
        try {
            nativeChDir(path)
        } catch (e: Throwable) {

        }
    }

    fun readDir(dir: Long): Long {
        return try {
            nativeReadDir(dir)
        } catch (e: Throwable) {
            -1L
        }
    }

    fun getDirentType(dirent: Long): Int {
        return try {
            nativeGetDirentType(dirent)
        } catch (e: Throwable) {
            -1
        }
    }

    fun getDirentName(dirent: Long): String? {
        return try {
            nativeGetDirentName(dirent)
        } catch (e: Throwable) {
            null
        }
    }

    fun closeDir(dir: Long) {
        try {
            nativeCloseDir(dir)
        } catch (e: Throwable) {

        }
    }

    private external fun nativeGetFileSize(path: String): Long

    private external fun nativePrintDir(path: String)

    // test

    private external fun nativeOpenDir(path: String): Long

    private external fun nativeChDir(path: String)

    private external fun nativeReadDir(dir: Long): Long

    private external fun nativeGetDirentType(dirent: Long): Int

    private external fun nativeGetDirentName(dirent: Long): String?

    private external fun nativeCloseDir(dir: Long)


    init {
        try {
            System.loadLibrary("appFileTools")
        } catch (e: Throwable) {
        }
    }
}