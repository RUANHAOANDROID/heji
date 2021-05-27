package com.rh.heji.ui.user.register

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.FragmentRegisterBinding

class RegisterFragment : BaseFragment() {


    private val viewModel: RegisterViewModel by lazy {
        ViewModelProvider(this).get(RegisterViewModel::class.java)
    }
    private lateinit var binding: FragmentRegisterBinding

    override fun layoutId(): Int {
        return R.layout.fragment_register
    }

    override fun initView(rootView: View) {
        binding = FragmentRegisterBinding.bind(rootView)
        binding.btnRegister.setOnClickListener {
            val password1 = binding.editPassword.text.toString()
            val password2 = binding.editPassword2.text.toString()
            if (password1 != password2) {
                ToastUtils.showLong("两次输入的密码不一致")
                return@setOnClickListener
            }
            val code = binding.editInviteCode.text.toString()
            val tel = binding.editTEL.text.toString()
            val username = binding.editUserName.text.toString()
            viewModel.register(username, tel, code, password1).observe(viewLifecycleOwner, { user ->
                var mBundle = Bundle()
                mBundle.putSerializable("user", user)
                Navigation.findNavController(rootView).popBackStack()
                Navigation.findNavController(rootView).navigate(R.id.nav_login, mBundle)
            })
        }
    }


    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = getString(R.string.register)
    }
}