package com.sh.app.base.filetravel

import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FastTravelFile(val file: File, private val deep: Int = -1) {

    var parent: FastTravelFile? = null
        private set
    var lastChild: FastTravelFile? = null
        private set
    var nextBrother: FastTravelFile? = null
        private set

    private var needCount = 1
    private val finishedCount = AtomicInteger(0)
    private val firstVisit = AtomicBoolean(true)

    private var visitAction: ((node: FastTravelFile) -> Unit)? = null
    private var leaveAction: ((node: FastTravelFile) -> Unit)? = null

    fun setVisitAction(action: ((node: FastTravelFile) -> Unit)?) {
        this.visitAction = action
    }

    fun setLeaveAction(action: ((node: FastTravelFile) -> Unit)?) {
        this.leaveAction = action
    }

    fun start() {
        Thread { travel() }.start()
    }

    private fun travel() {
        if (firstVisit.compareAndSet(true, false)) {
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
                val subNode = FastTravelFile(subFile, deep - 1)
                subNode.attachParent(this)
                TravelThreadPool.execute { subNode.travel() }
            }
        }

        // 遍历子文件
        var subNode: FastTravelFile? = lastChild
        while (subNode != null) {
            subNode.travel()
            subNode = subNode.nextBrother
        }
    }

    private fun attachParent(parent: FastTravelFile?) {
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

    private fun performVisit(travelFile: FastTravelFile) {
        visitAction?.invoke(travelFile)
        parent?.performVisit(travelFile)
    }

    private fun performLeave(travelFile: FastTravelFile) {
        leaveAction?.invoke(travelFile)
        parent?.performLeave(travelFile)
    }
}