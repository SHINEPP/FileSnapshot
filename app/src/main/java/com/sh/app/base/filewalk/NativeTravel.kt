package com.sh.app.base.filewalk

import android.os.Environment
import android.util.Log
import com.sh.app.utils.NativeUtils

class NativeTravel {

    companion object {
        private const val TAG = "NATIVE_TRAVEL"
    }

    fun test() {
        Thread {
            val startTime = System.currentTimeMillis()
            printDir(Environment.getExternalStorageDirectory().path)
            Log.d(TAG, "Native wrapper duration = ${System.currentTimeMillis() - startTime}ms")
        }.start()
    }

    private fun printDir(path: String) {
        val dir = NativeUtils.openDir(path)

        NativeUtils.chDir(path)
        var dirent = NativeUtils.readDir(dir)
        Log.d(TAG, "dirent = $dirent")

        while ((dirent) != -1L) {
            val type = NativeUtils.getDirentType(dirent)
            val name = NativeUtils.getDirentName(dirent)
            Log.d(TAG, "type = $type, name = $name")

            if (type == 4 && name != null && name != "." && name != "..") {
                printDir(name)
            }

            dirent = NativeUtils.readDir(dir)
        }

        NativeUtils.chDir("..")
        NativeUtils.closeDir(dir)
    }
}