package com.sh.app.modules.space

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.base.filetravel.FileNode
import kotlinx.android.synthetic.main.activity_space_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        var gRoot: FileNode? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_detail)

        val node = gRoot
        if (node != null) {
            browsingView.setBrowsingFile(SpaceFile(null, node))
        }
        gRoot = null
    }


    override fun onBackPressed() {
        if (!browsingView.back()) {
            super.onBackPressed()
        }
    }
}