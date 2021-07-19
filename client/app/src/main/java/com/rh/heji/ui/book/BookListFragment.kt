package com.rh.heji.ui.book

import android.graphics.Rect
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.db.Book
import com.rh.heji.databinding.FragmentBookListBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.base.hideRefreshing
import com.rh.heji.ui.base.swipeRefreshLayout

/**
 * Date: 2021/7/9
 * Author: 锅得铁
 * #
 */
class BookListFragment : BaseFragment() {
    lateinit var adapter: BookListAdapter
    lateinit var binding: FragmentBookListBinding
    private val bookViewModel by lazy { getViewModel(BookViewModel::class.java) }
    override fun layoutId(): Int {
        return R.layout.fragment_book_list
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "账本"
        showBlack()
    }

    override fun initView(rootView: View) {
        binding = FragmentBookListBinding.bind(rootView)

        adapter = BookListAdapter()
        adapter.recyclerView = binding.list
        binding.list.layoutManager = LinearLayoutManager(mainActivity)
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
        adapter.animationEnable = true
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom)
        swipeRefreshLayout(binding.refreshLayout) { bookViewModel.getBookList() }
        bookViewModel.getBookList().observe(this, {
            adapter.setList(it)
            hideRefreshing(binding.refreshLayout)
        })
        listener()
    }

    private fun listener() {
        adapter.setOnItemClickListener { adapter, view, position ->
            AppCache.getInstance().kvStorage?.let {
                val book: Book = adapter.getItem(position) as Book
                mainActivity.setCurrentBook(book.name)
                AppCache.getInstance().currentBook = book
            }
            findNavController().popBackStack()
        }
        binding.fab.setOnClickListener {
            XPopup.Builder(context).asBottomList(
                "", arrayOf("新建账本", "加入他人账本")
            ) { _, text ->
                when (text) {
                    "新建账本" ->
                        findNavController().navigate(R.id.nav_book_add)
                    "加入他人账本" ->
                      ToastUtils.showShort("join Boot")
                }

            }.show()
        }
    }
}