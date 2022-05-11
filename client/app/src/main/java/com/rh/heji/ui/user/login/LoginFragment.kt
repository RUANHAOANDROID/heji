package com.rh.heji.ui.user.login

import android.content.Context
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
import com.rh.heji.StartupActivity
import com.rh.heji.databinding.FragmentLoginBinding
import com.rh.heji.ui.user.register.RegisterUser

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_login, container, false)
        binding = FragmentLoginBinding.inflate(inflater)
        initView(binding.root)
        return binding.root
    }

    fun initView(rootView: View) {

        binding = FragmentLoginBinding.bind(rootView)
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.editUser.text.toString()
            val password = binding.editPassword.text.toString()
            viewModel.login(username, password)
                .observe(this.viewLifecycleOwner) { token ->
                    findNavController().popBackStack()
                    (activity as LoginActivity).startMainActivity()
                    AppViewModel.get().asyncData()
                    LogUtils.d(token)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        with(activity as LoginActivity) {
            findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.login)
            this
        }
        arguments?.let {
            var user: RegisterUser = it.getSerializable("user") as RegisterUser
            binding.editUser.setText(user.tel)
            binding.editPassword.setText(user.password)
            ToastUtils.showLong(user.name)
        }
    }

}