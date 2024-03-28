package com.hao.heji.ui.setting.banner

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hao.heji.ui.base.BaseFragment

class BannerFragment : BaseFragment() {


    private val viewModel: BannerViewModel by lazy {
        ViewModelProvider(this)[BannerViewModel::class.java]
    }


    override fun layout()= TODO()

    override fun initView(rootView: View) {

    }


}