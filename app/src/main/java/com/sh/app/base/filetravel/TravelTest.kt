package com.sh.app.base.filetravel

import android.util.Log
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.available
import com.sh.app.utils.formatFileSize
import java.io.File
import java.util.concurrent.atomic.AtomicLong

object TravelTest {

    private const val TAG = "MULTIPLE_TRAVEL"

    private var size = 0L
    private var count = 0

    fun startMultipleTravel() {
        val superFile = FastTravelFile(SnapshotManager.sdcardFile)
        val startTime = System.currentTimeMillis()
        val totalSize = AtomicLong(0L)
        superFile.setVisitAction {
            if (it.file.isFile) {
                totalSize.addAndGet(it.file.available())
            }
            Log.d(TAG, "size = ${totalSize.get().formatFileSize()}")
        }
        superFile.setLeaveAction { item ->
            if (item === superFile) {
                Log.d(TAG, "duration = ${System.currentTimeMillis() - startTime}ms")
            }
        }
        superFile.start()
    }

    fun startSingleTravel() {
        Thread {
            val startTime = System.currentTimeMillis()
            size = 0L
            count = 0
            travel(SnapshotManager.sdcardFile, -1)
            Log.d(TAG, "duration = ${System.currentTimeMillis() - startTime}")
        }.start()
    }

    private fun travel(file: File, deep: Int) {
        if (deep == 0) {
            return
        }

        if (file.isFile) {
            size += file.available()
        }
        Log.d(TAG, "size = ${size.formatFileSize()}")
        if (file.isFile) {
            return
        }

        val files = file.listFiles() ?: return
        files.forEach { travel(it, deep - 1) }
    }
}