package com.sh.app.modules.commit

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import com.sh.app.base.snapshot.SnapshotManager
import com.sh.app.base.snapshot.ObjectFile
import com.sh.app.modules.common.browsingText
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.layout_object_diff_item.view.*
import java.io.File

class ObjectDiffItem(private val context: Context, private val objectFile1: ObjectFile?, private val objectFile2: ObjectFile?) : AbstractFlexibleItem<ObjectDiffItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val pathLabel1: TextView = view.pathLabel1
        val pathLabel2: TextView = view.pathLabel2
        val moreView1: View = view.moreView1
        val moreView2: View = view.moreView2
    }

    private val path1: String = objectFile1?.getPath()?.substringAfter(SnapshotManager.sdcardFile.path)
            ?: "※※※※※※※※※※※※※※※※※※※"
    private val path2: String = objectFile2?.getPath()?.substringAfter(SnapshotManager.sdcardFile.path)
            ?: "※※※※※※※※※※※※※※※※※※※"

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.pathLabel1.text = path1
        holder.pathLabel2.text = path2

        holder.pathLabel1.setOnClickListener {
            open(objectFile1)
        }

        holder.moreView1.setOnClickListener {
            open(objectFile1)
        }

        holder.pathLabel2.setOnClickListener {
            open(objectFile2)
        }

        holder.moreView2.setOnClickListener {
            open(objectFile2)
        }
    }

    private fun open(objectFile: ObjectFile?) {
        objectFile ?: return
        if (objectFile.isBlob) {
            if (objectFile != objectFile2) {
                val file = File(objectFile.getPath())
                if (file.exists()) {
                    file.browsingText(context)
                }
            }
        } else {
            BrowsingActivity.gObjectFile = objectFile
            context.startActivity(Intent(context, BrowsingActivity::class.java))
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.layout_object_diff_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}