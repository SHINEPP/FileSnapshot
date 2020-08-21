package com.sh.app.modules.commit

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sh.app.R
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.base.snapshot.ObjectFile
import com.sh.app.item.KeyValueItem
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
            Log.d(TAG, "compare(), error")
            items.add(KeyValueItem("error", ""))
            return
        }

        if (objectFile1 == null) {
            Log.d(TAG, "compare(), delete")
            items.add(KeyValueItem("delete", ""))
            showAllFiles(objectFile2)
            return
        }

        if (objectFile2 == null) {
            Log.d(TAG, "compare(), add")
            items.add(KeyValueItem("add", ""))
            showAllFiles(objectFile1)
            return
        }

        if (objectFile1.sha1 == objectFile2.sha1) {
            if (objectFile1.name != objectFile2.name) {
                Log.d(TAG, "compare(), ${objectFile2.name} -> ${objectFile1.name}, path = ${objectFile1.getPath()}")
                items.add(KeyValueItem("${objectFile2.name} -> ${objectFile1.name}", ""))
            }
            return
        }

        if (objectFile1.isBlob && objectFile2.isBlob) {
            Log.d(TAG, "compare(), blob -> blob")
            items.add(KeyValueItem("blob -> blob", ""))
            val path1 = objectFile2.getPath().substringAfter(SnapshotManager.sdcardFile.path)
            val path2 = objectFile2.getPath().substringAfter(SnapshotManager.sdcardFile.path)
            items.add(BlobDiffItem(path1, path2))
            return
        }

        if (objectFile1.isBlob) {
            Log.d(TAG, "compare(), tree -> blob")
            items.add(KeyValueItem("tree -> blob", ""))
            showAllFiles(objectFile2)
            showAllFiles(objectFile1)
            return
        }

        if (objectFile2.isBlob) {
            Log.d(TAG, "compare(), blob -> tree")
            items.add(KeyValueItem("blob -> tree", ""))
            showAllFiles(objectFile2)
            showAllFiles(objectFile1)
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

    private fun showAllFiles(objectFile: ObjectFile?) {
        if (objectFile == null) {
            return
        }

        if (objectFile.isBlob) {
            val path = objectFile.getPath().substringAfter(SnapshotManager.sdcardFile.path)
            Log.d(TAG, "test(), path = $path")
            items.add(KeyValueItem(path, ""))
            return
        }

        val nodes = objectFile.getObjectFiles()
        for (node in nodes) {
            showAllFiles(node)
        }
    }
}
