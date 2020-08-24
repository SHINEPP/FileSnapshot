package com.sh.app.base.snapshot

import android.util.Log
import com.sh.app.utils.ThreadPoolManager
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


class SnapshotNode(private val file: File?, val name: String) {

    companion object {
        private const val TAG = "SNAPSHOT_NODE"
    }

    var parent: SnapshotNode? = null
        private set
    var lastChild: SnapshotNode? = null
        private set
    var nexNode: SnapshotNode? = null
        private set

    var sha1 = ""
        private set

    var childCount = 0
    private var finishedCount = AtomicInteger(0)

    private var finishedAction: ((fileNode: SnapshotNode) -> Unit)? = null

    fun onSubFinished(action: ((fileNode: SnapshotNode) -> Unit)?) {
        this.finishedAction = action
    }

    @Synchronized
    fun attachParent(parent: SnapshotNode?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nexNode = parentLastChild
        }
    }

    fun startWriteToObjects() {
        ThreadPoolManager.execute {
            if (file == null) {
                return@execute
            }

            if (file.isFile) {
                notifySubFinished()
                return@execute
            }

            val files = file.listFiles()
            childCount = files.size

            if (files.isEmpty()) {
                notifySubFinished()
            } else {
                files.forEach {
                    val node = SnapshotNode(it, it.name)
                    node.attachParent(this)
                    node.startWriteToObjects()
                }
            }
        }
    }

    private fun notifySubFinished() {
        if (finishedCount.addAndGet(1) >= childCount) {
            checkToWriteToObjects()
            finishedAction?.invoke(this)
            parent?.notifySubFinished()
        }
    }

    private fun checkToWriteToObjects() {
        if (file != null && file.isFile) {
            sha1 = writeToObjects(file.getSHA1())
            return
        }

        var content = ""

        var node = lastChild
        while (node != null) {
            val nodeFile = node.file ?: continue
            if (node.sha1.isNotEmpty()) {
                val type = if (nodeFile.isFile) SnapshotManager.NODE_BLOB else SnapshotManager.NODE_TREE
                content += "$type,${node.sha1},${node.name},${nodeFile.lastModified()}\n"
            }
            node = node.nexNode
        }

        if (content.isNotEmpty()) {
            sha1 = writeToObjects(content)
        }
    }

    fun writeToObjects(content: String): String {
        val sha1 = content.toByteArray().getSHA1()
        val leftFile = File(SnapshotManager.objectsFile, sha1.sha1ToObjectLeftPath())
        val file = File(leftFile, sha1.sha1ToObjectRightPath())
        if (!file.exists()) {
            if (!leftFile.exists()) {
                leftFile.mkdirs()
            }
            file.writeText(content)
        }
        Log.d(TAG, "writeToObjects(), SHA1 = $sha1, content = $content")
        return sha1
    }
}