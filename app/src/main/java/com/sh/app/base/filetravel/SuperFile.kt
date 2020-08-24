package com.sh.app.base.filetravel

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class SuperFile(val file: File) {

    var parent: SuperFile? = null
        private set
    var lastChild: SuperFile? = null
        private set
    var nextBrother: SuperFile? = null
        private set

    private var needCount = 1
    private val finishedCount = AtomicInteger(0)

    private var acceptAction: ((fileNode: SuperFile) -> Boolean)? = null
    private var leaveAction: ((fileNode: SuperFile) -> Unit)? = null

    fun onAccept(action: ((fileNode: SuperFile) -> Boolean)?) {
        this.acceptAction = action
    }

    fun onLeave(action: ((fileNode: SuperFile) -> Unit)?) {
        this.leaveAction = action
    }

    fun travel() {
        val isAccept = performAccept(this)
        if (!isAccept) {
            notifySubFinished()
            return
        }

        if (file.isFile) {
            notifySubFinished()
            return
        }

        val files = file.listFiles()
        if (files == null || files.isEmpty()) {
            notifySubFinished()
            return
        }

        needCount = files.size + 1
        for (subFile in files) {
            val subNode = SuperFile(subFile)
            subNode.attachParent(this)
            subNode.travel()
        }
    }

    @Synchronized
    fun attachParent(parent: SuperFile?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nextBrother = parentLastChild
        }
    }

    private fun notifySubFinished() {
        if (finishedCount.addAndGet(1) == needCount) {
            performLeave(this)
            parent?.notifySubFinished()
        }
    }

    private fun performAccept(superFile: SuperFile): Boolean {
        val cAccept = acceptAction?.invoke(superFile) ?: true
        val pAccept = parent?.performAccept(superFile) ?: true
        return cAccept && pAccept
    }

    private fun performLeave(superFile: SuperFile) {
        leaveAction?.invoke(superFile)
        parent?.performLeave(superFile)
    }
}