package com.sh.app.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.browsing.BrowsingFile
import com.sh.app.snapshot.SnapshotManager
import kotlinx.android.synthetic.main.activity_browsing.*

class BrowsingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_COMMIT = "EXTRA_KEY_COMMIT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browsing)

        val commitNode = intent.getStringExtra(EXTRA_KEY_COMMIT)
        val objectFile = SnapshotManager.createCommitNode(commitNode)?.getObjectFile()
        if (objectFile != null) {
            browsingView.setBrowsingFile(BrowsingFile(null, objectFile))
        }
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
