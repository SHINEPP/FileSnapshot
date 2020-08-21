package com.sh.app.snapshot

import android.util.Log
import java.io.File

class CommitNode(val sha1: String) {

    companion object {
        private const val TAG = "COMMIT_NODE"
    }

    private var nodeType = ""
    private var objectSha1 = ""
    private var parentSha1 = ""

    private var objectFile: ObjectFile? = null
    private var parent: CommitNode? = null

    private var lastModifyTime = 0L

    init {
        parser()
    }

    private fun parser() {
        if (sha1.isNotValidSha1()) {
            return
        }

        val file = File(SnapshotManager.objectsFile, sha1.sha1ToObjectPath())
        if (!file.exists()) {
            Log.d(TAG, "parser(), not exists, path = ${file.path}")
            return
        }

        lastModifyTime = file.lastModified()
        val lines = file.readLines()
        for (line in lines) {
            Log.d(TAG, "parser(), line = $line")
            val values = line.split(",")
            val type = values[0].trim()
            val sha1 = values[1].trim()

            if (sha1.isNotValidSha1()) {
                continue
            }

            when (type) {
                "blob", "tree" -> {
                    nodeType = type
                    objectSha1 = sha1
                }
                "parent" -> {
                    parentSha1 = sha1
                }
            }
        }
    }

    fun getLastModifyTime(): Long {
        return lastModifyTime
    }

    fun getParent(): CommitNode? {
        if (parent == null && parentSha1.isValidSha1()) {
            parent = CommitNode(parentSha1)
        }
        return parent
    }

    fun getObjectFile(): ObjectFile? {
        if (objectFile == null && nodeType.isNotEmpty()) {
            objectFile = ObjectFile(nodeType == "blob", objectSha1, "Root", lastModifyTime)
        }
        return objectFile
    }
}