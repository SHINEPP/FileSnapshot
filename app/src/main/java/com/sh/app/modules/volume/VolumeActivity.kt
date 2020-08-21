package com.sh.app.modules.volume

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import kotlinx.android.synthetic.main.activity_sdcard.*

class VolumeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdcard)

        browsingView.setBrowsingFile(
            BrowsingFile(
                Environment.getExternalStorageDirectory()
            )
        )
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
