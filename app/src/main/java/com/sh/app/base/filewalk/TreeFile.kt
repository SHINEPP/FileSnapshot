package com.sh.app.base.filewalk


class TreeFile(parent: TreeFile?, val name: String, absPath: String, size: Long) {

    var parent: TreeFile? = null
        private set
    var lastChild: TreeFile? = null
        private set
    var nextBrother: TreeFile? = null
        private set

    var absPath: String
        private set
    var size = 0L
        private set

    init {
        this.absPath = absPath
        this.size = size
        attachParent(parent)
    }

    fun add(path: String, absPath: String, size: Long) {
        val names = path.split("/")
        var node = this
        for (cName in names) {
            if (cName.isEmpty()) {
                continue
            }
            node.size += size
            node = node.findChild(cName) ?: TreeFile(node, cName, parent?.absPath+"/$cName", 0L)
        }

        node.absPath = absPath
        node.size = size
    }

    private fun findChild(name: String): TreeFile? {
        var child = lastChild
        while (child != null) {
            if (child.name == name) {
                break
            }
            child = child.nextBrother
        }
        return child
    }

    private fun attachParent(parent: TreeFile?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nextBrother = parentLastChild
        }
    }

    fun reset() {
        parent = null
        lastChild = null
        nextBrother = null
        size = 0L
    }
}