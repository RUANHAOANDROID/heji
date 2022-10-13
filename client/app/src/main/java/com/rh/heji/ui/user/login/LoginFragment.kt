package com.rh.heji.ui.user.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppViewModel
import com.rh.heji.LoginActivity
import com.rh.heji.R
import com.rh.heji.databinding.FragmentLoginBinding
import com.rh.heji.ui.user.register.RegisterUser
import com.rh.heji.uiState

class LoginFragment : Fragment() {
   private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_login, container, false)
        initView(binding.root)
        uiState(viewModel) {
            when (it) {
                is LoginUiState.Success -> {
                    findNavController().popBackStack()
                    (activity as LoginActivity).startMainActivity()
                    AppViewModel.get().asyncData()
                    LogUtils.d(it.token)
                }
                is LoginUiState.Error -> {
                    ToastUtils.showLong("登陆错误:${it.t.message}")

                }
            }
        }
        return binding.root
    }

    fun initView(rootView: View) {
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.editUser.text.toString()
            val password = binding.editPassword.text.toString()
            viewModel.doAction(LoginAction.Login(username, password))
        }
    }

    override fun onResume() {
        super.onResume()
        with(activity as LoginActivity) {
            findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.login)
            this
        }
        //auto input user info
        arguments?.let {
            var user: RegisterUser = it.getSerializable("user") as RegisterUser
            binding.editUser.setText(user.tel)
            binding.editPassword.setText(user.password)
            ToastUtils.showLong(user.name)
        }
    }

}