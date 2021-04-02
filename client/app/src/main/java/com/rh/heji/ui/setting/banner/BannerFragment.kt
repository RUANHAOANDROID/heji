package com.rh.heji.ui.setting.banner

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rh.heji.R
import com.rh.heji.ui.base.BaseFragment

class BannerFragment : BaseFragment() {


    private val viewModel: BannerViewModel by lazy {
        getViewModel(BannerViewModel::class.java)
    }


    override fun layoutId(): Int {
        return R.layout.banner_fragment;
    }

    override fun initView(rootView: View) {

    }


}