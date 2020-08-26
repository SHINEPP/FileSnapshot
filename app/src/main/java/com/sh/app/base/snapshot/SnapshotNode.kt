package com.sh.app.base.snapshot

import android.util.Log
import com.sh.app.utils.ThreadPoolManager
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


class SnapshotNode(private val file: File?, val name: String, private var objectFile: ObjectFile?) {

    companion object {
        private const val TAG = "SNAPSHOT_NODE"
    }

    var parent: SnapshotNode? = null
        private set
    var lastChild: SnapshotNode? = null
        private set
    var nextBrother: SnapshotNode? = null
        private set

    var sha1 = ""
        private set
    var isChanged = true
        private set

    var childCount = 0

    private val finishedCount = AtomicInteger(0)

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
            nextBrother = parentLastChild
        }
    }

    fun startWriteToObjects() {
        ThreadPoolManager.requestExecute {
            if (file == null) {
                return@requestExecute
            }

            if (file.isFile) {
                notifySubFinished()
                return@requestExecute
            }

            val files = file.listFiles()
            childCount = files?.size ?: 0
            if (files == null || files.isEmpty()) {
                notifySubFinished()
                return@requestExecute
            }

            val objectFiles = objectFile?.getObjectFiles()
            for (cFile in files) {
                var subObjFile: ObjectFile? = null
                if (objectFiles != null) {
                    for (objFile in objectFiles) {
                        if (cFile.path == objFile.getPath()) {
                            subObjFile = objFile
                            break
                        }
                    }
                }

                val node = SnapshotNode(cFile, cFile.name, subObjFile)
                node.attachParent(this)
                node.startWriteToObjects()
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
            sha1 = if (objectFile?.lastModifyTime == file.lastModified()) {
                isChanged = false
                objectFile?.sha1 ?: ""
            } else {
                isChanged = true
                writeToObjects(file.getSHA1())
            }
            return
        }

        var content = ""
        var node = lastChild
        var haveChanged = false
        while (node != null) {
            val nodeFile = node.file ?: continue
            if (node.sha1.isNotEmpty()) {
                if (node.isChanged) {
                    haveChanged = true
                }
                val type = if (nodeFile.isFile) SnapshotManager.NODE_BLOB else SnapshotManager.NODE_TREE
                content += "$type,${node.sha1},${node.name},${nodeFile.lastModified()}\n"
            }
            node = node.nextBrother
        }

        if (content.isNotEmpty()) {
            val cObjFile = objectFile
            sha1 = if (haveChanged || cObjFile == null || cObjFile.lastModifyTime != file?.lastModified()) {
                isChanged = true
                writeToObjects(content)
            } else {
                isChanged = false
                cObjFile.sha1
            }
        }
    }

    fun writeToObjects(content: String): String {
        val cSha1 = content.toByteArray().getSHA1()
        val leftFile = File(SnapshotManager.objectsFile, cSha1.sha1ToObjectLeftPath())
        val file = File(leftFile, cSha1.sha1ToObjectRightPath())
        if (!file.exists()) {
            if (!leftFile.exists()) {
                leftFile.mkdirs()
            }
            file.writeText(content)
        }
        Log.d(TAG, "writeToObjects(), SHA1 = $cSha1, content = $content")
        return cSha1
    }
}