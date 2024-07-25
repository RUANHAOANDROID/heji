package com.hao.heji.ui.create

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hao.heji.data.BillType
import com.hao.heji.data.db.Category
import com.hao.heji.databinding.FragmentCategoryContentBinding
import com.hao.heji.ui.base.BaseFragment
import com.hao.heji.ui.create.adapter.SelectCategoryAdapter

/**
 * @date: 2020/10/11
 * @author: 锅得铁
 * # 收入/支出标签 复用该Fragment
 */
internal class CategoryFragment : BaseFragment() {
    val binding: FragmentCategoryContentBinding by lazy {
        FragmentCategoryContentBinding.inflate(layoutInflater)
    }
    private lateinit var labelAdapter: SelectCategoryAdapter

    private val createBillFragment by lazy {
        (parentFragment) as CreateBillFragment
    }
    //选中的标签、默认选择第一个、没有时为空
    private var selectCategory: Category? = null

    //类型 支出 或 收入
    lateinit var type: BillType

    override fun layout() = binding.root

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        layout().post {
            arguments?.let {
                type = CategoryFragmentArgs.fromBundle(it).type
                if (type == BillType.INCOME)//预加载一次
                    createBillFragment.viewModel.doAction(CreateBillAction.GetCategories(type.valueInt))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        layout().post {
            with(createBillFragment) {
                categoryFragment = this@CategoryFragment
                viewModel.doAction(CreateBillAction.GetCategories(type.valueInt))
            }
            createBillFragment.selectedCategory(type.valueInt, selectCategory)
            LogUtils.d(selectCategory)
            LogUtils.d(type)
        }
    }

    override fun initView(view: View) {
        labelAdapter = SelectCategoryAdapter(ArrayList()).apply {
            setDiffCallback(object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.id == newItem.id
                }

            })
            setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
                selectCategory = labelAdapter.getItem(position) //当前点击的
                //使其他置为为选中状态
                labelAdapter.data.forEach {
                    it.isSelected = it.name == selectCategory!!.name
                }
                labelAdapter.notifyDataSetChanged()
                createBillFragment.selectedCategory(type.valueInt, selectCategory!!)
            }
        }
        binding.categoryRecycler.apply {
            layoutManager = GridLayoutManager(mainActivity, 6)
            adapter = labelAdapter
        }
    }


    /**
     *
     *  @see TypeTabFragment.setCategories
     * @param categories
     */
    fun setCategories(categories: MutableList<Category>) {
        LogUtils.d(
            "TimeTest", type,
            TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")
        )
        labelAdapter.setDiffNewData(categories)
        defSelected()
    }

    /**
     * 默认第一个为选中
     */
    private fun defSelected() {
        val data = labelAdapter.data
        if (selectCategory != null) return
        if (data.isNotEmpty() && data.size > 0) {
            val count =
                data.count { category: Category -> category.isSelected }
            if (count <= 0) {
                selectCategory = data.stream().findFirst().get()
                createBillFragment.selectedCategory(type.valueInt, selectCategory!!)
                selectCategory!!.isSelected = true
                labelAdapter.notifyDataSetChanged()
            }
        }
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
            LogUtils.d(type)
            val categoryFragment = CategoryFragment()
            categoryFragment.arguments =
                CategoryFragmentArgs(type).toBundle()
            return categoryFragment
        }
    }
}
