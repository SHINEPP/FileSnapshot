package com.sh.app.base.snapshot

import android.util.Log
import java.io.File

class CommitNode(val sha1: String) {

    companion object {
        private const val TAG = "COMMIT_NODE"
    }

    private var nodeType = ""
    private var objectSha1 = ""
    private var parentSha1 = ""

    private var objectFile: SnObjectFile? = null
    private var parent: CommitNode? = null

    private var lastModifyTime = 0L

    private var duration = 0L

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
            parserLine(line)
        }
    }

    private fun parserLine(line: String) {
        Log.d(TAG, "parserLine(), line = $line")

        val values = line.split(",")
        when (val type = values[0].trim()) {
            "blob", "tree" -> {
                val sha1 = values[1].trim()
                if (sha1.isValidSha1()) {
                    nodeType = type
                    objectSha1 = sha1
                }

            }
            "parent" -> {
                val sha1 = values[1].trim()
                if (sha1.isValidSha1()) {
                    parentSha1 = sha1
                }
            }
            "duration" -> {
                val dur = values[1].trim()
                try {
                    duration = dur.toLong()
                } catch (e: Throwable) {

                }
            }
        }
    }

    fun getLastModifyTime(): Long {
        return lastModifyTime
    }

    fun getDuration(): Long {
        return duration
    }

    fun getParent(): CommitNode? {
        if (parent == null && parentSha1.isValidSha1()) {
            parent = CommitNode(parentSha1)
        }
        return parent
    }

    fun getObjectFile(): SnObjectFile? {
        if (objectFile == null && nodeType.isNotEmpty()) {
            objectFile = SnObjectFile(nodeType == "blob", objectSha1, "", lastModifyTime)
        }
        return objectFile
    }
}