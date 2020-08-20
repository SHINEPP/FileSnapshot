package com.sh.app.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import com.sh.app.snapshot.sha1ToSimple
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_commit.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommitActivity : AppCompatActivity() {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val snapshotNode = SnapshotManager.createHeadCommitNode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit)

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        updateItems()
    }

    private fun updateItems() {
        items.clear()

        var index = 0

        var curSnapshotNode = snapshotNode
        while (curSnapshotNode != null) {
            index++
            val commitItem = CardViewItem("$index. " + curSnapshotNode.sha1.sha1ToSimple())
            items.add(commitItem)

            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA)
            val datetime = sdf.format(Date(curSnapshotNode.getLastModifyTime()))
            commitItem.add(KeyValueItem("datetime", datetime))

            val node1 = curSnapshotNode.sha1
            commitItem.add(KeyValueItem("files", "VIEW") {
                val intent = Intent(this, BrowsingActivity::class.java)
                intent.putExtra(BrowsingActivity.EXTRA_KEY_COMMIT, node1)
                startActivity(intent)
            })

            val node2 = curSnapshotNode.getParent()?.sha1
            commitItem.add(KeyValueItem("diff", "VS. $index") {
                val intent = Intent(this, DifferenceActivity::class.java)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_1, node1)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_2, node2)
                startActivity(intent)
            })

            curSnapshotNode = curSnapshotNode.getParent()
        }

        adapter.updateDataSet(items)
    }
}
