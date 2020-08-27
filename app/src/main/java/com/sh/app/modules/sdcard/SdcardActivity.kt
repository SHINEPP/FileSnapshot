package com.sh.app.modules.sdcard

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.modules.common.browsingText
import kotlinx.android.synthetic.main.activity_sdcard.*
import java.io.File

class SdcardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sdcard)

        browsingView.setBrowsingFile(BrowsingFile(Environment.getExternalStorageDirectory()))
        browsingView.setFileClickedAction {
            val file = File(it.getFilePath())
            if (file.exists()) {
                file.browsingText(this)
            }
        }
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
