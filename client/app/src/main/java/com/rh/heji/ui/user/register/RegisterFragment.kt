package com.rh.heji.ui.user.register

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.R
import com.rh.heji.databinding.RegisterFragmentBinding

class RegisterFragment : BaseFragment() {


    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: RegisterFragmentBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.register_fragment;
    }

    override fun initView(view: View?) {
        binding = RegisterFragmentBinding.bind(view!!)
        binding.btnRegister.setOnClickListener {
            val password1 = binding.editPassword.text.toString()
            val password2 = binding.editPassword2.text.toString()
            if (password1 != password2) {
                ToastUtils.showLong("两次输入的密码不一致")
                return@setOnClickListener
            }
            val code = binding.editInviteCode.text.toString()
            val tel = binding.editTEL.text.toString()
            val username =binding.editUserName.text.toString()
            viewModel.register(username,tel, code, password1).observe(viewLifecycleOwner, Observer {

            })
        }
    }


    override fun setUpViews() {
        mainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mainActivity.fab.visibility = View.GONE
        mainActivity.toolbar.visibility = View.VISIBLE
        super.setUpViews()
    }
}