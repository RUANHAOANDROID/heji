package com.hao.heji.ui.user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.hao.heji.ui.user.login.LoginActivity
import com.hao.heji.R
import com.hao.heji.databinding.FragmentRegisterBinding
import com.hao.heji.requireNonEmpty
import com.hao.heji.ui.base.render


class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }
    private val binding: FragmentRegisterBinding by lazy {
        FragmentRegisterBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        renderView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            btnRegister.setOnClickListener {
                val password1 = editPassword.text.toString()
                val password2 = editPassword2.text.toString()
                if (password1 != password2) {
                    ToastUtils.showLong("两次输入的密码不一致")
                    return@setOnClickListener
                }
                val code = editInviteCode.text.toString()
                val tel = editTEL.text.toString()
                val username = editUserName.text.toString()
                try {
                    username.requireNonEmpty("UserName is null!")
                    tel.requireNonEmpty("TEL is null!")
                    password1.requireNonEmpty("password is null!")
                    viewModel.register(username, tel, code, password1)
                } catch (e: Exception) {
                    ToastUtils.showLong(e.message)
                }
            }
        }
    }

    private fun renderView() {
        render(viewModel) {
            when (it) {
                is RegisterUiState.Success -> {
                    toLogin(it.user)
                }

                is RegisterUiState.Error -> {
                    ToastUtils.showLong(it.throwable.message)
                }
            }
        }
    }


    private fun toLogin(user: RegisterUser) {
        var mBundle = Bundle()
        mBundle.putSerializable("user", user)
        Navigation.findNavController(binding.root).popBackStack()
        Navigation.findNavController(binding.root).navigate(R.id.nav_login, mBundle)
    }


    override fun onResume() {
        super.onResume()
        setTitle()
    }

    private fun setTitle() {
        with(activity as LoginActivity) {
            findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.register)
            this
        }
    }
}