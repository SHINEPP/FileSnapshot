package com.sh.app.browsing

interface IBrowsingFile {

    fun isFile(): Boolean

    fun getFileName(): String

    fun getFilePath(): String

    fun getParent(): IBrowsingFile?

    fun getLastModifyTime(): Long

    fun getSubCount(): Int

    fun getBrowsingFiles(): List<IBrowsingFile>

    fun clear()
}