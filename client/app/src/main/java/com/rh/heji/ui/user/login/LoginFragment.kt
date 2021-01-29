package com.rh.heji.ui.user.login

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.rh.heji.R
import com.rh.heji.databinding.LoginFragmentBinding
import com.rh.heji.ui.base.BaseFragment
import kotlinx.android.synthetic.main.app_bar_main.*

class LoginFragment : BaseFragment() {

    private lateinit var viewModel: LoginViewModel
    lateinit var binding: LoginFragmentBinding;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        mainActivity.toolbar.visibility =View.GONE
    }

    override fun onDestroyView() {
        mainActivity.toolbar.visibility =View.VISIBLE
        super.onDestroyView()
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

    override fun layoutId(): Int {
        return R.layout.login_fragment
    }

}