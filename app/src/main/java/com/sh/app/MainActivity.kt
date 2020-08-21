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
import com.sh.app.demo.CommitActivity
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import com.sh.app.snapshot.FileSnapshot
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.snapshot.sha1ToSimple
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val handler = Handler()

    private var snapshotCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkToRequestPermission()

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        updateItems()
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

    private fun updateItems() {
        items.clear()

        items.add(CardViewItem("Wexin") {
            snapshotWexin()
        })

        items.add(CardViewItem("QQ") {
            snapshotQQ()
        })

        SnapshotManager.getHeadNames().forEach {
            val sha1 = SnapshotManager.getHeadSHA1(it)
            val cardItem = CardViewItem(it)
            items.add(cardItem)

            cardItem.add(KeyValueItem("sha1", sha1.sha1ToSimple()))
            cardItem.add(KeyValueItem("browsing", "VIEW") {
                val intent = Intent(this, CommitActivity::class.java)
                intent.putExtra(CommitActivity.EXTRA_KEY_COMMIT, sha1)
                startActivity(intent)
            })
        }

        adapter.updateDataSet(items)
    }

    private fun snapshotWexin() {
        progressBar.visibility = View.VISIBLE
        snapshotCount += 1
        Thread {
            FileSnapshot("Wexin",
                    File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mm").path,
                    File(SnapshotManager.sdcardFile, "tencent/MicroMsg").path,
                    File(SnapshotManager.sdcardFile, "Pictures/WeiXin").path
            ).start()
            handler.post {
                snapshotCount -= 1
                Toast.makeText(this, "Snapshot Wexin finished", Toast.LENGTH_LONG).show()
                if (snapshotCount == 0) {
                    progressBar.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun snapshotQQ() {
        progressBar.visibility = View.VISIBLE
        snapshotCount += 1
        Thread {
            FileSnapshot("QQ",
                    File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mobileqq").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQ_Favorite").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQ_Images").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQfile_recv").path,
                    File(SnapshotManager.sdcardFile, "tencent/MobileQQ").path
            ).start()
            handler.post {
                snapshotCount -= 1
                Toast.makeText(this, "Snapshot QQ finished", Toast.LENGTH_LONG).show()
                if (snapshotCount == 0) {
                    progressBar.visibility = View.GONE
                }
            }
        }.start()
    }
}
