package com.rh.heji.ui.user.login

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.user.register.RegisterViewModel

class LoginFragment : BaseFragment() {

    lateinit var binding: LoginFragmentBinding;
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.login_fragment
    }

    override fun initView(view: View?) {
        toolBar.title = getString(R.string.login)
        view?.let { v ->
            binding = LoginFragmentBinding.bind(v);
            binding.tvRegister.setOnClickListener {
                mainActivity.navController.popBackStack()
                mainActivity.navController.navigate(R.id.nav_register)
            }
            binding.btnLogin.setOnClickListener {
                val username = binding.editUser.text.toString()
                val password = binding.editPassword.text.toString()
                viewModel.login(username, password)
                        .observe(this.viewLifecycleOwner, Observer { token ->
                            //Navigation.findNavController(view).navigate)
                            mainActivity.navController.popBackStack()
                            mainActivity.startSyncDataService()
                            AppCache.instance.appViewModule.asyncData()
                            LogUtils.d(token)
                        })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //拦截回退直接退出  object : Class 内部类
        mainActivity.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.finish()
            }
        })
        arguments?.let {
            var user: RegisterViewModel.User = it.getSerializable("user") as RegisterViewModel.User
            binding.editUser.setText(user.name)
            binding.editPassword.setText(user.password)
            ToastUtils.showLong(user.name)
        }
    }

}