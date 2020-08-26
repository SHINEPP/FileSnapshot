package com.sh.app.modules.space

import android.animation.ValueAnimator
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

class SpaceActivity : AppCompatActivity() {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val spaceScanner = SpaceScanner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space)

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        updateItems()
    }

    private fun updateItems() {
        items.clear()

        val card = CardViewItem("Space")
        items.add(card)

        val videoItem = KeyValueItem("Video", "0 B")
        card.add(videoItem)

        val audioItem = KeyValueItem("Audio", "0 B")
        card.add(audioItem)

        val imageItem = KeyValueItem("Image", "0 B")
        card.add(imageItem)

        val documentItem = KeyValueItem("Document", "0 B")
        card.add(documentItem)

        val apkItem = KeyValueItem("Apk", "0 B")
        card.add(apkItem)

        val totalItem = KeyValueItem("Total", "0 ms")
        card.add(totalItem)

        card.setClickedAction {
            if (progressBar.visibility == View.VISIBLE) {
                return@setClickedAction
            }

            spaceScanner.start()
            progressBar.visibility = View.VISIBLE
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener {
                if (!spaceScanner.isScanning) {
                    progressBar.visibility = View.GONE
                    animator.cancel()
                    return@addUpdateListener
                }

                videoItem.setValue(spaceScanner.videoSize.get().formatFileSize())
                audioItem.setValue(spaceScanner.audioSize.get().formatFileSize())
                imageItem.setValue(spaceScanner.imageSize.get().formatFileSize())
                documentItem.setValue(spaceScanner.documentSize.get().formatFileSize())
                apkItem.setValue(spaceScanner.apkSize.get().formatFileSize())
                totalItem.setValue("${System.currentTimeMillis() - spaceScanner.startTime} ms")

                adapter.updateDataSet(items)

            }
            animator.repeatCount = ValueAnimator.INFINITE
            animator.start()
        }

        adapter.updateDataSet(items)
    }
}