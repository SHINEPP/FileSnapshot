package com.sh.app.modules.space

import android.os.Environment
import android.util.Log
import com.sh.app.base.filewalk.WalkFile
import com.sh.app.base.filewalk.TreeFile
import com.sh.app.utils.nativeGetSize
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.HashSet

class SpaceScanTask {

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

    val videoRoot = TreeFile(null, "Video", null, 0L)
    val audioRoot = TreeFile(null, "Audio", null, 0L)
    val imageRoot = TreeFile(null, "Image", null, 0L)
    val documentRoot = TreeFile(null, "Document", null, 0L)
    val apkRoot = TreeFile(null, "Apk", null, 0L)

    private val sdcardPath = Environment.getExternalStorageDirectory().path

    private val videoSet = HashSet<String>()
    private val audioSet = HashSet<String>()
    private val imageSet = HashSet<String>()
    private val documentSet = HashSet<String>()

    init {
        videoSet.add("mp4")
        videoSet.add("mkv")
        videoSet.add("mpg")
        videoSet.add("mpeg")
        videoSet.add("mpe")
        videoSet.add("avi")
        videoSet.add("rm")
        videoSet.add("rmvb")
        videoSet.add("mov")
        videoSet.add("wmv")
        videoSet.add("vob")
        videoSet.add("divx")
        videoSet.add("asf")
        videoSet.add("3gp")
        videoSet.add("webm")
        videoSet.add("swf")
        videoSet.add("bdmv")
        videoSet.add("3gpp")
        videoSet.add("f4v")
        videoSet.add("xvid")
        videoSet.add("mpeg4")

        imageSet.add("png")
        imageSet.add("jpg")
        imageSet.add("jpeg")
        imageSet.add("gif")
        imageSet.add("psd")
        imageSet.add("svg")
        imageSet.add("ai")
        imageSet.add("ps")
        imageSet.add("tif")
        imageSet.add("tiff")

        audioSet.add("mp3")
        audioSet.add("cda")
        audioSet.add("wav")
        audioSet.add("ape")
        audioSet.add("flac")
        audioSet.add("aac")
        audioSet.add("ogg")
        audioSet.add("wma")
        audioSet.add("m4a")
        audioSet.add("mid")
        audioSet.add("wave")
        audioSet.add("caf")
        audioSet.add("m4r")
        audioSet.add("m3u")
        audioSet.add("ac3")
        audioSet.add("mka")

        documentSet.add("txt")
        documentSet.add("doc")
        documentSet.add("hlp")
        documentSet.add("wps")
        documentSet.add("ftf")
        documentSet.add("html")
        documentSet.add("pdf")
        documentSet.add("docx")
        documentSet.add("xls")
        documentSet.add("ppt")
        documentSet.add("pptx")
        documentSet.add("csv")
        documentSet.add("epub")
        documentSet.add("mobi")
        documentSet.add("rtf")
    }

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

        val root = WalkFile(Environment.getExternalStorageDirectory())
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
        val extraName = path.substringAfterLast(".", "").toLowerCase(Locale.CHINA)

        // document
        if (documentSet.contains(extraName)) {
            //Log.d(TAG, "groupFile(), document, path = $path")
            val size = file.nativeGetSize()
            documentSize.addAndGet(size)
            synchronized(documentRoot) {
                documentRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }

        // image
        if (imageSet.contains(extraName)) {
            //Log.d(TAG, "groupFile(), image, path = $path")
            val size = file.nativeGetSize()
            imageSize.addAndGet(size)
            synchronized(imageRoot) {
                imageRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }

        // audio
        if (audioSet.contains(extraName)) {
            //Log.d(TAG, "groupFile(), audio, path = $path")
            val size = file.nativeGetSize()
            audioSize.addAndGet(size)
            synchronized(audioRoot) {
                audioRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }

        // video
        if (videoSet.contains(extraName)) {
            //Log.d(TAG, "groupFile(), video, path = $path")
            val size = file.nativeGetSize()
            videoSize.addAndGet(size)
            synchronized(videoRoot) {
                videoRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }

        // apk
        if (extraName == "apk") {
            Log.d(TAG, "groupFile(), apk, path = $path")
            val size = file.nativeGetSize()
            apkSize.addAndGet(size)
            synchronized(apkRoot) {
                apkRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }

        if (extraName == "1" && path.endsWith(".apk.1")) {
            Log.d(TAG, "groupFile(), apk, path = $path")
            val size = file.nativeGetSize()
            apkSize.addAndGet(size)
            synchronized(apkRoot) {
                apkRoot.add(file.path.substringAfter(sdcardPath), file, size)
            }
            return
        }
    }
}