package com.sh.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import com.sh.app.snapshot.CommitNode
import com.sh.app.snapshot.sha1ToSimple
import com.sh.app.utils.toDatetimeString
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_commit.*
import kotlin.collections.ArrayList

class CommitActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY_COMMIT = "EXTRA_KEY_COMMIT"
    }

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private var commitNode: CommitNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit)

        val sha1 = intent.getStringExtra(EXTRA_KEY_COMMIT) ?: ""
        commitNode = SnapshotManager.createCommitNode(sha1)

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        updateItems()
    }

    private fun updateItems() {
        items.clear()

        var index = 0

        var curNode = commitNode
        while (curNode != null) {
            index++
            val commitItem = CardViewItem("$index. " + curNode.sha1.sha1ToSimple())
            items.add(commitItem)

            commitItem.add(KeyValueItem("datetime", curNode.getLastModifyTime().toDatetimeString()))

            val node1 = curNode.sha1
            commitItem.add(KeyValueItem("files", "VIEW") {
                val intent = Intent(this, BrowsingActivity::class.java)
                intent.putExtra(BrowsingActivity.EXTRA_KEY_COMMIT, node1)
                startActivity(intent)
            })

            val node2 = curNode.getParent()?.sha1
            commitItem.add(KeyValueItem("diff", "VS. ${index + 1}") {
                val intent = Intent(this, DifferenceActivity::class.java)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_1, node1)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_2, node2)
                startActivity(intent)
            })

            curNode = curNode.getParent()
        }

        adapter.updateDataSet(items)
    }
}
