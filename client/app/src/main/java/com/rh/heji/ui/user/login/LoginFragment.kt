package com.rh.heji.ui.user.login

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppViewModel
import com.rh.heji.R
import com.rh.heji.databinding.FragmentLoginBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.user.register.RegisterUser

class LoginFragment : BaseFragment() {

    lateinit var binding: FragmentLoginBinding
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.fragment_login
    }

    override fun initView(rootView: View) {
        toolBar.title = getString(R.string.login)
        binding = FragmentLoginBinding.bind(rootView)
        binding.tvRegister.setOnClickListener {
            //findNavController().popBackStack()
            findNavController().navigate(R.id.nav_register)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.editUser.text.toString()
            val password = binding.editPassword.text.toString()
            viewModel.login(username, password)
                .observe(this.viewLifecycleOwner, { token ->
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.nav_home)
                    AppViewModel.get().asyncData()
                    LogUtils.d(token)
                })
        }
        enableDrawer = false
    }

    override fun onResume() {
        super.onResume()
        registerBackPressed {
            mainActivity.finish()
        }
        arguments?.let {
            var user: RegisterUser = it.getSerializable("user") as RegisterUser
            binding.editUser.setText(user.tel)
            binding.editPassword.setText(user.password)
            ToastUtils.showLong(user.name)
        }
    }

}