package com.example.kotlinnote.du

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import cn.dujc.core.adapter.BaseQuickAdapter
import cn.dujc.core.initializer.baselist.IBaseListSetup

class ListSetup : IBaseListSetup {
    override fun recyclerViewOtherSetup(
        context: Context,
        recyclerView: RecyclerView,
        adapter: BaseQuickAdapter<*, *>?
    ) { /*if (item_card != null) {
            item_card.setEmptyView(R.layout.layout_empty);
        }*/
    }

    override fun recyclerViewSetupBeforeAdapter(
        context: Context,
        recyclerView: RecyclerView,
        adapter: BaseQuickAdapter<*, *>?
    ) { /*if (item_card != null) {
            item_card.setHeaderFooterEmpty(true, true);
        }*/
    }

    override fun recyclerViewSetupBeforeLayoutManager(
        context: Context,
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager
    ) {
    }

    override fun endGone(): Boolean {
        return true
    }
}