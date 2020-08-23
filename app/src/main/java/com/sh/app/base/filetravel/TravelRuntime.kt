package com.sh.app.base.filetravel

import android.util.Log
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.formatFileSize
import java.io.File

class TravelRuntime(private vararg val paths: String) {

    companion object {
        private const val TAG = "SIMPLE_TEST"

        fun test() {
            Thread {
                TravelRuntime(File(SnapshotManager.sdcardFile, "Android").path)
                        .start()
            }.start()
        }
    }

    private var totalSize = 0L
    private val rootNode = FileNode()

    fun start() {
        totalSize = 0L
        rootNode.reset()
        val time = System.currentTimeMillis()
        for (path in paths) {
            travelFile(rootNode, File(path))
        }
        Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - time}ms, size = ${totalSize.formatFileSize()}")

        totalSize = 0L
        travelNode(rootNode)
        Log.d(TAG, "start(), size = ${totalSize.formatFileSize()}")
    }

    private fun travelFile(parent: FileNode?, file: File) {
        val fileNode = FileNode()
        fileNode.path = file.path
        fileNode.setParent(parent)

        if (file.isFile) {
            val inputStream = file.inputStream()
            val size = inputStream.available().toLong()
            inputStream.close()

            fileNode.size = size
            totalSize += size
            //Log.d(TAG, "travelFile(), path = ${file.path}, size = ${size.formatFileSize()}")
            return
        }

        val files = file.listFiles()
        files.sortWith(Comparator { o1, o2 ->
            if (o1 === o2) {
                0
            } else if (o1.isFile) {
                if (o2.isFile) {
                    o1.name.compareTo(o2.name, ignoreCase = true)
                } else {
                    1
                }
            } else {
                if (o2.isFile) {
                    -1
                } else {
                    o1.name.compareTo(o2.name, ignoreCase = true)
                }
            }
        })

        for (item in files) {
            travelFile(fileNode, item)
        }
    }

    private fun travelNode(node: FileNode?) {
        node ?: return
        if (node.lastChild == null) {
            totalSize += node.size
        }
        travelNode(node.lastChild)
        travelNode(node.nexNode)
    }
}