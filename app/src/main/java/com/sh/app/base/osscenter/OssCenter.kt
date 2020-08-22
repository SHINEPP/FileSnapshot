package com.sh.app.base.osscenter

import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.sh.app.OptApplication
import com.sh.app.utils.FileDownloader
import java.io.File
import java.util.*


object OssCenter {

    private const val TAG = "OSS_CENTER"

    private const val INFO_URL = "https://zzl-share.oss-cn-beijing.aliyuncs.com/sts_content"

    private const val endpoint = "https://oss-cn-beijing.aliyuncs.com"
    const val bucketName = "app-sh-test"

    private var oss: OSSClient? = null

    private var stsPath = ""
    private var downloader: FileDownloader? = null

    private var accessKeyId = ""
    private var accessKeySecret = ""
    private var securityToken = ""

    init {
        OSSLog.enableLog()
        updateSTSValue()
    }

    fun getOSS(): OSS {
        if (oss == null) {
            val provider = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
            val conf = ClientConfiguration()
            conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
            conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
            conf.maxConcurrentRequest = 5 // 最大并发请求数，默认5个
            conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
            oss = OSSClient(OptApplication.context, endpoint, provider, conf)
        }

        return oss!!
    }

    private fun updateSTSValue() {
        val file = OptApplication.context.getDir("sts_info", Context.MODE_PRIVATE)
        if (!file.exists()) {
            file.mkdirs()
        }
        stsPath = File(file, "sts").path
        val stsFile = File(stsPath)

        if (stsFile.exists()) {
            val values = stsFile.readText().split(",")
            if (values.size >= 3) {
                accessKeyId = values[0]
                accessKeySecret = values[1]
                securityToken = values[2]
            }
        }
    }

    fun startCheckTimer() {
        Timer(true).schedule(object : TimerTask() {
            override fun run() {
                checkToLoadSTSInfo()
            }
        }, 0, 3600 * 1000L)
    }

    private fun checkToLoadSTSInfo() {
        val stsFile = File(stsPath)
        if (!stsFile.exists() || (System.currentTimeMillis() - stsFile.lastModified()) > 3600 * 1000) {
            if (downloader == null) {
                downloader = FileDownloader(INFO_URL, stsFile)
            }

            Log.d(TAG, "checkToUpdateSTSInfo(), load")
            downloader?.load(object : FileDownloader.DownloaderListener {
                override fun onFinished(e: Exception?) {
                    oss = null
                    updateSTSValue()
                }
            })
        }
    }
}