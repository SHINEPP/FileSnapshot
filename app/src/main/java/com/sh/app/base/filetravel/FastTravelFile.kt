package com.sh.app.base.filetravel

import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FastTravelFile(file: File, private var deep: Int = -1) {

    companion object {

        private val poolSync = Any()
        private var recycleNode: FastTravelFile? = null

        private fun obtain(file: File, deep: Int): FastTravelFile {
            var node: FastTravelFile?

            synchronized(poolSync) {
                node = recycleNode
                recycleNode = recycleNode?.nextBrother
            }

            if (node == null) {
                return FastTravelFile(file, deep)
            }

            node!!.file = file
            node!!.deep = deep
            node!!.parent = null
            node!!.lastChild = null
            node!!.nextBrother = null
            node!!.needCount = 1
            node!!.finishedCount.set(0)
            node!!.firstVisit.set(true)
            node!!.visitAction = null
            node!!.leaveAction = null

            return node!!
        }

        private fun recycle(node: FastTravelFile) {
            var cNode = node
            while (cNode.nextBrother != null) {
                cNode = cNode.nextBrother!!
            }
            cNode.nextBrother = recycleNode
            recycleNode = node
        }

        fun clearPool() {
            synchronized(poolSync) {
                recycleNode = null
            }
        }
    }

    var file: File
        private set

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

    init {
        this.file = file
    }

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
                val subNode = obtain(subFile, deep - 1)
                subNode.attachParent(this)
                TravelThreadPool.execute { subNode.travel() }
            }
        }

        // 遍历子文件
        var subNode: FastTravelFile? = lastChild
        while (subNode != null) {
            var isRecycle = false
            synchronized(poolSync) {
                if (lastChild == null) {
                    isRecycle = true
                }
            }
            if (isRecycle) {
                break
            }
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
            synchronized(poolSync) {
                if (lastChild != null) {
                    recycle(lastChild!!)
                    lastChild = null
                }
            }
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