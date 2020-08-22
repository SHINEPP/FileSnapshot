package com.sh.app.modules.ossfile

import android.util.Log
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.OSSObjectSummary
import com.sh.app.base.browsing.IBrowsingFile
import com.sh.app.base.osscenter.OssCenter

class ShOssFile private constructor(private val parent: ShOssFile?, ossObjSummary: OSSObjectSummary?) : IBrowsingFile {

    companion object {

        private const val TAG = "SH_OSS_FILE"

        fun createRoot(): ShOssFile {

            val rootFile = ShOssFile(null, null)
            rootFile.fileName = OssCenter.bucketName

            var maker = ""
            while (true) {
                Log.d(TAG, "createRoot(), maker = $maker")
                val request = ListObjectsRequest(OssCenter.bucketName)
                //request.prefix = ""
                request.marker = maker
                request.maxKeys = 100
                //request.delimiter = "/"

                val objList = OssCenter.oss.listObjects(request)
                val objSummary = objList.objectSummaries
                objSummary.forEach {
                    rootFile.getSubFiles().add(ShOssFile(rootFile, it))
                }

                maker = objList.nextMarker ?: ""
                if (!objList.isTruncated) {
                    break
                }
            }

            return rootFile
        }
    }

    private var lastModifyTime = 0L
    private var fileName = ""

    private var activePosition = -1
    private var offsetDy = 0

    private var subFiles: ArrayList<ShOssFile>? = null

    init {
        if (ossObjSummary != null) {
            fileName = ossObjSummary.key
            lastModifyTime = ossObjSummary.lastModified.time
        }
    }

    override fun isFile(): Boolean {
        return getSubFiles().isEmpty()
    }

    override fun getFileName(): String {
        return fileName
    }

    override fun getFilePath(): String {
        return ""
    }

    override fun getParent(): IBrowsingFile? {
        return parent
    }

    override fun getLastModifyTime(): Long {
        return lastModifyTime
    }

    override fun getSubCount(): Int {
        return getSubFiles().size
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        return getSubFiles()
    }

    override fun getActivePosition(): Int {
        return activePosition
    }

    override fun setActivePosition(position: Int) {
        activePosition = position
    }

    override fun getActiveOffsetDy(): Int {
        return offsetDy
    }

    override fun setActiveOffsetDy(offsetDy: Int) {
        this.offsetDy = offsetDy
    }

    override fun clear() {

    }

    private fun getSubFiles(): ArrayList<ShOssFile> {
        if (subFiles == null) {
            subFiles = ArrayList()
        }
        return subFiles!!
    }
}