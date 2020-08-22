package com.sh.app.modules.commit

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import com.sh.app.utils.toDatetimeString
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.layout_head_diff_item.view.*

class HeadDiffItem(private val title: String, time: Long = 0) : AbstractFlexibleItem<HeadDiffItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val titleLabel: TextView = view.titleLabel
        val dateLabel: TextView = view.dateLabel
        val topView: View = view.topView
    }

    private val datetime = if (time > 0) time.toDatetimeString() else ""

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.titleLabel.text = title
        holder.dateLabel.text = datetime
        if (position == 0) {
            holder.topView.visibility = View.GONE
        } else {
            holder.topView.visibility = View.VISIBLE
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.layout_head_diff_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}