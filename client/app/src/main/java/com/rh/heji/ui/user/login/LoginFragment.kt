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
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.rh.heji.*
import com.rh.heji.databinding.FragmentLoginBinding
import com.rh.heji.ui.MainActivity
import com.rh.heji.ui.user.register.RegisterUser

class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        renderView()
        return binding.root
    }

    private fun renderView() {
        render(viewModel) {
            when (it) {
                is LoginUiState.LoginSuccess -> {
                    findNavController().popBackStack()
                    (activity as LoginActivity).startMainActivity()
                    LogUtils.d(it.token)
                }
                is LoginUiState.LoginError -> {
                    ToastUtils.showLong("登陆错误:${it.t.message}")
                }
                is LoginUiState.OfflineRun -> {
                    MainActivity.start(requireActivity())
                    activity?.finish()
                }
            }
        }
    }

    fun initView() {
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.editUser.text.toString()
            val password = binding.editPassword.text.toString()
            viewModel.doAction(LoginAction.Login(username, password))
        }
        binding.tvOnlyLocalUse.setOnClickListener {
            XPopup.Builder(requireActivity()).asConfirm(
                "仅离线使用说明",
                "1.不支持合伙记账   \n" +
                        "2.数据仅存储在本地"
            ) {
                viewModel.doAction(LoginAction.EnableOfflineMode)
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle()
        //auto input user info
        autoInputUserInfo()
    }

    private fun autoInputUserInfo() {
        arguments?.let {
            var user: RegisterUser = it.getSerializable("user") as RegisterUser
            binding.editUser.setText(user.tel)
            binding.editPassword.setText(user.password)
            ToastUtils.showLong(user.name)
        }
    }

    private fun setTitle() {
        with(activity as LoginActivity) {
            findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.login)
            this
        }
    }

}