package com.sh.app.browsing

import java.io.File

class BrowsingPath(private val file: File) : IBrowsingFile {

    private var parent: BrowsingPath? = null
    private var subFiles: ArrayList<BrowsingPath>? = null

    private var activePosition = -1

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun getFileName(): String {
        return file.name
    }

    override fun getFilePath(): String {
        return file.path
    }

    override fun getParent(): IBrowsingFile? {
        return parent
    }

    override fun getLastModifyTime(): Long {
        return file.lastModified()
    }

    override fun getSubCount(): Int {
        checkSubFiles()
        return subFiles!!.size
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        checkSubFiles()
        return subFiles!!
    }

    override fun getActivePosition(): Int {
        return activePosition
    }

    override fun setActivePosition(position: Int) {
        activePosition = position
    }

    override fun clear() {
        subFiles = null
    }

    private fun checkSubFiles() {
        if (subFiles == null) {
            subFiles = ArrayList()
            if (!file.isFile) {
                for (item in file.listFiles()) {
                    val sub = BrowsingPath(item)
                    sub.parent = this
                    subFiles!!.add(sub)
                }
            }
        }
    }
}