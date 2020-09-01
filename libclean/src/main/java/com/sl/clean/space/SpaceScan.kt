package com.sl.clean.space

class SpaceScan(private val path: String, private val deep: Int) {

    interface ScanListener {
        fun onProgress(type: Int, path: String, size: Long, lastModifyTime: Long)
    }

    companion object {
        const val TYPE_VIDEO = 1
        const val TYPE_IMAGE = 2
        const val TYPE_AUDIO = 3
        const val TYPE_DOCUMENT = 4
        const val TYPE_APK = 5
    }

    private var token = -1L

    fun start(scanListener: ScanListener) {
        if (token != -1L) {
            return
        }

        try {
            token = nativeCreateScanSpace(path, deep)
            nativeStartScanSpace(token, scanListener)
        } catch (e: Throwable) {
        }

        cancel()
    }

    fun cancel() {
        if (token == -1L) {
            return
        }
        try {
            nativeCancelScanSpace(token)
            token = -1L
        } catch (e: Throwable) {
        }
    }

    private external fun nativeCreateScanSpace(path: String, deep: Int): Long

    private external fun nativeStartScanSpace(token: Long, callback: ScanListener)

    private external fun nativeCancelScanSpace(token: Long)

    init {
        try {
            System.loadLibrary("sl2clean")
        } catch (e: Throwable) {
        }
    }
}