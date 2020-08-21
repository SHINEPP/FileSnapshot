package com.sh.app.browsing

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.common_browsing_nav_item.view.*

class BrowsingNavItem(val browsingFile: IBrowsingFile, private val action: ((item: BrowsingNavItem) -> Unit)? = null) : AbstractFlexibleItem<BrowsingNavItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val titleLabel: TextView = view.titleLabel
        val leftLabel: TextView = view.leftLabel
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (position == 0) {
            holder.leftLabel.visibility = View.GONE
        } else {
            holder.leftLabel.visibility = View.VISIBLE
        }

        holder.titleLabel.text = "${browsingFile.getFileName()} "
        if (adapter.itemCount == position + 1) {
            holder.titleLabel.setTextColor(Color.parseColor("#DF000000"))
        } else {
            holder.titleLabel.setTextColor(Color.parseColor("#8A000000"))
        }

        holder.itemView.setOnClickListener { action?.invoke(this) }
    }

    override fun getLayoutRes(): Int {
        return R.layout.common_browsing_nav_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}