 package com.rh.heji.ui.category

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryContentBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.add.AddBillFragment
import com.rh.heji.ui.category.adapter.CategoryAdapter
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 收入支出标签
 */
class CategoryFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryContentBinding
    private lateinit var labelAdapter: CategoryAdapter
    private var thisType = 0
    private var tabType: BillType? = null

    private lateinit var categoryViewModule: CategoryViewModule

    private var labelObserver = Observer { categories: MutableList<Category> ->
        val selectCategory = categoryViewModule.selectCategory
        if (null != selectCategory) {
            categories.stream().forEach { category: Category ->
                val isSelected = category.category == selectCategory.category && category.type == selectCategory.type
                if (isSelected) {
                    category.selected = true
                }
            }
        }
        labelAdapter.setNewInstance(categories)
        addCategoryFooterView(labelAdapter)
        defSelected()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryViewModule = (parentFragment!!.parentFragment as AddBillFragment).categoryViewModule
        arguments?.let {
            tabType = categoryViewModule.type
            thisType = it.getInt(KEY_TYPE)
        }
    }

    override fun initView(view: View) {
        binding = FragmentCategoryContentBinding.bind(view)
        initCategory(ArrayList())
        registerLabelObserver()
    }

    private fun registerLabelObserver() {
        categoryViewModule.let {
            var categoryLiveData = if (thisType == BillType.INCOME.type()) it.incomeCategory else it.expenditureCategory
            categoryLiveData.observe(this, labelObserver)
            categoryLiveData.value?.let { data ->
                if (data.size > 0) labelAdapter.setNewInstance(data)
            }
        }
    }


    //Item点击事件
    private var onItemClickListener = OnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
        val category = labelAdapter.getItem(position) //当前点击的
        if (category.category == CategoryAdapter.SETTING) { //设置
            val args = CategoryManagerFragmentArgs.Builder()
                    .setIeType(thisType).build()
            mainActivity.navController.navigate(R.id.nav_category_manager, args.toBundle())
        }
        category.selected = !category.selected //反选
        //使其他置为为选中状态
        labelAdapter.data.forEach(Consumer { i: Category ->
            if (i.category != category.category) {
                i.selected = false
            }
        })
        labelAdapter.notifyDataSetChanged()
        categoryViewModule.selectCategory = category
    }

    override fun layoutId(): Int {
        return R.layout.fragment_category_content
    }

    private fun initCategory(categories: MutableList<Category>) {
        labelAdapter = CategoryAdapter(categories)
        binding.categoryRecycler.layoutManager = GridLayoutManager(mainActivity, 6)
        binding.categoryRecycler.adapter = labelAdapter
        labelAdapter.setOnItemClickListener(onItemClickListener)
        addCategoryFooterView(labelAdapter) //尾部添加设置按钮
    }

    /**
     * 默认第一个为选中
     */
    private fun defSelected() {
        if (labelAdapter.data.isNotEmpty() && labelAdapter.data.size > 0) {
            val count = labelAdapter.data.stream().filter { category: Category -> category.selected }.count()
            if (count <= 0) {
                val firstItem = labelAdapter.data.stream().findFirst().get()
                if (firstItem.category != "管理") {
                    firstItem.selected = true
                    labelAdapter.notifyDataSetChanged()
                    if (!isHidden && thisType == tabType!!.type()) {
                        categoryViewModule.selectCategory = firstItem
                    }
                }
            }
        }
    }

    private fun addCategoryFooterView(labelAdapter: CategoryAdapter) {
        if (labelAdapter.data != null && labelAdapter.data.size > 0) {
            val lastItem = labelAdapter.data[labelAdapter.itemCount - 1]
            if (lastItem.category != CategoryAdapter.SETTING) {
                addSettingItem(labelAdapter)
            }
        } else {
            addSettingItem(labelAdapter)
        }
        labelAdapter.notifyDataSetChanged()
    }

    private fun addSettingItem(labelAdapter: CategoryAdapter) {
        val category = Category(CategoryAdapter.SETTING, 0, thisType)
        labelAdapter.addData(labelAdapter.itemCount, category)
    }

    fun setCategory() {
        if (!this::labelAdapter.isInitialized || labelAdapter == null) return
        var selectCategory: Category?
        val selects = labelAdapter.data.stream().filter { category: Category -> category.selected }.collect(Collectors.toList())
        selectCategory = if (selects.size <= 0) labelAdapter.data.stream().findFirst().get() else selects.stream().findFirst().get()
        categoryViewModule.selectCategory = selectCategory
    }

    companion object {
        const val KEY_TYPE = "TYPE"

        /**
         * 收\支
         *
         * @param ieType Income : Expenditure
         * @return
         */
        @JvmStatic
        fun newInstance(ieType: Int): CategoryFragment {
            val ieCategoryFragment = CategoryFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, ieType)
            ieCategoryFragment.arguments = bundle
            return ieCategoryFragment
        }
    }
}
