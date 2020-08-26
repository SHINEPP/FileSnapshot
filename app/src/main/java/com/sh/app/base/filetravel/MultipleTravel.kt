package com.sh.app.base.filetravel

import android.util.Log
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.ThreadPoolManager
import com.sh.app.utils.available
import com.sh.app.utils.formatFileSize
import java.util.concurrent.atomic.AtomicLong

object MultipleTravel {

    private const val TAG = "MULTIPLE_TRAVEL"

    fun start() {
        val superFile = BTravelFile(SnapshotManager.sdcardFile)
        val startTime = System.currentTimeMillis()
        val totalSize = AtomicLong(0L)
        superFile.onVisit {
            if (it.file.isFile) {
                totalSize.addAndGet(it.file.available())
            }
            Log.d(TAG, "visit, size = ${totalSize.get().formatFileSize()}")
        }
        superFile.onLeave { item ->
            if (item === superFile) {
                Log.d(TAG, "leave, duration = ${System.currentTimeMillis() - startTime}ms")
            }
        }
        superFile.start()
    }
}