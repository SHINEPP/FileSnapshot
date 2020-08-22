package com.sh.app.modules.ossfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import kotlinx.android.synthetic.main.activity_sdcard.*

class OssBrowsingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oss_browsing)

        val browsingRoot = OssBrowsingRoot(null, "")
        browsingRoot.refresh()
        browsingView.setBrowsingFile(browsingRoot.refresh())
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
