package com.sh.app.base.filetravel

import java.util.concurrent.atomic.AtomicInteger


class FileNode {

    var parent: FileNode? = null
        private set
    var lastChild: FileNode? = null
        private set
    var nextBrother: FileNode? = null
        private set

    var childCount = 0
    private val finishedCount = AtomicInteger(0)

    var path = ""
    var size = 0L

    private var finishedAction: ((fileNode: FileNode) -> Unit)? = null

    fun onSubFinished(action: ((fileNode: FileNode) -> Unit)?) {
        this.finishedAction = action
    }

    @Synchronized
    fun attachParent(parent: FileNode?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nextBrother = parentLastChild
        }
    }

    fun notifySubFinished() {
        if (finishedCount.addAndGet(1) >= childCount) {
            finishedAction?.invoke(this)
            parent?.notifySubFinished()
        }
    }

    @Synchronized
    fun reset() {
        parent = null
        lastChild = null
        nextBrother = null
        childCount = 1
        finishedCount.set(0)
    }
}