package com.rh.heji.ui.book

import android.view.View
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.R
import com.rh.heji.databinding.AddBookFragmentBinding
import com.rh.heji.ui.base.BaseFragment

class AddBookFragment : BaseFragment() {
    private val viewModel: BookViewModel by lazy { getViewModel(BookViewModel::class.java) }
    lateinit var binding: AddBookFragmentBinding
    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
    }

    override fun layoutId(): Int {
        return R.layout.add_book_fragment
    }

    override fun initView(rootView: View) {
        binding = AddBookFragmentBinding.bind(rootView)
        binding.banner.setOnClickListener { }
        binding.layoutType.setOnClickListener { }
        binding.btnSave.setOnClickListener {
            val name =bookName()
            val type =bookType()
            if (name.isEmpty()) {
                ToastUtils.showShort("请选择填写账本名称")
                return@setOnClickListener
            }
            if (type.isEmpty()/**||type=="未设置"**/) {
                ToastUtils.showShort("请选择账本类型")
                return@setOnClickListener
            }
            viewModel.createNewBook(name, type).observe(viewLifecycleOwner,{
                findNavController().popBackStack()
            })
        }

    }

    fun bookName() = binding.textInputEdit.text.toString()
    fun bookType() = binding.tvBookType.text.toString()
}