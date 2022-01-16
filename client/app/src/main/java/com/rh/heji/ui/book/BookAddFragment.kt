package com.rh.heji.ui.book

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.rh.heji.R
import com.rh.heji.databinding.FragmentBookAddBinding
import com.rh.heji.ui.base.BaseFragment

class BookAddFragment : BaseFragment() {
    private val viewModel: BookViewModel by lazy { ViewModelProvider(this).get(BookViewModel::class.java) }
    lateinit var binding: FragmentBookAddBinding
    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
    }

    override fun layoutId(): Int {
        return R.layout.fragment_book_add
    }

    override fun initView(rootView: View) {
        binding = FragmentBookAddBinding.bind(rootView)
        binding.banner.setOnClickListener { }
        binding.layoutType.setOnClickListener {
            XPopup.Builder(requireContext()).asBottomList("选择账单类型", arrayOf("日常生活", "经营账本", "人情账本", "汽车账本")
            ) { position, text ->
                binding.tvBookType.text = text
            }.show()
        }
        binding.btnCreate.setOnClickListener {
            val name = bookName()
            val type = bookType()
            if (name.isEmpty()) {
                ToastUtils.showShort("请选择填写账本名称")
                return@setOnClickListener
            }
            if (type.isEmpty()||type=="未设置") {
                ToastUtils.showShort("请选择账本类型")
                return@setOnClickListener
            }
            viewModel.createNewBook(name, type).observe(viewLifecycleOwner, {
                findNavController().popBackStack()
            })
        }

    }

    fun bookName() = binding.textInputEdit.text.toString()
    fun bookType() = binding.tvBookType.text.toString()
}