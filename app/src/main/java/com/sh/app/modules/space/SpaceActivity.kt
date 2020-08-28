package com.sh.app.modules.space

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import com.sh.app.utils.formatFileSize
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_space.*
import java.util.*
import kotlin.collections.ArrayList

class SpaceActivity : AppCompatActivity() {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val spaceScanner = SpaceScanTask()

    private lateinit var spaceCardItem: CardViewItem
    private lateinit var videoItem: KeyValueItem
    private lateinit var audioItem: KeyValueItem
    private lateinit var imageItem: KeyValueItem
    private lateinit var documentItem: KeyValueItem
    private lateinit var apkItem: KeyValueItem
    private lateinit var totalItem: KeyValueItem

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space)

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        spaceCardItem = CardViewItem("Space") {
            scan()
        }
        items.add(spaceCardItem)

        videoItem = KeyValueItem("Video", "0 B") {
            DetailActivity.gRoot = spaceScanner.videoRoot
            startActivity(Intent(this, DetailActivity::class.java))
        }
        spaceCardItem.add(videoItem)

        audioItem = KeyValueItem("Audio", "0 B") {
            DetailActivity.gRoot = spaceScanner.audioRoot
            startActivity(Intent(this, DetailActivity::class.java))
        }
        spaceCardItem.add(audioItem)

        imageItem = KeyValueItem("Image", "0 B") {
            DetailActivity.gRoot = spaceScanner.imageRoot
            startActivity(Intent(this, DetailActivity::class.java))
        }
        spaceCardItem.add(imageItem)

        documentItem = KeyValueItem("Document", "0 B") {
            DetailActivity.gRoot = spaceScanner.documentRoot
            startActivity(Intent(this, DetailActivity::class.java))
        }
        spaceCardItem.add(documentItem)

        apkItem = KeyValueItem("Apk", "0 B") {
            DetailActivity.gRoot = spaceScanner.apkRoot
            startActivity(Intent(this, DetailActivity::class.java))
        }
        spaceCardItem.add(apkItem)

        totalItem = KeyValueItem("Total", "0 ms")
        spaceCardItem.add(totalItem)

        adapter.updateDataSet(items)

        scan()
    }

    private fun scan() {
        if (progressView.visibility == View.VISIBLE) {
            return
        }

        progressView.visibility = View.VISIBLE
        spaceScanner.start()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    if (!spaceScanner.isScanning) {
                        progressView.visibility = View.GONE
                        timer.cancel()
                    } else {
                        videoItem.setValue(spaceScanner.videoSize.get().formatFileSize())
                        audioItem.setValue(spaceScanner.audioSize.get().formatFileSize())
                        imageItem.setValue(spaceScanner.imageSize.get().formatFileSize())
                        documentItem.setValue(spaceScanner.documentSize.get().formatFileSize())
                        apkItem.setValue(spaceScanner.apkSize.get().formatFileSize())
                        totalItem.setValue("${System.currentTimeMillis() - spaceScanner.startTime} ms")
                    }
                    adapter.updateDataSet(items)
                }
            }
        }, 0, 100L)
    }
}