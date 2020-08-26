package com.sh.app.base.snapshot

import android.os.Handler
import android.os.Looper
import android.util.Log

class SnapshotTask(private val headName: String, private vararg val paths: String) {

    companion object {
        private const val TAG = "FILE_SNAPSHOT"
    }

    fun start(complete: () -> Unit) {

        val startTime = System.currentTimeMillis()

        val headSha1 = SnapshotManager.getHeadSHA1(headName)
        val objectFile = SnapshotManager.createCommitNode(headSha1)?.getObjectFile()
        val rootNode = SnFastWriter(null, "", objectFile)

        rootNode.setLeaveAction { node ->
            if (node === rootNode) {
                val sha1 = node.sha1
                val duration = System.currentTimeMillis() - startTime
                Log.d(TAG, "leave root, sha1 = $sha1")
                if (sha1.isNotEmpty()) {
                    val content = "${SnapshotManager.NODE_TREE},$sha1,0\nparent,$headSha1\nduration,$duration"
                    SnapshotManager.setHeadSHA1(headName, node.writeToObjects(content))
                }

                Log.d(TAG, "leave root, duration = ${duration}ms")
                Handler(Looper.getMainLooper()).post { complete() }
            }
        }

        rootNode.start(*paths)
    }
}