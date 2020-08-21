package com.sh.app.main

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.browsing.BrowsingPath
import kotlinx.android.synthetic.main.activity_sdcard.*

class SdcardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdcard)

        browsingView.setBrowsingFile(BrowsingPath(Environment.getExternalStorageDirectory()))
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
