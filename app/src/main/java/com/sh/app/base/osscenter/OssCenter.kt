package com.sh.app.base.osscenter

import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.sh.app.OptApplication
import com.sh.app.utils.FileDownloader
import java.io.File


object OssCenter {

    private const val TAG = "OSS_CENTER"

    private const val INFO_URL = "https://zzl-share.oss-cn-beijing.aliyuncs.com/sts_content"

    private const val endpoint = "https://oss-cn-beijing.aliyuncs.com"
    const val bucketName1 = "app-sh-test"
    const val bucketName2 = "zzl-resource"
    const val bucketName3 = "zzl-share"

    private var stsPath = ""

    init {
        OSSLog.enableLog()
    }

    private fun createOSS(): OSS {
        var accessKeyId = ""
        var accessKeySecret = ""
        var securityToken = ""

        val stsDirFile = OptApplication.context.getDir("sts_info", Context.MODE_PRIVATE)
        if (!stsDirFile.exists()) {
            stsDirFile.mkdirs()
        }
        stsPath = File(stsDirFile, "sts").path
        val stsFile = File(stsPath)
        if (stsFile.exists()) {
            val values = stsFile.readText().split(",")
            if (values.size >= 3) {
                accessKeyId = values[0]
                accessKeySecret = values[1]
                securityToken = values[2]
            }
        }

        val provider = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求数，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        return OSSClient(OptApplication.context, endpoint, provider, conf)
    }

    fun withOSS(action: (OSS?) -> Unit) {
        try {
            action(createOSS())
        } catch (e: Throwable) {
            Log.d(TAG, "getOSS(), e = $e")

            FileDownloader(INFO_URL, stsPath).load(object : FileDownloader.DownloaderListener {
                override fun onFinished(e: Exception?) {
                    try {
                        action(createOSS())
                    } catch (e: Throwable) {
                        action(null)
                    }
                }
            })
        }
    }
}