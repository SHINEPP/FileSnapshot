package com.sh.app.base.filetravel


class FileNode(parent: FileNode?, val name: String, file: SlFile?, size: Long) {

    var parent: FileNode? = null
        private set
    var lastChild: FileNode? = null
        private set
    var nextBrother: FileNode? = null
        private set

    var file: SlFile?
        private set
    var size = 0L
        private set

    init {
        this.file = file
        this.size = size
        attachParent(parent)
    }

    fun add(path: String, file: SlFile, size: Long) {
        val names = path.split("/")
        var node = this
        for (cName in names) {
            if (cName.isEmpty()) {
                continue
            }
            node.size += size
            node = node.findChild(cName) ?: FileNode(node, cName, null, 0L)
        }

        node.file = file
        node.size = size
    }

    private fun findChild(name: String): FileNode? {
        var child = lastChild
        while (child != null) {
            if (child.name == name) {
                break
            }
            child = child.nextBrother
        }
        return child
    }

    private fun attachParent(parent: FileNode?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nextBrother = parentLastChild
        }
    }
}