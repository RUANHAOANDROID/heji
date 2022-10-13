package com.rh.heji.ui.bill.create.type

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.rh.heji.App.Companion.currentBook
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryContentBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.category.adapter.CategoryAdapter
import com.rh.heji.ui.bill.category.manager.CategoryManagerFragmentArgs
import com.rh.heji.ui.bill.create.CreateBillAction
import com.rh.heji.ui.bill.create.CreateBillFragment
import com.rh.heji.ui.bill.create.CreateBillUIState
import java.util.function.Consumer

/**
 * @date: 2020/10/11
 * @author: 锅得铁
 * # 收入/支出标签 复用该Fragment
 */
class SelectCategoryFragment : BaseFragment() {
    val binding: FragmentCategoryContentBinding by lazy {
        FragmentCategoryContentBinding.bind(
            rootView
        )
    }
    private lateinit var labelAdapter: CategoryAdapter

    private val viewModel by lazy {
        (((parentFragment)?.parentFragment) as CreateBillFragment).viewModel
    }

    //类型 支出 或 收入
    lateinit var type: BillType

    override fun layoutId(): Int {
        return R.layout.fragment_category_content
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            type = SelectCategoryFragmentArgs.fromBundle(it).type
        }

    }

    override fun onResume() {
        super.onResume()
        LogUtils.d(type)
        viewModel.doAction(CreateBillAction.GetCategories(type.type()))
    }

    override fun initView(view: View) {
        labelAdapter = CategoryAdapter(ArrayList())
        binding.categoryRecycler.apply {
            layoutManager = GridLayoutManager(mainActivity, 6)
            adapter = labelAdapter
        }
        labelAdapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val category = labelAdapter.getItem(position) //当前点击的
            if (category.name == CategoryAdapter.SETTING) { //设置
                val args = CategoryManagerFragmentArgs.Builder().setIeType(type.type()).build()
                findNavController().navigate(R.id.nav_category_manager, args.toBundle())
            }
            category.isSelected = !category.isSelected //反选
            //使其他置为为选中状态
            labelAdapter.data.forEach(Consumer { i: Category ->
                if (i.name != category.name) {
                    i.isSelected = false
                }
            })
            labelAdapter.notifyDataSetChanged()
        }
        addCategoryFooterView(labelAdapter) //尾部添加设置按钮
    }


    /**
     *
     *  @see TypeTabFragment.setCategories
     * @param categories
     */
    fun setCategories(categories: MutableList<Category>) {
        categories.forEach { category: Category ->
            val isSelected =
                category.name == getSelectedCategory().name && category.type == getSelectedCategory().type
            if (isSelected) {
                category.isSelected = true
            }
        }
        labelAdapter.setNewInstance(categories)
        addCategoryFooterView(labelAdapter)
        defSelected()
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
                if (firstItem.name != "管理") {
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
        if (labelAdapter.data.size > 0) {
            val lastItem = labelAdapter.data[labelAdapter.itemCount - 1]
            if (lastItem.name != CategoryAdapter.SETTING) {
                addSettingItem(labelAdapter)
            }
        } else {
            addSettingItem(labelAdapter)
        }
        labelAdapter.notifyDataSetChanged()
    }

    private fun addSettingItem(labelAdapter: CategoryAdapter) {
        val category = Category(
            name = CategoryAdapter.SETTING,
            bookId = currentBook.id,
        ).apply { level = 0 }
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
        fun newInstance(type: BillType): SelectCategoryFragment {
            val categoryFragment = SelectCategoryFragment()
            categoryFragment.arguments =
                SelectCategoryFragmentArgs.Builder().setType(type).build().toBundle()
            return categoryFragment
        }
    }
}
