package com.sh.app.base.filetravel

import android.util.Log
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.formatFileSize
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class TravelRuntime(private vararg val paths: String) {

    companion object {
        private const val TAG = "SIMPLE_TEST"

        fun test() {
            Thread { TravelRuntime(SnapshotManager.sdcardFile.path).start() }.start()
        }
    }

    private var startTime = 0L
    private var totalSize = AtomicLong(0L)
    private val rootNode = FileNode()

    fun start() {
        startTime = System.currentTimeMillis()
        rootNode.reset()

        totalSize.set(0L)
        rootNode.onFinished {
            Log.d(TAG, "start(), duration = ${System.currentTimeMillis() - startTime}ms, size = ${totalSize.get().formatFileSize()}")

            totalSize.set(0L)
            travelNode(rootNode)
            Log.d(TAG, "start(), size = ${totalSize.get().formatFileSize()}")
        }

        if (paths.isEmpty()) {
            rootNode.totalCount = 1
            rootNode.notifyFinished()
        } else {
            rootNode.totalCount = paths.size
            for (path in paths) {
                travelFile(rootNode, File(path))
            }
        }
    }

    private fun travelFile(parent: FileNode?, file: File) {
        TravelThreadPool.execute {
            val fileNode = FileNode()
            fileNode.path = file.path
            fileNode.attachParent(parent)

            if (file.isFile) {
                fileNode.totalCount = 1
                val inputStream = file.inputStream()
                val size = inputStream.available().toLong()
                inputStream.close()

                fileNode.size = size
                val cSize = totalSize.addAndGet(size)
                Log.d(TAG, "travelFile(), path = ${file.path}, size = ${cSize.formatFileSize()}")
                fileNode.notifyFinished()

            } else {
                val files = file.listFiles()
                if (files.isEmpty()) {
                    fileNode.totalCount = 1
                    fileNode.notifyFinished()
                } else {
                    fileNode.totalCount = files.size
                    for (item in files) {
                        travelFile(fileNode, item)
                    }
                }
            }
        }
    }

    private fun travelNode(node: FileNode?) {
        node ?: return
        if (node.lastChild == null) {
            totalSize.addAndGet(node.size)
        }
        travelNode(node.lastChild)
        travelNode(node.nexNode)
    }
}