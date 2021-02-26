package com.rh.heji.ui.user.info

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.R
import com.rh.heji.databinding.FragmentUserInfoBinding
import com.rh.heji.ui.base.BaseFragment

class UserInfoFragment : BaseFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(UserInfoViewModel::class.java) }
    lateinit var binding: FragmentUserInfoBinding

    override fun layoutId(): Int {
        return R.layout.fragment_user_info
    }

    override fun initView(view: View) {
        binding = FragmentUserInfoBinding.bind(view)
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "个人信息"
    }
}