package com.sh.app.base.filetravel

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.sh.app.OptApplication
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.utils.ThreadPoolManager
import com.sh.app.utils.formatFileSize
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class TravelFileTest(private vararg val paths: String) {

    companion object {
        private const val TAG = "SIMPLE_TEST"

        fun test() {
            Thread { TravelFileTest(SnapshotManager.sdcardFile.path).start() }.start()
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
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(OptApplication.context,
                        "duration = ${System.currentTimeMillis() - startTime}ms, size = ${totalSize.get().formatFileSize()}", Toast.LENGTH_LONG).show()
            }

            totalSize.set(0L)
            travelNode(rootNode)
            Log.d(TAG, "start(), size = ${totalSize.get().formatFileSize()}")
        }

        if (paths.isEmpty()) {
            rootNode.childCount = 1
            rootNode.notifyFinished()
        } else {
            rootNode.childCount = paths.size
            for (path in paths) {
                travelFile(rootNode, File(path))
            }
        }
    }

    private fun travelFile(parent: FileNode?, file: File) {
        ThreadPoolManager.execute {
            val fileNode = FileNode()
            fileNode.path = file.path
            fileNode.attachParent(parent)

            if (file.isFile) {
                fileNode.childCount = 1
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
                    fileNode.childCount = 1
                    fileNode.notifyFinished()
                } else {
                    fileNode.childCount = files.size
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