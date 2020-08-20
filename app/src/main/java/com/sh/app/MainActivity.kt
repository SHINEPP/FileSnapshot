package com.sh.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sh.app.snapshot.FileSnapshot
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.demo.CommitActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkToRequestPermission()

        progressBar.visibility = View.GONE
        startBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            Thread {
                FileSnapshot(
                        File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mm").path,
                        File(SnapshotManager.sdcardFile, "tencent/MicroMsg").path,
                        File(SnapshotManager.sdcardFile, "Pictures/WeiXin").path,
                        File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mobileqq").path,
                        File(SnapshotManager.sdcardFile, "tencent/QQ_Favorite").path,
                        File(SnapshotManager.sdcardFile, "tencent/QQ_Images").path,
                        File(SnapshotManager.sdcardFile, "tencent/QQfile_recv").path,
                        File(SnapshotManager.sdcardFile, "tencent/MobileQQ").path)
                        .start()
                handler.post {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Snapshot finished", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        commitBtn.setOnClickListener { startActivity(Intent(this, CommitActivity::class.java)) }
    }

    private fun checkToRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requestPermissions = ArrayList<String>()
            val allNeedPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            for (permission in allNeedPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions.add(permission)
                }
            }

            if (requestPermissions.isNotEmpty()) {
                requestPermissions(requestPermissions.toTypedArray(), 0)
            }
        }
    }
}
