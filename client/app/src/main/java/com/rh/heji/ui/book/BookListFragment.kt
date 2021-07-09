package com.rh.heji.ui.book

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rh.heji.R
import com.rh.heji.data.db.Book
import com.rh.heji.databinding.FragmentBookBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.widget.CardDecoration

/**
 * Date: 2021/7/9
 * Author: 锅得铁
 * #
 */
class BookListFragment : BaseFragment() {
    val binding by lazy { FragmentBookBinding.bind(rootView) }
    val adapter: BookListAdapter = BookListAdapter()
    override fun layoutId(): Int {
        return R.layout.fragment_book
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "账本"
        showBlack()
    }

    override fun initView(rootView: View) {
        adapter.recyclerView = binding.list
        //binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity(),LinearLayoutManager.HORIZONTAL,false));
        //binding.homeRecycler.layoutManager = LinearLayoutManager(mainActivity)
        binding.list.adapter = adapter
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = 16
                outRect.top = 16
                outRect.bottom = 16
                outRect.right = 16
                //super.getItemOffsets(outRect, view, parent, state)
            }
        })
        adapter.setNewInstance(mutableListOf(Book("1", "个人账本", "19921969586", "日常")))
    }

}