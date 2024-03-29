package com.hao.heji.ui.user.info

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hao.heji.config.Config
import com.hao.heji.databinding.FragmentUserInfoBinding
import com.hao.heji.ui.base.BaseFragment

class UserInfoFragment : BaseFragment() {
    private val viewModel by lazy { ViewModelProvider(this)[UserInfoViewModel::class.java] }
    val binding: FragmentUserInfoBinding by lazy {
        FragmentUserInfoBinding.inflate(layoutInflater)
    }

    override fun layout() = binding.root

    override fun initView(rootView: View) {
        binding.tvNickName.text = Config.user.name
        binding.tvTEL.text = Config.user.token
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "个人信息"
    }
}