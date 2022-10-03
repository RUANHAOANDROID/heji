package com.rh.heji.ui.bill.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.rh.heji.App.Companion.currentBook
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryContentBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.category.adapter.CategoryAdapter
import com.rh.heji.ui.bill.category.manager.CategoryManagerFragmentArgs
import com.rh.heji.ui.bill.create.CreateBillFragment
import java.util.function.Consumer

/**
 * @date: 2020/10/11
 * @author: 锅得铁
 * # 收入/支出标签 复用该Fragment
 */
class CategoryFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryContentBinding
    private lateinit var labelAdapter: CategoryAdapter
    //类型 支出 或 收入
    lateinit var type: BillType

    private lateinit var categoryViewModule: CategoryViewModel

    private var labelObserver = Observer { categories: MutableList<Category> ->

        if (null != getSelectedCategory()) {
            categories.stream().forEach { category: Category ->
                val isSelected =
                    category.category == getSelectedCategory()!!.category && category.type == getSelectedCategory()!!.type
                if (isSelected) {
                    category.isSelected = true
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
        categoryViewModule = (parentFragment!!.parentFragment as CreateBillFragment).categoryViewModel
        arguments?.let {
            type = CategoryFragmentArgs.fromBundle(it).type
        }
    }

    override fun initView(view: View) {
        binding = FragmentCategoryContentBinding.bind(view)
        initCategory(ArrayList())
        registerLabelObserver()
    }

    private fun registerLabelObserver() {
        categoryViewModule.let {
            var categoryLiveData =
                if (type == BillType.INCOME) it.getIncomeCategory() else it.getExpenditureCategory()
            categoryLiveData.observe(this, labelObserver)
            categoryLiveData.value?.let { data ->
                if (data.size > 0) labelAdapter.setNewInstance(data)
            }
        }
    }


    //Item点击事件
    private var onItemClickListener =
        OnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val category = labelAdapter.getItem(position) //当前点击的
            if (category.category == CategoryAdapter.SETTING) { //设置
                val args = CategoryManagerFragmentArgs.Builder()
                    .setIeType(type.type()).build()
                findNavController().navigate(R.id.nav_category_manager, args.toBundle())
            }
            category.isSelected = !category.isSelected //反选
            //使其他置为为选中状态
            labelAdapter.data.forEach(Consumer { i: Category ->
                if (i.category != category.category) {
                    i.isSelected = false
                }
            })
            labelAdapter.notifyDataSetChanged()
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
            val count =
                labelAdapter.data.filter { category: Category -> category.isSelected }.count()
            if (count <= 0) {
                val firstItem = labelAdapter.data.stream().findFirst().get()
                if (firstItem.category != "管理") {
                    firstItem.isSelected = true
                    labelAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 添加尾部设置按钮
     *
     * @param labelAdapter
     */
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
        val category = Category(
            category = CategoryAdapter.SETTING,
            bookId = currentBook.id,
            level = 0,
            type = type.type()
        )
        labelAdapter.addData(labelAdapter.itemCount, category)
    }

    fun getSelectedCategory(): Category {
        var selectCategory: Category?
        //选中的类别
        val selectItem =
            labelAdapter.data.filter { category: Category -> category.isSelected }.toList()
        //未选中默认第一个ITEM
        selectCategory = if (selectItem.isEmpty()) labelAdapter.data.first() else selectItem.first()
        return selectCategory.apply { this.type = type }
    }

    fun setSelectCategory(category: String? = null) {
        if (!category.isNullOrEmpty()) {
            binding.categoryRecycler.post {
                labelAdapter.setSelectCategory(category)
            }
        }
    }

    companion object {
        /**
         * 收\支
         *
         * @param type Income : Expenditure
         * @return
         */
        @JvmStatic
        fun newInstance(type: BillType): CategoryFragment {
            val categoryFragment = CategoryFragment()
            categoryFragment.arguments =
                CategoryFragmentArgs.Builder().setType(type).build().toBundle()
            return categoryFragment
        }
    }
}
