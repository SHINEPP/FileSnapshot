package com.sh.app.base.filetravel

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class BTravelFile(val file: File, private val deep: Int = -1) {

    var parent: BTravelFile? = null
        private set
    var lastChild: BTravelFile? = null
        private set
    var nextBrother: BTravelFile? = null
        private set

    private var needCount = 1
    private val finishedCount = AtomicInteger(0)

    private var visitAction: ((node: BTravelFile) -> Unit)? = null
    private var leaveAction: ((node: BTravelFile) -> Unit)? = null

    fun onVisit(action: ((node: BTravelFile) -> Unit)?) {
        this.visitAction = action
    }

    fun onLeave(action: ((node: BTravelFile) -> Unit)?) {
        this.leaveAction = action
    }

    fun start() {
        run()
    }

    private fun run() {
        nextBrother?.start()

        performVisit(this)

        if (deep == 0 || file.isFile) {
            notifySubFinished()
            return
        }

        val files = file.listFiles()
        if (files == null || files.isEmpty()) {
            notifySubFinished()
            return
        }

        needCount = files.size
        for (subFile in files) {
            val subNode = BTravelFile(subFile, deep - 1)
            subNode.attachParent(this)
        }

        lastChild?.run()
    }

    @Synchronized
    fun attachParent(parent: BTravelFile?) {
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

    private fun performVisit(travelFile: BTravelFile) {
        visitAction?.invoke(travelFile)
        parent?.performVisit(travelFile)
    }

    private fun performLeave(travelFile: BTravelFile) {
        leaveAction?.invoke(travelFile)
        parent?.performLeave(travelFile)
    }
}