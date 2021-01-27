package com.rh.heji.ui.user.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.R
import com.rh.heji.databinding.FragmentUserInfoBinding

class UserInfoFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(UserInfoViewModel::class.java) }
    lateinit var binding: FragmentUserInfoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_user_info, container, false)
        binding = FragmentUserInfoBinding.bind(view)
        return view
    }

}