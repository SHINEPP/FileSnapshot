package com.sh.app.base.snapshot

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.sh.app.utils.ThreadPoolManager
import java.io.File

class FileSnapshot(private val headName: String, private vararg val paths: String) {

    companion object {
        private const val TAG = "FILE_SNAPSHOT"
    }

    fun start(complete: () -> Unit) {
        ThreadPoolManager.execute {
            val startTime = System.currentTimeMillis()

            val rootNode = SnapshotNode(null, "")
            rootNode.childCount = paths.size
            rootNode.onSubFinished { root ->
                val sha1 = root.sha1
                Log.d(TAG, "start(), sha1 = $sha1")
                if (sha1.isNotEmpty()) {
                    val head = SnapshotManager.getHeadSHA1(headName)
                    val content = "${SnapshotManager.NODE_TREE},$sha1,0\nparent,$head"
                    SnapshotManager.setHeadSHA1(headName, root.writeToObjects(content))
                }

                Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms")
                Handler(Looper.getMainLooper()).post { complete() }
            }

            for (path in paths) {
                val file = File(path)
                val node = SnapshotNode(file, file.path)
                node.attachParent(rootNode)
                node.startWriteToObjects()
            }
        }
    }
}