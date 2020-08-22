package com.sh.app.modules.commit

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.base.snapshot.ObjectFile
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_commit.recycleView
import kotlinx.android.synthetic.main.activity_difference.*

class DifferenceActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "COMPARE_ACTIVITY"

        const val EXTRA_KEY_COMMIT_1 = "EXTRA_KEY_COMMIT_1"
        const val EXTRA_KEY_COMMIT_2 = "EXTRA_KEY_COMMIT_2"
    }

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val handler = Handler()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difference)

        adapter = FlexibleAdapter(items)
        recycleView.adapter = adapter
        recycleView.layoutManager = SmoothScrollLinearLayoutManager(this)
        recycleView.hasFixedSize()

        val node1 = intent.getStringExtra(EXTRA_KEY_COMMIT_1) ?: ""
        val node2 = intent.getStringExtra(EXTRA_KEY_COMMIT_2) ?: ""

        Thread {
            compareCommit(
                    SnapshotManager.createCommitNode(node1)?.getObjectFile(),
                    SnapshotManager.createCommitNode(node2)?.getObjectFile())
            handler.post {
                progressBar.visibility = View.GONE
                adapter.updateDataSet(items)

                if (items.isEmpty()) {
                    emptyLabel.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    private fun compareCommit(objectFile1: ObjectFile?, objectFile2: ObjectFile?) {
        if (objectFile1 == null && objectFile2 == null) {
            items.add(HeadDiffItem("${++index}. Error"))
            return
        }

        if (objectFile1 == null) {
            items.add(HeadDiffItem("${++index}. Delete"))
            items.add(BlobDiffItem(this, objectFile1, objectFile2))
            return
        }

        if (objectFile2 == null) {
            items.add(HeadDiffItem("${++index}. Add"))
            items.add(BlobDiffItem(this, objectFile1, objectFile2))
            return
        }

        if (objectFile1.sha1 == objectFile2.sha1) {
            if (objectFile1.name != objectFile2.name) {
                items.add(HeadDiffItem("${++index}. Name -> Name"))
                items.add(BlobDiffItem(this, objectFile1, objectFile2))
            }
            return
        }

        if (objectFile1.isBlob && objectFile2.isBlob) {
            items.add(HeadDiffItem("${++index}. Blob -> Blob"))
            items.add(BlobDiffItem(this, objectFile1, objectFile2))
            return
        }

        if (objectFile1.isBlob) {
            items.add(HeadDiffItem("${++index}. Tree -> Blob"))
            items.add(BlobDiffItem(this, objectFile1, objectFile2))
            return
        }

        if (objectFile2.isBlob) {
            items.add(HeadDiffItem("${++index}. Blob -> Tree"))
            items.add(BlobDiffItem(this, objectFile1, objectFile2))
            return
        }

        val list1 = objectFile1.getObjectFiles()
        val list2 = ArrayList(objectFile2.getObjectFiles())

        for (objFile1 in list1) {
            var sameSha1Obj: ObjectFile? = null
            for (objFile2 in list2) {
                if (objFile1.sha1 == objFile2.sha1) {
                    sameSha1Obj = objFile2
                    break
                }
            }
            if (sameSha1Obj != null) {
                list2.remove(sameSha1Obj)
                compareCommit(objFile1, sameSha1Obj)
                continue
            }

            var sameNameObj: ObjectFile? = null
            for (objFile2 in list2) {
                if (objFile1.name == objFile2.name) {
                    sameNameObj = objFile2
                    break
                }
            }

            if (sameNameObj != null) {
                list2.remove(sameNameObj)
                compareCommit(objFile1, sameNameObj)
                continue
            }

            compareCommit(objFile1, null)
        }

        for (objectFile in list2) {
            compareCommit(null, objectFile)
        }
    }
}
