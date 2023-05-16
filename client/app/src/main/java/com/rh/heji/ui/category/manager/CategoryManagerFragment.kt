package com.rh.heji.ui.category.manager

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryManagerBinding
import com.rh.heji.doAction
import com.rh.heji.render
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.category.adapter.CategoryManagerAdapter

/**
 * 类别标签管理
 * @date: 2020/10/10
 * @author: 锅得铁
 *
 */
class CategoryManagerFragment : BaseFragment() {
    val binding: FragmentCategoryManagerBinding by lazy {
        FragmentCategoryManagerBinding.inflate(layoutInflater).apply {
            btnAdd.setOnClickListener { v: View ->
                val name = binding.editCategoryValue.text.toString().trim { it <= ' ' }
                viewModel.doAction(CategoryManagerAction.SaveCategory(name, args.ieType))
                KeyboardUtils.hideSoftInput(v) //隐藏键盘
                binding.editCategoryValue.setText("")
                binding.editCategoryValue.clearFocus() //清除聚焦
            }
        }
    }

    private val viewModel: CategoryManagerViewModel by lazy {
        ViewModelProvider(this)[CategoryManagerViewModel::class.java]
    }
    lateinit var args: CategoryManagerFragmentArgs
    private lateinit var adapter: CategoryManagerAdapter
    override fun layout() = binding.root

    override fun initView(view: View) {
        args = CategoryManagerFragmentArgs.fromBundle(requireArguments())
        viewModel.doAction(CategoryManagerAction.GetCategories(args.ieType))

        binding.categoryRecycler.layoutManager = LinearLayoutManager(context)
        adapter = object : CategoryManagerAdapter() {
            override fun convert(holder: BaseViewHolder, category: Category) {
                super.convert(holder, category)
                itemBinding.btnDelete.setOnClickListener {
                    alertDeleteTip(category)
                }
            }
        }
        binding.categoryRecycler.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render(viewModel) {
            when (it) {
                is CategoryManagerUiState.Categories -> adapter.setNewInstance(it.data)
            }
        }
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.apply {
            title = "分类管理(${BillType.transform(args.ieType).valueString})"
            navigationIcon = blackDrawable()
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun alertDeleteTip(label: Category) {
        XPopup.Builder(requireContext()).asConfirm("提示", "确认删除该标签？") {
            doAction(viewModel, CategoryManagerAction.DeleteCategory(label))
        }.show()
    }

}