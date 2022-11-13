package com.rh.heji.ui.adapter

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jetbrains.annotations.NotNull

/**
 * @author hao
 */
class NodeBillsAdapter : BaseNodeAdapter() , LoadMoreModule {
    init {
        addNodeProvider(DayIncomeNodeProvider())
        addNodeProvider(DayBillsNodeProvider())
    }

    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        if (position < 0) return -1
        val node = data[position]
        if (node is DayIncomeNode) {
            return 0
        } else if (node is DayBillsNode) {
            return 1
        }
        return -1
    }
}
//class CustomLoadMoreView : BaseLoadMoreView() {
//    @NotNull
//    override fun getRootView(@NotNull parent: ViewGroup): View {
//        return LayoutInflater.from(parent.context).inflate(R.layout.view_load_more, parent, false)
//    }
//
//    @NotNull
//    override fun getLoadingView(@NotNull holder: BaseViewHolder): View {
//        return R.id.load_more_loading_view.findView()
//    }
//
//    @NotNull
//    override fun getLoadComplete(@NotNull holder: BaseViewHolder): View {
//        return R.id.load_more_load_complete_view.findView()
//    }
//
//    @NotNull
//    override fun getLoadEndView(@NotNull holder: BaseViewHolder): View {
//        return R.id.load_more_load_end_view.findView()
//    }
//
//    @NotNull
//    override fun getLoadFailView(@NotNull holder: BaseViewHolder): View {
//        return R.id.load_more_load_fail_view.findView()
//    }
//}