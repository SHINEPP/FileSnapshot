package com.sh.app.modules.space

import android.os.Environment
import android.util.Log
import com.sh.app.base.filetravel.FastTravelFile
import com.sh.app.utils.available
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class SpaceScanner {

    companion object {
        private const val TAG = "SPACE_SCANNER"
    }

    var isScanning = false
        private set
    var startTime = 0L
        private set

    val videoSize = AtomicLong(0L)
    val audioSize = AtomicLong(0L)
    val imageSize = AtomicLong(0L)
    val documentSize = AtomicLong(0L)
    val apkSize = AtomicLong(0L)

    fun start() {
        if (isScanning) {
            return
        }

        isScanning = true
        startTime = System.currentTimeMillis()
        videoSize.set(0L)
        audioSize.set(0L)
        imageSize.set(0L)
        documentSize.set(0L)
        apkSize.set(0L)

        val root = FastTravelFile(Environment.getExternalStorageDirectory())
        root.setVisitAction {
            if (it.file.isFile) {
                groupFile(it.file)
            }
        }
        root.setLeaveAction {
            if (it === root) {
                Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms")
                isScanning = false
            }
        }
        root.start()
    }

    private fun groupFile(file: File) {
        val path = file.path
        val extraName = path.substringAfterLast(".", "")
        when (extraName.toLowerCase(Locale.CHINA)) {
            // video
            "mp4", "mkv", "mpg", "mpeg", "mpe",
            "avi", "rm", "rmvb", "mov", "wmv",
            "vob", "divx", "asf", "3gp", "webm",
            "swf", "bdmv", "3gpp", "f4v", "xvid",
            "mpeg4"
            -> {
                Log.d(TAG, "groupFile(), video, path = $path")
                videoSize.addAndGet(file.available())
            }

            // image
            "png", "jpg", "jpeg", "gif", "psd",
            "svg", "ai", "ps", "tif", "tiff"
            -> {
                Log.d(TAG, "groupFile(), image, path = $path")
                imageSize.addAndGet(file.available())
            }

            // audio
            "mp3", "cda", "wav", "ape", "flac",
            "aac", "ogg", "wma", "m4a", "mid",
            "wave", "caf", "m4r", "m3u", "ac3",
            "mka"
            -> {
                Log.d(TAG, "groupFile(), audio, path = $path")
                audioSize.addAndGet(file.available())
            }

            // document
            "txt", "doc", "hlp", "wps", "ftf",
            "html", "pdf", "docx", "xls", "ppt",
            "pptx", "csv", "epub", "mobi", "rtf",
            "pages", "number", "key"
            -> {
                Log.d(TAG, "groupFile(), document, path = $path")
                documentSize.addAndGet(file.available())
            }

            // apk
            "apk" -> {
                Log.d(TAG, "groupFile(), apk, path = $path")
                apkSize.addAndGet(file.available())
            }

            "1" -> {
                Log.d(TAG, "groupFile(), apk, path = $path")
                if (path.endsWith(".apk.1")) {
                    apkSize.addAndGet(file.available())
                }
            }
        }
    }
}