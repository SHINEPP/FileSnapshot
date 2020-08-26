package com.sh.app.modules.sdcard

import com.sh.app.base.browsing.IBrowsingFile
import java.io.File

class BrowsingFile(private val file: File) : IBrowsingFile {

    private var parent: BrowsingFile? = null
    private var subFiles: ArrayList<BrowsingFile>? = null

    private var activePosition = -1
    private var offsetDy = 0

    private var size = 0L

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

    override fun getSize(): Long {
        if (size == 0L && file.isFile) {
            val inputStream = file.inputStream()
            size = inputStream.available().toLong()
            inputStream.close()
        }
        return size
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

    override fun getActiveOffsetDy(): Int {
        return offsetDy
    }

    override fun setActiveOffsetDy(offsetDy: Int) {
        this.offsetDy = offsetDy
    }

    override fun clear() {
        subFiles = null
    }

    private fun checkSubFiles() {
        if (subFiles == null) {
            subFiles = ArrayList()
            if (!file.isFile) {
                for (item in file.listFiles()) {
                    val sub = BrowsingFile(item)
                    sub.parent = this
                    subFiles!!.add(sub)
                }
            }
        }
    }
}