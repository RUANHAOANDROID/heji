package com.rh.heji.ui.setting

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.rh.heji.R
import com.rh.heji.databinding.FragmentSettingBinding
import com.rh.heji.ui.base.BaseFragment

class SettingFragment : BaseFragment() {
    //lateinit var settingViewModel: SettingViewModel
    private val binding: FragmentSettingBinding by lazy { FragmentSettingBinding.inflate(layoutInflater) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun initView(rootView: View) {
        binding.inputETC.setOnClickListener { findNavController().navigate(R.id.nav_input_etc) }
        binding.exportQianJi.setOnClickListener { findNavController().navigate(R.id.nav_export) }
    }

    override fun layout()=binding.root

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "设置"
        toolBar.navigationIcon = blackDrawable()
        toolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

}