package com.sh.app.modules.space

import android.os.Environment
import com.sh.app.base.browsing.IBrowsingFile
import com.sh.app.base.filetravel.FileNode
import com.sh.app.utils.available
import java.io.File

class SpaceFile(private val parent: SpaceFile?, private val node: FileNode) : IBrowsingFile {

    private var activePosition = -1
    private var offsetDy = 0

    private var size = -1L

    private var subList: ArrayList<SpaceFile>? = null

    private val file: File

    init {
        file = when {
            node.file != null -> {
                node.file!!
            }
            parent == null -> {
                Environment.getExternalStorageDirectory()
            }
            else -> {
                File(parent.file, node.name)
            }
        }
    }

    override fun isFile(): Boolean {
        return node.file?.isFile ?: false
    }

    override fun getFileName(): String {
        return node.name
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
        if (subList != null) {
            return subList?.size ?: 0
        }

        var count = 0
        var node = node.lastChild
        while (node != null) {
            count += 1
            node = node.nextBrother
        }
        return count
    }

    override fun getSize(): Long {
        if (size == -1L) {
            size = file.available()
        }
        return size
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        if (subList == null) {
            subList = ArrayList()

            var childNode = node.lastChild
            while (childNode != null) {
                subList!!.add(SpaceFile(this, childNode))
                childNode = childNode.nextBrother
            }
        }

        return subList!!
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
}