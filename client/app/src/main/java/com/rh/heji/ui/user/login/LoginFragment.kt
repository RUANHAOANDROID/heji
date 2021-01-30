package com.rh.heji.ui.user.login

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding
import com.rh.heji.ui.base.BaseFragment

class LoginFragment : BaseFragment() {

    lateinit var binding: LoginFragmentBinding;
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun layoutId(): Int {
        return R.layout.login_fragment
    }

    override fun setUpViews() {
        super.setUpViews()
        mainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mainActivity.fab.visibility = View.GONE
        mainActivity.toolbar.visibility = View.GONE
        //拦截回退直接退出  object : Class 内部类
        mainActivity.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.finish()
            }
        })
    }

    override fun restoreVies() {
        super.restoreVies()
        mainActivity.toolbar.visibility = View.VISIBLE
        mainActivity.fab.visibility = View.VISIBLE
        mainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
    }

    override fun initView(view: View?) {
        view?.let { v ->
            binding = LoginFragmentBinding.bind(v);
            binding.tvRegister.setOnClickListener {
                Navigation.findNavController(v).navigate(R.id.nav_register)
            }
            binding.btnLogin.setOnClickListener {
                val username = binding.editUser.text.toString()
                val password = binding.editPassword.text.toString()
                viewModel.login(username, password)
                        .observe(this.viewLifecycleOwner, Observer { token ->
                            //Navigation.findNavController(view).navigate)
                            Navigation.findNavController(view).popBackStack()
                            mainActivity.startSyncDataService()
                            LogUtils.e(token)
                        })
            }
        }
    }

}