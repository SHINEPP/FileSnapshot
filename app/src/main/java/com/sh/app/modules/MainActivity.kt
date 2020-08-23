package com.sh.app.modules

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sh.app.R
import com.sh.app.base.filetravel.TravelFileTest
import com.sh.app.base.osscenter.OssCenter
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import com.sh.app.modules.commit.CommitActivity
import com.sh.app.modules.volume.VolumeActivity
import com.sh.app.base.snapshot.FileSnapshot
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.base.snapshot.sha1ToSimple
import com.sh.app.modules.ossfile.OssBrowsingActivity
import com.sh.app.utils.toDatetimeString
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val handler = Handler()

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
            val allNeedPermissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            for (permission in allNeedPermissions) {
                if (ContextCompat.checkSelfPermission(
                                this,
                                permission
                        ) != PackageManager.PERMISSION_GRANTED
                ) {
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

        items.add(CardViewItem("Snapshot Wexin") {
            snapshotWexin(it)
        })

        items.add(CardViewItem("Snapshot QQ") {
            snapshotQQ(it)
        })

        items.add(CardViewItem("Browsing SDCard") {
            startActivity(Intent(this, VolumeActivity::class.java))
        })

        // OSS
        val ossBrowsingCard = CardViewItem("Browsing Remote")
        items.add(ossBrowsingCard)
        val bucketNames = arrayListOf(OssCenter.bucketName1, OssCenter.bucketName2,
                OssCenter.bucketName3)
        for (name in bucketNames) {
            ossBrowsingCard.add(KeyValueItem(name, "VIEW") {
                val intent = Intent(this, OssBrowsingActivity::class.java)
                intent.putExtra(OssBrowsingActivity.EXTRA_KEY_BUCKET_NAME, name)
                startActivity(intent)
            })
        }

        SnapshotManager.getHeadNames().forEach { headName ->
            val sha1 = SnapshotManager.getHeadSHA1(headName)
            val cardItem = CardViewItem(headName)
            items.add(cardItem)

            val datetime = SnapshotManager.createCommitNode(sha1)?.getLastModifyTime() ?: 0L
            cardItem.add(KeyValueItem("SHA-1", sha1.sha1ToSimple()))
            cardItem.add(KeyValueItem("Datetime", datetime.toDatetimeString()))
            cardItem.add(KeyValueItem("Browsing", "VIEW") {
                val intent = Intent(this, CommitActivity::class.java)
                intent.putExtra(CommitActivity.EXTRA_KEY_COMMIT, sha1)
                startActivity(intent)
            })
        }

        items.add(CardViewItem("Simple Travel") {
            TravelFileTest.test()
        })

        adapter.updateDataSet(items)
    }

    private fun snapshotWexin(cardViewItem: CardViewItem) {
        if (cardViewItem.showProgress) {
            return
        }
        cardViewItem.showProgress = true
        adapter.updateDataSet(items)
        Thread {
            FileSnapshot(
                    "Wexin",
                    File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mm").path,
                    File(SnapshotManager.sdcardFile, "tencent/MicroMsg").path,
                    File(SnapshotManager.sdcardFile, "Pictures/WeiXin").path
            ).start()
            handler.post {
                Toast.makeText(this, "Snapshot Wexin finished", Toast.LENGTH_SHORT).show()
                cardViewItem.showProgress = true
                updateItems()
            }
        }.start()
    }

    private fun snapshotQQ(cardViewItem: CardViewItem) {
        if (cardViewItem.showProgress) {
            return
        }
        cardViewItem.showProgress = true
        adapter.updateDataSet(items)
        Thread {
            FileSnapshot(
                    "QQ",
                    File(SnapshotManager.sdcardFile, "Android/data/com.tencent.mobileqq").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQ_Favorite").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQ_Images").path,
                    File(SnapshotManager.sdcardFile, "tencent/QQfile_recv").path,
                    File(SnapshotManager.sdcardFile, "tencent/MobileQQ").path
            ).start()
            handler.post {
                Toast.makeText(this, "Snapshot QQ finished", Toast.LENGTH_SHORT).show()
                cardViewItem.showProgress = true
                updateItems()
            }
        }.start()
    }
}
