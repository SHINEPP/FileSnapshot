package com.sh.app.modules.ossfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import kotlinx.android.synthetic.main.activity_sdcard.*

class OssBrowsingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_BUCKET_NAME = "EXTRA_KEY_BUCKET_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oss_browsing)

        val bucketName = intent.getStringExtra(EXTRA_KEY_BUCKET_NAME) ?: ""
        val browsingRoot = OssBrowsingRoot(null, bucketName, "")
        browsingRoot.refresh {
            browsingView.setBrowsingFile(it)
        }
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
