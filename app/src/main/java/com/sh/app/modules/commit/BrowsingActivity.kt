package com.sh.app.modules.commit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.base.snapshot.ObjectFile
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.modules.common.browsingText
import kotlinx.android.synthetic.main.activity_browsing.*
import java.io.File

class BrowsingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_COMMIT = "EXTRA_KEY_COMMIT"

        var gObjectFile: ObjectFile? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browsing)

        val commitNode = intent.getStringExtra(EXTRA_KEY_COMMIT) ?: ""
        if (commitNode.isNotEmpty()) {
            val objectFile = SnapshotManager.createCommitNode(commitNode)?.getObjectFile()
            if (objectFile != null) {
                browsingView.setBrowsingFile(BrowsingFile(null, objectFile))
            }
        } else {
            val objectFile = gObjectFile
            if (objectFile != null) {
                browsingView.setBrowsingFile(BrowsingFile(null, objectFile))
            }
        }

        browsingView.setFileClickedAction {
            val file = File(it.getFilePath())
            if (file.exists()) {
                file.browsingText(this)
            }
        }

        gObjectFile = null
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
