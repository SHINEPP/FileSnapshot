package com.sh.app.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.snapshot.SnapshotManager
import com.sh.app.item.CardViewItem
import com.sh.app.item.KeyValueItem
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_commit.*

class CommitActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "COMMIT_ACTIVITY"
    }

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

        var curSnapshot = snapshotNode
        while (curSnapshot != null) {
            val item = CardViewItem(curSnapshot.sha1)
            items.add(item)

            val node1 = curSnapshot.sha1
            val objFile = curSnapshot.getObjectFile()
            item.add(KeyValueItem("all files", "VIEW") {
                val intent = Intent(this, BrowsingActivity::class.java)
                intent.putExtra(BrowsingActivity.EXTRA_KEY_COMMIT, node1)
                startActivity(intent)
            })

            val node2 = curSnapshot.getParent()?.sha1
            item.add(KeyValueItem("compare", "DIFF") {
                val intent = Intent(this, DifferenceActivity::class.java)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_1, node1)
                intent.putExtra(DifferenceActivity.EXTRA_KEY_COMMIT_2, node2)
                startActivity(intent)
            })

            curSnapshot = curSnapshot.getParent()
        }

        adapter.updateDataSet(items)
    }
}
