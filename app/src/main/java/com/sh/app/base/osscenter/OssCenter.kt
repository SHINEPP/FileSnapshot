package com.sh.app.base.osscenter

import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.sh.app.OptApplication


object OssCenter {

    private const val TAG = "OSS_CENTER"

    private const val endpoint = "https://oss-cn-beijing.aliyuncs.com"
    private const val accessKeyId = "STS.NT5guJ53nbJB5CrX8eQTVHYua"
    private const val accessKeySecret = "B7D3ZFTG682KrMyyUJj9uX34u1mPiWHdVBJ3Xx7mb4hU"
    private const val securityToken = "CAIS/gF1q6Ft5B2yfSjIr5eALM/+2OxP1YipN2XDvDgwXdt6p5zegzz2IH5MfXNgCOgXtfo3nGtY6PwdlqVoRoReREvCKM1565kPK4cKrSOH6aKP9rUhpMCPKwr6UmzGvqL7Z+H+U6mqGJOEYEzFkSle2KbzcS7YMXWuLZyOj+wIDLkQRRLqL0AFZrFsKxBltdUROFbIKP+pKWSKuGfLC1dysQcO7gEa4K+kkMqH8Uic3h+oiM1t/tmqe8L4N5AxZMkkCYrpjNYbLPSRjHRijDFR77pzgaB+/jPKg8qQGVE54W/dbbKNqI0+dF4iOPBiQP4a9qfm/uZkofxfbxYWWer5VxqAATIZvUUjiGSuUFZs3AGmbC74QXCpW3fRBWhmdqw6wy+JWMcFnok9/WiwyYk4bA5eb7aZZfjrhfgNnfvNtOXyKiSjUu49jZ79R0WyjngRN8Ec4GaqdH/2xf+UW7lcXVxS7GQo/8aXWUu6KuOLb/Pz6t6f18X+QXh3HrggDuWLx8qz"

    const val bucketName = "app-sh-test"

    val oss: OSSClient

    init {
        OSSLog.enableLog()
        val provider = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求数，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        oss = OSSClient(OptApplication.context, endpoint, provider, conf)
    }
}