package com.sh.app.modules.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import kotlinx.android.synthetic.main.activity_browsing_text.*
import java.io.File

class BrowsingTextActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_FILE_PATH = "EXTRA_KEY_FILE_PATH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browsing_text)

        val path = intent.getStringExtra(EXTRA_KEY_FILE_PATH) ?: ""
        val file = File(path)
        textBrowsingView.setFile(file)
    }
}