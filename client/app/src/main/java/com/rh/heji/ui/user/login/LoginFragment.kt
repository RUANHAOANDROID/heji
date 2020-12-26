package com.rh.heji.ui.user.login

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.BaseFragment
import com.rh.heji.R

class LoginFragment : BaseFragment() {

    private lateinit var viewModel: LoginViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun initView(view: View?) {
        TODO("Not yet implemented")
    }

    override fun layoutId(): Int {
        return R.layout.login_fragment
    }

}