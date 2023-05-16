package com.rh.heji.ui.book

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.rh.heji.databinding.FragmentBookAddBinding
import com.rh.heji.ui.base.BaseFragment

class CreateBookFragment : BaseFragment() {
    private val viewModel: BookViewModel by lazy {
        ViewModelProvider(
            this,
            BookViewModelFactory(mainActivity.mService.getBookSyncManager())
        )[BookViewModel::class.java]
    }
    private val binding: FragmentBookAddBinding by lazy {
        FragmentBookAddBinding.inflate(layoutInflater)
    }
    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
    }

    override fun layout(): View {
        return binding.root
    }

    override fun initView(rootView: View) {
        with(binding){
            banner.setOnClickListener { }
            layoutType.setOnClickListener {
                XPopup.Builder(requireContext()).asBottomList(
                    "选择账单类型", arrayOf("日常生活", "经营账本", "人情账本", "汽车账本")
                ) { _, text ->
                    tvBookType.text = text
                }.show()
            }
            btnCreate.setOnClickListener {
                val name = textInputEdit.text.toString()
                val type = tvBookType.text.toString()
                if (name.isEmpty()) {
                    ToastUtils.showShort("请选择填写账本名称")
                    return@setOnClickListener
                }
                if (type.isEmpty() || type == "未设置") {
                    ToastUtils.showShort("请选择账本类型")
                    return@setOnClickListener
                }
                viewModel.createNewBook(name, type)
            }
        }

        viewModel.bookCreate().observe(this) {
            findNavController().popBackStack()
        }
    }

}