package com.sh.app.modules.ossfile

import android.util.Log
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.OSSObjectSummary
import com.sh.app.base.osscenter.OssCenter

class OssBrowsingRoot(parent: OssBrowsingFile?, private val bucketName: String, private val prefix: String) {

    companion object {
        private const val TAG = "OSS_BROWSING_ROOT"
    }

    private var rootFile: OssBrowsingFile

    init {
        val rootSummary = OSSObjectSummary()
        rootSummary.bucketName = bucketName
        rootSummary.key = prefix
        rootFile = OssBrowsingFile(parent, rootSummary, prefix)
    }

    fun refresh(complete: (OssBrowsingFile) -> Unit) {
        OssCenter.withOSS { oss ->
            if (oss == null) {
                complete(rootFile)
                return@withOSS
            }

            rootFile.clearSubOssBrowsingFiles()

            var maker = ""
            for (i in 0..9) {
                val request = ListObjectsRequest(bucketName)
                request.marker = maker
                request.maxKeys = 100
                request.prefix = prefix

                val result = oss.listObjects(request)
                val summaryList = result.objectSummaries
                summaryList.forEach {
                    handleSummary(it)
                }

                maker = result.nextMarker ?: ""
                if (!result.isTruncated) {
                    break
                }
            }

            complete(rootFile)
        }
    }

    private fun handleSummary(summary: OSSObjectSummary) {
        Log.d(TAG, "handleSummary(), key = ${summary.key}")

        var browsingFile = rootFile
        val nameList = summary.key.split("/")
        for (name in nameList) {
            if (name.isEmpty()) {
                continue
            }
            var matchedFile: OssBrowsingFile? = null
            for (file in browsingFile.getBrowsingFiles()) {
                if (file.getFileName() == name && file is OssBrowsingFile) {
                    matchedFile = file
                }
            }

            if (matchedFile == null) {
                matchedFile = OssBrowsingFile(browsingFile, OSSObjectSummary(), name)
                browsingFile.addSubOssBrowsingFile(matchedFile)
            }

            browsingFile = matchedFile
        }

        browsingFile.updateOSSObjectSummary(summary)
    }
}