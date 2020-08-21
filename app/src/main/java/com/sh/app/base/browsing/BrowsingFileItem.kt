package com.sh.app.base.browsing

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
import kotlinx.android.synthetic.main.common_browsing_file_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class BrowsingFileItem(val browsingFile: IBrowsingFile, private val action: ((item: BrowsingFileItem, itemTop: Int) -> Unit)? = null) : AbstractFlexibleItem<BrowsingFileItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val iconImageView: AppCompatImageView = view.iconImageView
        val nameLabel: TextView = view.nameLabel
        val desLabel: TextView = view.desLabel
        val moreView: View = view.moreView
        val lineView: View = view.lineView
    }

    private var dateStr = ""

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        if (dateStr.isEmpty()) {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
            dateStr = sdf.format(Date(browsingFile.getLastModifyTime()))
        }

        if (browsingFile.isFile()) {
            holder.iconImageView.setImageResource(R.drawable.svg_file_icon_file)
            holder.moreView.visibility = View.INVISIBLE
            holder.desLabel.text = dateStr
        } else {
            holder.iconImageView.setImageResource(R.drawable.svg_file_icon_folder)
            holder.moreView.visibility = View.VISIBLE
            holder.desLabel.text = String.format("%s - %d项", dateStr, browsingFile.getSubCount())
        }

        holder.nameLabel.text = browsingFile.getFileName()

        if (adapter.itemCount == position + 1) {
            holder.lineView.visibility = View.INVISIBLE
        } else {
            holder.lineView.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            action?.invoke(this, holder.itemView.top)
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.common_browsing_file_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}