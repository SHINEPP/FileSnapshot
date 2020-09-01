package com.sh.app.modules.space

import com.sh.app.base.browsing.IBrowsingFile
import com.sh.app.base.filewalk.TreeFile
import java.io.File

class BrowsingFile(private val parent: BrowsingFile?, private val node: TreeFile) : IBrowsingFile {

    private var activePosition = -1
    private var offsetDy = 0

    private var subList: ArrayList<BrowsingFile>? = null

    private val file = File(node.absPath)

    override fun isFile(): Boolean {
        return file.isFile
    }

    override fun getFileName(): String {
        return node.name
    }

    override fun getFilePath(): String {
        return node.absPath
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
        return node.size
    }

    override fun getBrowsingFiles(): List<IBrowsingFile> {
        if (subList == null) {
            subList = ArrayList()

            var childNode = node.lastChild
            while (childNode != null) {
                subList!!.add(BrowsingFile(this, childNode))
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