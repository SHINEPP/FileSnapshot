package com.sh.app.modules.ossfile

import com.alibaba.sdk.android.oss.model.*
import com.sh.app.base.browsing.IBrowsingFile

class OssBrowsingFile(private val parent: OssBrowsingFile?, private val summary: OSSObjectSummary, private val name: String) : IBrowsingFile {

    private var activePosition = -1
    private var offsetDy = 0

    private var subFiles = ArrayList<OssBrowsingFile>()

    override fun isFile(): Boolean {
        if (subFiles.isNotEmpty()) {
            return false
        }

        return !summary.key.endsWith("/")
    }

    override fun getFileName(): String {
        return name
    }

    override fun getFilePath(): String {
        return summary.key
    }

    override fun getParent(): IBrowsingFile? {
        return parent
    }

    override fun getLastModifyTime(): Long {
        return summary.lastModified.time
    }

    override fun getSubCount(): Int {
        return subFiles.size
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        return subFiles
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

    fun updateOSSObjectSummary(summary: OSSObjectSummary) {
        this.summary.key = summary.key
        this.summary.bucketName = summary.bucketName
        this.summary.type = summary.type
        this.summary.eTag = summary.eTag
        this.summary.owner = summary.owner
        this.summary.lastModified = summary.lastModified
        this.summary.storageClass = summary.storageClass
        this.summary.size = summary.size
    }

    fun addSubOssBrowsingFile(browsingFile: OssBrowsingFile) {
        subFiles.add(browsingFile)
    }

    fun clearSubOssBrowsingFiles() {
        subFiles.clear()
    }
}