package com.sh.app.item

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.common_key_value_item.view.*

class KeyValueItem constructor(private var key: String, private var value: String, private var valueAction: (() -> Unit)? = null)
    : AbstractFlexibleItem<KeyValueItem.ViewHolder>() {

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val keyLabel: TextView = view.keyLabel
        val valueLabel: TextView = view.valueLabel
        val lineView: View = view.lineView
    }

    fun setKey(key: String) {
        this.key = key
    }

    fun setValue(value: String) {
        this.value = value
    }

    fun setValueAction(action: (() -> Unit)?) {
        this.valueAction = action
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.keyLabel.text = key
        holder.valueLabel.text = value

        if (adapter.itemCount == position + 1) {
            holder.lineView.visibility = View.INVISIBLE
        } else {
            holder.lineView.visibility = View.VISIBLE
        }

        if (valueAction != null) {
            holder.valueLabel.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            holder.valueLabel.setTextColor(Color.parseColor("#ff1d5795"))
            holder.valueLabel.setOnClickListener {
                valueAction?.invoke()
            }
        } else {
            holder.valueLabel.paintFlags = Paint.ANTI_ALIAS_FLAG
            holder.valueLabel.setTextColor(Color.parseColor("#df000000"))
            holder.valueLabel.setOnClickListener(null)
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.common_key_value_item
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}