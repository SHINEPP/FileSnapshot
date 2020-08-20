package com.sh.app.snapshot

import android.os.Environment
import java.io.File

object SnapshotManager {

    private const val STORE_ROOT = "zzl_git/wx_qq"
    private const val STORE_HEAD = "HEAD"
    private const val STORE_OBJECTS = "objects"

    const val NODE_TREE = "tree"
    const val NODE_BLOB = "blob"

    val sdcardFile: File = Environment.getExternalStorageDirectory()

    private val rootFile = File(sdcardFile, STORE_ROOT)
    private val headFile = File(rootFile, STORE_HEAD)

    val objectsFile = File(rootFile, STORE_OBJECTS)

    fun getHeadSHA1(): String {
        if (!headFile.exists()) {
            return ""
        }

        return headFile.readText()
    }

    fun setHeadSHA1(sha1: String) {
        if (!headFile.exists()) {
            headFile.createNewFile()
        }
        headFile.writeText(sha1)
    }

    fun createHeadCommitNode(): CommitNode? {
        val sha1 = getHeadSHA1()
        if (sha1.isNotValidSha1()) {
            return null
        }
        return CommitNode(sha1)
    }

    fun createCommitNode(sha1: String): CommitNode? {
        if (sha1.isNotValidSha1()) {
            return null
        }
        return CommitNode(sha1)
    }
}