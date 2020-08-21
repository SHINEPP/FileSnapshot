package com.sh.app.base.browsing

interface IBrowsingFile {

    fun isFile(): Boolean

    fun getFileName(): String

    fun getFilePath(): String

    fun getParent(): IBrowsingFile?

    fun getLastModifyTime(): Long

    fun getSubCount(): Int

    fun getBrowsingFiles(): List<IBrowsingFile>

    fun getActivePosition(): Int

    fun setActivePosition(position: Int)

    fun getActiveOffsetDy(): Int

    fun setActiveOffsetDy(offsetDy: Int)

    fun clear()
}