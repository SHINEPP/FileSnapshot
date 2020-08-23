package com.sh.app.base.filetravel

import android.util.Log

class FileNode {
    var parent: FileNode? = null
        private set
    var lastChild: FileNode? = null
        private set
    var preNode: FileNode? = null
        private set
    var nexNode: FileNode? = null
        private set

    var path = ""
    var size = 0L

    fun setParent(parent: FileNode?) {
        Log.d("ZZL_F", "setParent(), cur_$this, parent_$parent")

        this.parent = parent
        preNode = parent

        if (parent != null) {
            val parentLastChild = parent.lastChild
            parent.lastChild = this
            if (parentLastChild != null) {
                parentLastChild.preNode = this
            }
            nexNode = parentLastChild
        }
    }

    fun reset() {
        parent = null
        lastChild = null
        preNode = null
        nexNode = null
    }

    override fun toString(): String {
        return "path = $path"
    }
}