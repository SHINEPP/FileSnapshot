package com.sh.app.base.browsing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import java.io.File

class TextBrowsingView : ConstraintLayout {

    private val items = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var adapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private var file: File? = null

    fun setFile(file: File) {
        this.file = file

        items.clear()

        if (file.exists() && file.isFile) {
            val inputStream = file.inputStream()
            val size = inputStream.available()
            inputStream.close()

            if (size <= 2 * 1024 * 1024) {
                val lines = file.readLines(Charsets.US_ASCII)
                for (line in lines) {
                    val item = BrowsingTextItem()
                    item.setText(line)
                    items.add(item)
                }
            }
        }

        adapter.updateDataSet(items)
    }

    fun clear() {
        items.clear()
        adapter.updateDataSet(items)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.common_text_browsing_view, this, true)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        adapter = FlexibleAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = SmoothScrollLinearLayoutManager(context)
        recyclerView.hasFixedSize()
    }
}