package com.rh.heji.ui.setting

import android.content.Context
import android.view.View
import androidx.navigation.Navigation
import com.rh.heji.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.FragmentSettingBinding
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment() {
    lateinit var settingViewModel: SettingViewModel
    lateinit var binding: FragmentSettingBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingViewModel = getViewModel(SettingViewModel::class.java)
        mainActivity.fab.visibility = View.GONE
        mainActivity.toolbar.menu.setGroupVisible(R.id.menu_settings, false)
    }

    override fun initView(view: View) {
        binding = FragmentSettingBinding.bind(view)
        binding.inputETC.setOnClickListener { Navigation.findNavController(view).navigate(R.id.nav_input_etc) }
        binding.exportQianJi.setOnClickListener { Navigation.findNavController(view).navigate(R.id.nav_export) }
    }

    override fun layoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity.fab.visibility = View.VISIBLE
        mainActivity.toolbar.menu.setGroupVisible(R.id.menu_settings, true)
    }
}