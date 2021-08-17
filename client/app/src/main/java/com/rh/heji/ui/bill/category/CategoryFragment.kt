package com.rh.heji.ui.bill.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryContentBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.add.AddBillFragment
import com.rh.heji.ui.bill.category.adapter.CategoryAdapter
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 收入/支出标签 复用该Fragment
 */
class CategoryFragment : BaseFragment() {
    private lateinit var binding: FragmentCategoryContentBinding
    private lateinit var labelAdapter: CategoryAdapter

    //类型 支出 或 收入
    lateinit var type: BillType

    private lateinit var categoryViewModule: CategoryViewModule

    private var labelObserver = Observer { categories: MutableList<Category> ->
        val selectCategory = categoryViewModule.selectCategory
        if (null != selectCategory) {
            categories.stream().forEach { category: Category ->
                val isSelected =
                    category.category == selectCategory.category && category.type == selectCategory.type
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
        categoryViewModule = (parentFragment!!.parentFragment as AddBillFragment).categoryViewModule
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
                if (type == BillType.INCOME) it.incomeCategory else it.expenditureCategory
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
            val count =
                labelAdapter.data.filter { category: Category -> category.isSelected }.count()
            if (count <= 0) {
                val firstItem = labelAdapter.data.stream().findFirst().get()
                if (firstItem.category != "管理") {
                    firstItem.isSelected = true
                    labelAdapter.notifyDataSetChanged()
                    if (!isHidden && type == categoryViewModule.type) {
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
        val category = Category(category = CategoryAdapter.SETTING, level = 0, type = type.type())
        labelAdapter.addData(labelAdapter.itemCount, category)
    }

    fun setCategory(category: String? = null) {
        if (!this::labelAdapter.isInitialized || labelAdapter == null) return

        var selectCategory: Category?
        val selects =
            labelAdapter.data.filter { category: Category -> category.isSelected }.toList()
        selectCategory = if (selects.isEmpty()) labelAdapter.data.first() else selects.first()
        categoryViewModule.selectCategory = selectCategory
        if (!category.isNullOrEmpty()) {
            binding.categoryRecycler.post {
                labelAdapter.setSelectCategory(category)
            }
        }
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
        fun newInstance(type: BillType): CategoryFragment {
            val categoryFragment = CategoryFragment()
            categoryFragment.arguments =
                CategoryFragmentArgs.Builder().setType(type).build().toBundle()
            return categoryFragment
        }
    }
}
