package com.sh.app.modules.ossfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import kotlinx.android.synthetic.main.activity_sdcard.*

class OssFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oss_file)

        browsingView.setBrowsingFile(ShOssFile.createRoot())
    }

    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}
