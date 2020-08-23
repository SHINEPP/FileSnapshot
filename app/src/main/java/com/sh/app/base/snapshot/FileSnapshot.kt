package com.sh.app.base.snapshot

import android.util.Log
import java.io.File

class FileSnapshot(private val headName: String, private vararg val paths: String) {

    companion object {
        private const val TAG = "FILE_SNAPSHOT"
    }

    fun start() {
        val startTime = System.currentTimeMillis()

        val rootNode = TreeNode(null, "")
        rootNode.childCount = paths.size
        rootNode.onFinished { root ->
            val sha1 = root.sha1
            Log.d(TAG, "start(), sha1 = $sha1")
            if (sha1.isNotEmpty()) {
                val content = "${SnapshotManager.NODE_TREE},$sha1,0\nparent,${SnapshotManager.getHeadSHA1(headName)}"
                SnapshotManager.setHeadSHA1(headName, root.writeToObjects(content))
            }
            Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms")
        }

        for (path in paths) {
            val file = File(path)
            val node = TreeNode(file, file.path)
            node.attachParent(rootNode)
            node.writeToObjects()
        }
    }
}