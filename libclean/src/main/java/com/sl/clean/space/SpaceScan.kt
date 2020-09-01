package com.sl.clean.space

class SpaceScan(private val path: String, private val deep: Int) {

    interface ScanListener {
        fun onProgress(type: Int, path: String, size: Long, lastModifyTime: Long)
    }

    companion object {
        const val TYPE_VIDEO = 0x01
        const val TYPE_IMAGE = 0x02
        const val TYPE_AUDIO = 0x04
        const val TYPE_DOCUMENT = 0x08
        const val TYPE_APK = 0x10
    }

    private var token = -1L

    fun scan(scanListener: ScanListener) {
        scan(0x1f, scanListener)
    }

    fun scanApk(scanListener: ScanListener) {
        scan(TYPE_APK, scanListener)
    }

    private fun scan(flag: Int, scanListener: ScanListener) {
        if (token != -1L) {
            return
        }

        try {
            token = nativeCreateScanSpace(path, deep)
            nativeStartScanSpace(token, flag, scanListener)
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

    private external fun nativeStartScanSpace(token: Long, flag: Int, callback: ScanListener)

    private external fun nativeCancelScanSpace(token: Long)

    init {
        try {
            System.loadLibrary("sl2clean")
        } catch (e: Throwable) {
        }
    }
}