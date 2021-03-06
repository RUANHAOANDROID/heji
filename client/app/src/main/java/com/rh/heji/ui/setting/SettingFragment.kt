package com.rh.heji.ui.setting

import android.content.Context
import android.view.View
import androidx.navigation.Navigation
import com.rh.heji.R
import com.rh.heji.databinding.FragmentSettingBinding
import com.rh.heji.ui.base.BaseFragment

class SettingFragment : BaseFragment() {
    lateinit var settingViewModel: SettingViewModel
    lateinit var binding: FragmentSettingBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingViewModel = getViewModel(SettingViewModel::class.java)
    }

    override fun initView(rootView: View) {
        binding = FragmentSettingBinding.bind(rootView)
        binding.inputETC.setOnClickListener { Navigation.findNavController(rootView).navigate(R.id.nav_input_etc) }
        binding.exportQianJi.setOnClickListener { Navigation.findNavController(rootView).navigate(R.id.nav_export) }
    }

    override fun layoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "设置"
        toolBar.navigationIcon = blackDrawable()
        toolBar.setNavigationOnClickListener {
            Navigation.findNavController(rootView).navigateUp()
        }
    }

    override fun onDetach() {
        super.onDetach()
    }
}