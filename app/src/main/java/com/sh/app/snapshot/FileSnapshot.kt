package com.sh.app.snapshot

import android.util.Log
import com.sh.app.utils.getSHA1
import java.io.File

class FileSnapshot(private vararg val paths: String) {

    companion object {
        private const val TAG = "FILE_SNAPSHOT"
    }

    fun start() {
        val startTime = System.currentTimeMillis()

        var content = ""
        for (path in paths) {
            val file = File(path)
            val sha1 = writeToObjects(file)
            if (sha1.isNotEmpty()) {
                val node = if (file.isFile) SnapshotManager.NODE_BLOB else SnapshotManager.NODE_TREE
                content += "$node,$sha1,${file.path},${file.lastModified()}\n"
            }
        }

        if (content.isNotEmpty()) {
            val sha1 = writeToObjects(content)
            if (sha1.isNotEmpty()) {
                val node = "${SnapshotManager.NODE_TREE},$sha1,0\nparent,${SnapshotManager.getHeadSHA1()}"
                val commitSha1 = writeToObjects(node)
                SnapshotManager.setHeadSHA1(commitSha1)
            }
        }

        Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms")
    }

    // 写出存储，返回 SHA1
    private fun writeToObjects(file: File): String {
        if (file.isFile) {
            return writeToObjects(file.getSHA1())
        }

        val files = file.listFiles() ?: emptyArray()
        var content = ""
        files.forEach {
            val sha1 = writeToObjects(it)
            if (sha1.isNotEmpty()) {
                val node = if (it.isFile) SnapshotManager.NODE_BLOB else SnapshotManager.NODE_TREE
                content += "$node,$sha1,${it.name},${it.lastModified()}\n"
            }
        }

        if (content.isNotEmpty()) {
            return writeToObjects(content)
        }

        return ""
    }

    private fun writeToObjects(content: String): String {
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