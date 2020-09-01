package com.sh.app.modules.space

import android.os.Environment
import android.util.Log
import com.sh.app.base.filewalk.TreeFile
import com.sl.clean.space.SpaceScan
import java.io.File

class SpaceScanTask {

    companion object {
        private const val TAG = "SPACE_SCANNER"
    }

    var isScanning = false
        private set
    var startTime = 0L
        private set

    var videoSize = 0L
        private set
    var audioSize = 0L
        private set
    var imageSize = 0L
        private set
    var documentSize = 0L
        private set
    var apkSize = 0L
        private set

    private val sdcardPath = Environment.getExternalStorageDirectory().path

    val videoRoot = TreeFile(null, "Video", sdcardPath, 0L)
    val audioRoot = TreeFile(null, "Audio", sdcardPath, 0L)
    val imageRoot = TreeFile(null, "Image", sdcardPath, 0L)
    val documentRoot = TreeFile(null, "Document", sdcardPath, 0L)
    val apkRoot = TreeFile(null, "Apk", sdcardPath, 0L)

    private val spaceScan = SpaceScan(sdcardPath, 8)

    fun start() {
        Thread { startInner() }.start()
    }

    private fun startInner() {
        if (isScanning) {
            return
        }

        isScanning = true
        startTime = System.currentTimeMillis()

        videoSize = 0L
        audioSize = 0L
        imageSize = 0L
        documentSize = 0L
        apkSize = 0L

        videoRoot.reset()
        audioRoot.reset()
        imageRoot.reset()
        documentRoot.reset()
        apkRoot.reset()

        spaceScan.scan(object : SpaceScan.ScanListener {
            override fun onProgress(type: Int, path: String, size: Long, lastModifyTime: Long) {
                when (type) {
                    SpaceScan.TYPE_VIDEO -> {
                        videoSize += size
                        videoRoot.add(path.substringAfter(sdcardPath), path, size)
                    }
                    SpaceScan.TYPE_AUDIO -> {
                        audioSize += size
                        audioRoot.add(path.substringAfter(sdcardPath), path, size)
                    }
                    SpaceScan.TYPE_IMAGE -> {
                        imageSize += size
                        imageRoot.add(path.substringAfter(sdcardPath), path, size)
                    }
                    SpaceScan.TYPE_DOCUMENT -> {
                        documentSize += size
                        documentRoot.add(path.substringAfter(sdcardPath), path, size)
                    }
                    SpaceScan.TYPE_APK -> {
                        apkSize += size
                        apkRoot.add(path.substringAfter(sdcardPath), path, size)
                    }
                }
            }
        })

        isScanning = false

        Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms")
    }
}