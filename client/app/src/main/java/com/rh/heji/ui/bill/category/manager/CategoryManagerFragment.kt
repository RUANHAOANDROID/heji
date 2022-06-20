package com.rh.heji.ui.bill.category.manager

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryManagerBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.category.CategoryViewModel
import com.rh.heji.ui.bill.category.adapter.CategoryManagerAdapter

/**
 * 类别标签管理
 * Date: 2020/10/10
 * @author: 锅得铁
 *
 */
class CategoryManagerFragment : BaseFragment() {
    lateinit var binding: FragmentCategoryManagerBinding
    private lateinit var adapter: CategoryManagerAdapter
    private val categoryViewModule: CategoryViewModel by lazy {
        ViewModelProvider(this).get(
            CategoryViewModel::class.java
        )
    }
    private var args: CategoryManagerFragmentArgs? = null

    override fun onDetach() {
        super.onDetach()
        mainActivity.hideInput()
    }

    override fun initView(view: View) {
        binding = FragmentCategoryManagerBinding.bind(view)
        args = CategoryManagerFragmentArgs.fromBundle(requireArguments())

        binding.btnAdd.setOnClickListener { v: View? ->
            val name = binding.editCategoryValue.text.toString().trim { it <= ' ' }
            categoryViewModule.saveCategory(name, args!!.ieType)
            KeyboardUtils.hideSoftInput(view) //隐藏键盘
            binding.editCategoryValue.setText("")
            binding.editCategoryValue.clearFocus() //清除聚焦
        }
        binding.categoryRecycler.layoutManager = LinearLayoutManager(context)
        adapter = object : CategoryManagerAdapter() {
            override fun convert(holder: BaseViewHolder, category: Category) {
                super.convert(holder, category)
                itemBinding.btnDelete.setOnClickListener { v: View? ->
                    categoryViewModule.deleteCategory(category)
                }
            }
        }
        val isIncomeType = args!!.ieType == BillType.INCOME.type()
        if (isIncomeType)
            categoryViewModule.getIncomeCategory()
        else
            categoryViewModule.getExpenditureCategory()
                .observe(viewLifecycleOwner, categoryObserver)

        binding.categoryRecycler.adapter = adapter
    }

    private val categoryObserver: (t: MutableList<Category>) -> Unit = {
        adapter.setNewInstance(it)
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.let {
            it.title = "分类管理"
            it.navigationIcon = blackDrawable()
            it.setNavigationOnClickListener { v: View? ->
                Navigation.findNavController(rootView).navigateUp()
            }
        }

    }

    override fun layoutId(): Int {
        return R.layout.fragment_category_manager
    }

    private fun alertDeleteTip(label: Category) {}

}