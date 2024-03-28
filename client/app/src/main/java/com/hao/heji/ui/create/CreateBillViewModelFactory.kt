package com.hao.heji.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hao.heji.service.sync.IBillSync

/**
 *@date: 2022/6/20
 *Author: 锅得铁
 *#
 */
internal class CreateBillViewModelFactory(private val mBillSync: IBillSync) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === CreateBillViewModel::class.java) {
            return CreateBillViewModel(mBillSync = mBillSync) as T
        }
        throw IllegalArgumentException("类型不匹配")
    }
}