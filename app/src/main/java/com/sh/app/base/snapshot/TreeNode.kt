package com.sh.app.base.snapshot

import android.util.Log
import java.io.File


class TreeNode(private val file: File?, val name: String) {

    companion object {
        private const val TAG = "TREE_NODE"
    }

    var parent: TreeNode? = null
        private set
    var lastChild: TreeNode? = null
        private set
    var nexNode: TreeNode? = null
        private set

    var sha1 = ""
        private set

    var childCount = 0
    private var finishedCount = 0

    private var finishedAction: ((fileNode: TreeNode) -> Unit)? = null

    fun onFinished(action: ((fileNode: TreeNode) -> Unit)?) {
        this.finishedAction = action
    }

    fun writeToObjects() {
        if (file == null) {
            return
        }

        if (file.isFile) {
            notifyFinished()
            return
        }

        val files = file.listFiles()
        childCount = files.size

        if (files.isEmpty()) {
            notifyFinished()
        } else {
            files.forEach {
                val node = TreeNode(it, it.name)
                node.attachParent(this)
                node.writeToObjects()
            }
        }
    }

    @Synchronized
    fun attachParent(parent: TreeNode?) {
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
        if (finishedCount >= childCount) {
            checkToWriteToObjects()
            finishedAction?.invoke(this)
            parent?.notifyFinished()
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