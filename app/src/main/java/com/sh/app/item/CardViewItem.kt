package com.sh.app.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.common_card_view_item.view.*

class CardViewItem(private val title: String, private var clickedAction: ((item: CardViewItem) -> Unit)? = null)
    : AbstractFlexibleItem<CardViewItem.ViewHolder>() {

    companion object {
        private const val MAX_DISPLAY_COUNT = 3
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>, val subAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>) : FlexibleViewHolder(view, adapter) {
        val headLabel: TextView = view.headLabel
        val moreView: View = view.moreView
        val progressBar: ProgressBar = view.progressBar
        val recyclerView: RecyclerView = view.recycleView
    }

    private val srcItems = ArrayList<AbstractFlexibleItem<*>>()

    private val displayItems = ArrayList<AbstractFlexibleItem<*>>()
    private var moreViewItem: MoreViewItem? = null

    var showProgress = false

    fun setClickedAction(action: ((item: CardViewItem) -> Unit)?) {
        clickedAction = action
    }

    fun tryShowFull() {
        if (moreViewItem == null) {
            moreViewItem = MoreViewItem()
        }
        moreViewItem?.isMore = false
    }

    fun add(item: AbstractFlexibleItem<*>) {
        srcItems.add(item)
        updateDisplayItems()
    }

    fun remove(item: AbstractFlexibleItem<*>) {
        srcItems.remove(item)
        updateDisplayItems()
    }

    private fun updateDisplayItems() {
        displayItems.clear()

        if (srcItems.isEmpty()) {
            return
        }

        if (srcItems.size <= MAX_DISPLAY_COUNT) {
            displayItems.addAll(srcItems)
        } else {
            if (moreViewItem == null) {
                moreViewItem = MoreViewItem()
            }

            val isMore = moreViewItem?.isMore ?: true
            if (!isMore) {
                displayItems.addAll(srcItems)
            } else {
                for (item in srcItems) {
                    displayItems.add(item)
                    if (displayItems.size >= MAX_DISPLAY_COUNT) {
                        break
                    }
                }
            }

            displayItems.add(moreViewItem!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        val holder = ViewHolder(view, adapter, FlexibleAdapter(displayItems))
        holder.recyclerView.adapter = holder.subAdapter
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.recyclerView.context)
        return holder
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.headLabel.text = title
        holder.subAdapter.updateDataSet(displayItems)

        moreViewItem?.setMoreItemAction {
            moreViewItem?.isMore = !it
            updateDisplayItems()
            holder.subAdapter.updateDataSet(displayItems)
        }

        if (clickedAction == null) {
            holder.moreView.visibility = View.INVISIBLE
            holder.itemView.setOnClickListener(null)
        } else {
            holder.moreView.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                clickedAction?.invoke(this)
            }
        }

        if (showProgress) {
            holder.progressBar.visibility = View.VISIBLE
            holder.moreView.visibility = View.INVISIBLE
        } else {
            holder.progressBar.visibility = View.GONE
            if (clickedAction != null) {
                holder.moreView.visibility = View.VISIBLE
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.common_card_view_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}