package com.rh.heji.ui.user.register

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rh.heji.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding

class RegisterFragment : BaseFragment() {


    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: LoginFragmentBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun initView(view: View?) {
        binding = LoginFragmentBinding.bind(view!!);
        binding.btnLogin.setOnClickListener { TODO() }
        binding.textView4
    }

    override fun layoutId(): Int {
        return R.layout.register_fragment;
    }

}