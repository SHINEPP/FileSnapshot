package com.sh.app.base.snapshot

import android.os.Environment
import java.io.File

object SnapshotManager {

    private const val STORE_ROOT = "zzl_git/wx_qq_1"
    private const val STORE_OBJECTS = "objects"
    private const val STORE_REFS_HEADS = "refs/heads"

    const val NODE_TREE = "tree"
    const val NODE_BLOB = "blob"

    val sdcardFile: File = Environment.getExternalStorageDirectory()

    private val rootFile = File(sdcardFile, STORE_ROOT)

    val objectsFile = File(rootFile, STORE_OBJECTS)

    fun getHeadSHA1(name: String): String {
        var sha1 = ""
        val file = File(rootFile, "$STORE_REFS_HEADS/$name")
        if (file.exists() && file.isFile) {
            sha1 = file.readText()
        }
        return if (sha1.isValidSha1()) sha1 else ""
    }

    fun setHeadSHA1(name: String, sha1: String) {
        if (name.isEmpty() && sha1.isNotValidSha1()) {
            return
        }

        val headsFile = File(rootFile, STORE_REFS_HEADS)
        if (!headsFile.exists()) {
            headsFile.mkdirs()
        }

        File(headsFile, name).writeText(sha1)
    }

    fun getHeadNames(): List<String> {
        val headsFile = File(rootFile, STORE_REFS_HEADS)
        if (!headsFile.exists()) {
            return emptyList()
        }

        val nameList = ArrayList<String>()
        val listFiles = headsFile.listFiles() ?: emptyArray()
        for (file in listFiles) {
            if (file.isFile && !file.isHidden && file.name.isNotEmpty()) {
                nameList.add(file.name)
            }
        }
        return nameList
    }

    fun createCommitNode(sha1: String): CommitNode? {
        if (sha1.isNotValidSha1()) {
            return null
        }
        return CommitNode(sha1)
    }
}