package com.sh.app.browsing

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sh.app.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

class BrowsingView : ConstraintLayout {

    private val navItems = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var navAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private val fileItems = ArrayList<AbstractFlexibleItem<*>>()
    private lateinit var fileAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private var curBrowsingFile: IBrowsingFile? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun setBrowsingFile(browsingFile: IBrowsingFile?) {
        this.curBrowsingFile = browsingFile
        updateBrowsing()
    }

    fun back(): Boolean {
        curBrowsingFile?.clear()
        curBrowsingFile = curBrowsingFile?.getParent()
        return if (curBrowsingFile != null) {
            updateBrowsing()
            true
        } else {
            false
        }
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.common_browsing_view, this, true)
        val navRecyclerView: RecyclerView = view.findViewById(R.id.recyclerView1)
        val fileRecyclerView: RecyclerView = view.findViewById(R.id.recycleView2)

        val layoutManager = SmoothScrollLinearLayoutManager(context)
        layoutManager.orientation = SmoothScrollLinearLayoutManager.HORIZONTAL

        navAdapter = FlexibleAdapter(navItems)
        navRecyclerView.adapter = navAdapter
        navRecyclerView.layoutManager = layoutManager
        navRecyclerView.hasFixedSize()

        fileAdapter = FlexibleAdapter(fileItems)
        fileRecyclerView.adapter = fileAdapter
        fileRecyclerView.layoutManager = SmoothScrollLinearLayoutManager(context)
        fileRecyclerView.hasFixedSize()

        updateBrowsing()
    }

    private fun updateBrowsing() {
        // 导航
        navItems.clear()
        var browsingFile = curBrowsingFile
        while (browsingFile != null) {
            if (browsingFile.getFileName().isNotEmpty()) {
                navItems.add(0, BrowsingNavItem(browsingFile) { item ->
                    curBrowsingFile?.clear()
                    curBrowsingFile = item.browsingFile
                    updateBrowsing()
                })
            }
            browsingFile = browsingFile.getParent()
        }

        navAdapter.updateDataSet(navItems)
        if (navItems.isNotEmpty()) {
            navAdapter.smoothScrollToPosition(navItems.size - 1)
        }

        // 文件
        fileItems.clear()
        val browsingFiles = ArrayList(curBrowsingFile?.getBrowsingFiles() ?: emptyList())
        browsingFiles.sortWith(Comparator { o1, o2 ->
            if (o1 === o2) {
                0
            } else if (o1.isFile()) {
                if (o2.isFile()) {
                    o1.getFileName().compareTo(o2.getFileName(), ignoreCase = true)
                } else {
                    1
                }
            } else {
                if (o2.isFile()) {
                    -1
                } else {
                    o1.getFileName().compareTo(o2.getFileName(), ignoreCase = true)
                }
            }
        })
        browsingFiles.forEach {
            fileItems.add(BrowsingFileItem(it) { item ->
                if (!item.browsingFile.isFile()) {
                    curBrowsingFile = item.browsingFile
                    updateBrowsing()
                }
            })
        }

        fileAdapter.updateDataSet(fileItems)
    }
}