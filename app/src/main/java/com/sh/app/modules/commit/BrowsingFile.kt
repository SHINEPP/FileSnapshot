package com.sh.app.modules.commit

import com.sh.app.browsing.IBrowsingFile
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.snapshot.ObjectFile

class BrowsingFile(private val parent: BrowsingFile?, private val objectFile: ObjectFile) :
    IBrowsingFile {

    private var subFiles: ArrayList<BrowsingFile>? = null

    private var activePosition = -1
    private var offsetDy = 0

    override fun isFile(): Boolean {
        return objectFile.isBlob
    }

    override fun getFileName(): String {
        return objectFile.name.substringAfter(SnapshotManager.sdcardFile.path)
    }

    override fun getFilePath(): String {
        return objectFile.getPath()
    }

    override fun getParent(): IBrowsingFile? {
        return parent
    }

    override fun getLastModifyTime(): Long {
        return objectFile.lastModifyTime
    }

    override fun getSubCount(): Int {
        return objectFile.getSubCount()
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        if (subFiles == null) {
            subFiles = ArrayList()
            for (objectFile in objectFile.getObjectFiles()) {
                subFiles!!.add(
                    BrowsingFile(
                        this,
                        objectFile
                    )
                )
            }
        }
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
        objectFile.clear()
        subFiles = null
    }
}