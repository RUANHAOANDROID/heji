package com.rh.heji.ui.book

import android.view.View
import com.rh.heji.R
import com.rh.heji.databinding.FragmentBookSettingBinding
import com.rh.heji.ui.base.BaseFragment

class BookSettingFragment : BaseFragment() {


    private val viewModel by lazy { getViewModel(BookSettingViewModel::class.java) }
    private lateinit var binding: FragmentBookSettingBinding


    override fun layoutId(): Int {
        return R.layout.fragment_book_setting
    }

    override fun initView(rootView: View) {
        binding = FragmentBookSettingBinding.bind(rootView)

    }

}