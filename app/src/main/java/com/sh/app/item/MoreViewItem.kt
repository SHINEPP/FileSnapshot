package com.sh.app.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.common_more_view_item.view.*

class MoreViewItem : AbstractFlexibleItem<MoreViewItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val moreLabel: TextView = view.moreLabel
        val moreImageView: AppCompatImageView = view.moreImageView
    }

    private var moreItemAction: ((isMore: Boolean) -> Unit)? = null

    var isMore = true

    fun setMoreItemAction(action: ((isMore: Boolean) -> Unit)?) {
        this.moreItemAction = action
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (isMore) {
            holder.moreLabel.text = "More item"
            holder.moreImageView.setImageResource(R.drawable.svg_arrow_down)
        } else {
            holder.moreLabel.text = "Less item"
            holder.moreImageView.setImageResource(R.drawable.svg_arrow_up)
        }
        holder.itemView.setOnClickListener {
            moreItemAction?.invoke(isMore)
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.common_more_view_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}