package com.rh.heji.ui.user.register

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.rh.heji.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding
import com.rh.heji.databinding.RegisterFragmentBinding

class RegisterFragment : BaseFragment() {


    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: RegisterFragmentBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun initView(view: View?) {
        binding = RegisterFragmentBinding.bind(view!!);
        binding.btnRegister.setOnClickListener {
            val password1 = binding.editPassword.text.toString();
            val password2 = binding.editPassword2.text.toString();
            val code =binding.editInviteCode.text.toString();
            val tel =binding.editUser.text.toString();
            viewModel.register(tel,code,password1).observe(viewLifecycleOwner, Observer {

            })
        }
    }

    override fun layoutId(): Int {
        return R.layout.register_fragment;
    }

}