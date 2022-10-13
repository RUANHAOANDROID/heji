package com.rh.heji.ui.user.info

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.databinding.FragmentUserInfoBinding
import com.rh.heji.ui.base.BaseFragment

class UserInfoFragment : BaseFragment() {
    private val viewModel by lazy { ViewModelProvider(this)[UserInfoViewModel::class.java] }
    val binding: FragmentUserInfoBinding by lazy {
        FragmentUserInfoBinding.inflate(layoutInflater)
    }

    override fun layout() = binding.root

    override fun initView(rootView: View) {
        binding.tvNickName.text = App.user.name
        binding.tvTEL.text = App.user.token
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "个人信息"
    }
}