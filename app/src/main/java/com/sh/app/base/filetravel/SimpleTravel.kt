package com.sh.app.base.filetravel

import android.util.Log
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.ThreadPoolManager
import com.sh.app.utils.available
import com.sh.app.utils.formatFileSize
import java.io.File

object SimpleTravel {

    private const val TAG = "SIMPLE_TRAVEL"

    private var size = 0L
    private var count = 0

    fun start() {
        ThreadPoolManager.requestExecute {
            val startTime = System.currentTimeMillis()
            size = 0L
            count = 0
            travel(SnapshotManager.sdcardFile, -1)
            Log.d(TAG, "travel(), duration = ${System.currentTimeMillis() - startTime}")
        }
    }

    private fun travel(file: File, deep: Int) {
        if (deep == 0) {
            return
        }

        if (file.isFile) {
            size += file.available()
        }
        Log.d(TAG, "travel(), size = ${size.formatFileSize()}")
        if (file.isFile) {
            return
        }

        val files = file.listFiles() ?: return
        files.forEach { travel(it, deep - 1) }
    }
}