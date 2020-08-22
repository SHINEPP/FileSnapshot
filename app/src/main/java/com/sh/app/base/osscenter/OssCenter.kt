package com.sh.app.base.osscenter

import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.sh.app.OptApplication


object OssCenter {

    private const val endpoint = "https://oss-cn-beijing.aliyuncs.com"
    private const val accessKeyId = "STS.NULaaTJkQjHH4TAC1S9yZ4KPb"
    private const val accessKeySecret = "CzpszgdmGEY9ghGi8ofdRutJzJ1j3PXCGDfKkiaffJrc"
    private const val securityToken = "CAIS/gF1q6Ft5B2yfSjIr5b5Ktvgp7Rw3YqjNnLwpzEGNfZ22477gDz2IH5MfXNgCOgXtfo3nGtY6PwdlqVoRoReREvCKM1565kPTpVRnCOH6aKP9rUhpMCPKwr6UmzGvqL7Z+H+U6mqGJOEYEzFkSle2KbzcS7YMXWuLZyOj+wIDLkQRRLqL0AFZrFsKxBltdUROFbIKP+pKWSKuGfLC1dysQcO7gEa4K+kkMqH8Uic3h+oiM1t/tmqe8L4N5AxZMkkCYrpjNYbLPSRjHRijDFR77pzgaB+/jPKg8qQGVE54W/dbbKNqI0+dF4iOPBiQP4a9qfm/uZkofxfbxYWWer5VxqAAaW6lget1qGfqOqFftSd5VzPMbGGlZjXwWS/jbjukNc9pJCyKHYjSGOqpmU4NJRtwwBX6K/gWtG6g8vUWnGapR8kmMmSHFCnlVOnOJXmt7XsEGLyaVrPU4R38zmpSMgWYVOYbeKNL+EQOpq9h45yGyL7kFwx06gUNHxC0Tm0svPu"

    const val bucketName = "app-sh-test"

    val oss: OSSClient

    init {
        OSSLog.enableLog() //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv

        val credentialProvider = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求数，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次

        oss = OSSClient(OptApplication.context, endpoint, credentialProvider, conf)
    }
}