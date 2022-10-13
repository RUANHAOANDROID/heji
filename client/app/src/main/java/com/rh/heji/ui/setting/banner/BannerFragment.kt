package com.rh.heji.ui.setting.banner

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.R
import com.rh.heji.ui.base.BaseFragment

class BannerFragment : BaseFragment() {


    private val viewModel: BannerViewModel by lazy {
        ViewModelProvider(this).get(BannerViewModel::class.java)
    }


    override fun layout()= TODO()

    override fun initView(rootView: View) {

    }


}