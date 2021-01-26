package com.rh.heji.ui.user.login

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding

class LoginFragment : BaseFragment() {

    private lateinit var viewModel: LoginViewModel
    lateinit var binding: LoginFragmentBinding;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun initView(view: View?) {


        view?.let { v ->
            binding = LoginFragmentBinding.bind(v);
            binding.tvRegister.setOnClickListener {
                Navigation.findNavController(v).navigate(R.id.nav_register)
            }
            binding.btnLogin.setOnClickListener {
                viewModel.login(binding.editUser.text.toString(), binding.editPassword.text.toString())
            }
        }

    }

    override fun layoutId(): Int {
        return R.layout.login_fragment
    }

}