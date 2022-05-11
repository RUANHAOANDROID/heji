package com.rh.heji.ui.user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.LoginActivity
import com.rh.heji.R
import com.rh.heji.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {


    private val viewModel: RegisterViewModel by lazy {
        ViewModelProvider(this).get(RegisterViewModel::class.java)
    }
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_register, container, false)
        binding = FragmentRegisterBinding.inflate(inflater)

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
            viewModel.register(username, tel, code, password1)
        }
        viewModel.registerResult().observe(viewLifecycleOwner) { user ->
            var mBundle = Bundle()
            mBundle.putSerializable("user", user)
            Navigation.findNavController(binding.root).popBackStack()
            Navigation.findNavController(binding.root).navigate(R.id.nav_login, mBundle)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        with(activity as LoginActivity) {
            findViewById<Toolbar>(R.id.toolbar).title = getString(R.string.register)
            this
        }
    }
}