package com.sh.app.base.snapshot


class FileNode {
    var parent: FileNode? = null
        private set
    var lastChild: FileNode? = null
        private set
    var nexNode: FileNode? = null
        private set

    var totalCount = 1
    private var finishedCount = 0

    var path = ""
    var size = 0L

    private var finishedAction: ((fileNode: FileNode) -> Unit)? = null

    fun onFinished(action: ((fileNode: FileNode) -> Unit)?) {
        this.finishedAction = action
    }

    @Synchronized
    fun attachParent(parent: FileNode?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nexNode = parentLastChild
        }
    }

    @Synchronized
    fun notifyFinished() {
        finishedCount += 1
        if (totalCount == finishedCount) {
            finishedAction?.invoke(this)
            parent?.notifyFinished()
        }
    }

    @Synchronized
    fun reset() {
        parent = null
        lastChild = null
        nexNode = null
        totalCount = 1
        finishedCount = 0
    }
}