package com.sh.app.snapshot

import android.text.TextUtils
import android.util.Log
import java.io.File

class ObjectFile(val isBlob: Boolean, val sha1: String, val name: String, val lastModifyTime: Long) {

    companion object {
        private const val TAG = "OBJECT_FILE"
    }

    private var path: String = ""
    private lateinit var lines: List<String>

    private var parent: ObjectFile? = null
    private val subFiles = ArrayList<ObjectFile>()

    init {
        parser()
    }

    private fun parser() {
        if (sha1.isNotValidSha1()) {
            return
        }

        val file = File(SnapshotManager.objectsFile, sha1.sha1ToObjectPath())
        if (!file.exists()) {
            Log.d(TAG, "parser(), no exists, path = ${file.path}")
            return
        }

        lines = file.readLines()
    }

    fun getPath(): String {
        if (path.isEmpty()) {
            val prePath = parent?.getPath()
            path = if (TextUtils.isEmpty(prePath)) name else "$prePath/$name"
        }

        return path
    }

    fun getSubCount(): Int {
        return lines.size
    }

    fun getObjectFiles(): List<ObjectFile> {
        if (!isBlob && subFiles.isEmpty()) {
            for (line in lines) {
                Log.d(TAG, "getDigestFiles(), line = $line")

                val values = line.split(",")
                val cType = values[0].trim()
                val cSha1 = values[1].trim()
                val cName = values[2].trim()
                val cTime = values[3].trim().toLong()

                if (cSha1.isNotValidSha1()) {
                    continue
                }

                if (cType == "tree") {
                    val node = ObjectFile(false, cSha1, cName, cTime)
                    node.parent = this
                    subFiles.add(node)
                } else if (cType == "blob") {
                    val node = ObjectFile(true, cSha1, cName, cTime)
                    node.parent = this
                    subFiles.add(node)
                }
            }
        }

        return subFiles
    }

    fun clear() {
        subFiles.clear()
    }
}