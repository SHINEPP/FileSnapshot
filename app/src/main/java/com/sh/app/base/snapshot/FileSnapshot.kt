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
        ThreadPoolManager.requestExecute {
            val startTime = System.currentTimeMillis()

            val headSha1 = SnapshotManager.getHeadSHA1(headName)
            val objectFile = SnapshotManager.createCommitNode(headSha1)?.getObjectFile()

            val rootNode = SnapshotNode(null, "", null)
            rootNode.childCount = paths.size
            rootNode.onSubFinished { root ->
                val sha1 = root.sha1
                val duration = System.currentTimeMillis() - startTime
                Log.d(TAG, "start(), sha1 = $sha1")
                if (sha1.isNotEmpty()) {
                    val content = "${SnapshotManager.NODE_TREE},$sha1,0\nparent,$headSha1\nduration,$duration"
                    SnapshotManager.setHeadSHA1(headName, root.writeToObjects(content))
                }

                Log.d(TAG, "start(), duration = ${duration}ms")
                Handler(Looper.getMainLooper()).post { complete() }
            }

            val objectFiles = objectFile?.getObjectFiles()
            for (path in paths) {
                val file = File(path)
                var subObjFile: ObjectFile? = null
                if (objectFiles != null) {
                    for (objFile in objectFiles) {
                        if (file.path == objFile.getPath()) {
                            subObjFile = objFile
                            break
                        }
                    }
                }

                val node = SnapshotNode(file, file.path, subObjFile)
                node.attachParent(rootNode)
                node.startWriteToObjects()
            }
        }
    }
}