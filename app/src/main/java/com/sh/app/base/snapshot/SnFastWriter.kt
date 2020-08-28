package com.sh.app.base.snapshot

import android.util.Log
import com.sh.app.base.filewalk.WalkPool
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class SnFastWriter(val file: File?, val name: String, private var objectFile: SnObjectFile?, private val deep: Int = -1) {

    companion object {
        private const val TAG = "SNAPSHOT_NODE"
    }

    var parent: SnFastWriter? = null
        private set
    var lastChild: SnFastWriter? = null
        private set
    var nextBrother: SnFastWriter? = null
        private set

    var sha1 = ""
        private set
    private var isChanged = true

    private var needCount = 1
    private val finishedCount = AtomicInteger(0)
    private val firstVisit = AtomicBoolean(true)

    private var leaveAction: ((node: SnFastWriter) -> Unit)? = null

    fun setLeaveAction(action: ((node: SnFastWriter) -> Unit)?) {
        this.leaveAction = action
    }

    fun start(vararg paths: String) {
        val files = ArrayList<File>()
        for (path in paths) {
            files.add(File(path))
        }

        startWriteToObjects(files.toTypedArray(), true, deep)
    }

    private fun startWriteToObjects() {
        if (file != null && firstVisit.compareAndSet(true, false)) {
            if (deep == 0 || file.isFile) {
                notifySubFinished()
                return
            }

            val files = file.listFiles()
            if (files == null || files.isEmpty()) {
                notifySubFinished()
                return
            }

            startWriteToObjects(files, false, deep - 1)
        }

        // 遍历子文件
        var subNode: SnFastWriter? = lastChild
        while (subNode != null) {
            subNode.startWriteToObjects()
            subNode = subNode.nextBrother
        }
    }

    private fun startWriteToObjects(files: Array<File>, usePath: Boolean, deep: Int) {
        needCount = files.size
        val objectFiles = objectFile?.getObjectFiles()
        for (cFile in files) {
            var subObjFile: SnObjectFile? = null
            if (objectFiles != null) {
                for (objFile in objectFiles) {
                    if (cFile.path == objFile.getPath()) {
                        subObjFile = objFile
                        break
                    }
                }
            }

            val node = SnFastWriter(cFile, if (usePath) cFile.path else cFile.name, subObjFile, deep)
            node.attachParent(this)
            WalkPool.execute { node.startWriteToObjects() }
        }
    }

    private fun attachParent(parent: SnFastWriter?) {
        this.parent = parent
        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            nextBrother = parentLastChild
        }
    }

    private fun notifySubFinished() {
        if (finishedCount.addAndGet(1) == needCount) {
            checkToWriteToObjects()
            performLeave(this)
            parent?.notifySubFinished()
        }
    }

    private fun performLeave(node: SnFastWriter) {
        leaveAction?.invoke(node)
        parent?.performLeave(node)
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