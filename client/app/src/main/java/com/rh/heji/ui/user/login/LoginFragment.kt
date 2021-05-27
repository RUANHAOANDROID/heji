package com.rh.heji.ui.user.login

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.databinding.FragmentLoginBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.user.register.RegisterUser

class LoginFragment : BaseFragment() {

    lateinit var binding: FragmentLoginBinding;
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.fragment_login
    }

    override fun initView(rootView: View) {
        toolBar.title = getString(R.string.login)
        binding = FragmentLoginBinding.bind(rootView);
        binding.tvRegister.setOnClickListener {
            //mainActivity.navController.popBackStack()
            mainActivity.navController.navigate(R.id.nav_register)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.editUser.text.toString()
            val password = binding.editPassword.text.toString()
            viewModel.login(username, password)
                    .observe(this.viewLifecycleOwner, Observer { token ->
                        mainActivity.navController.popBackStack()
                        mainActivity.navController.navigate(R.id.nav_home)
                        AppCache.instance.appViewModule.asyncData()
                        LogUtils.d(token)
                    })
        }
    }

    override fun onResume() {
        super.onResume()
        registerBackPressed{
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