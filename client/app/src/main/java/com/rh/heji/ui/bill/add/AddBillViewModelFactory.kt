package com.rh.heji.ui.bill.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.service.sync.IBillSync

/**
 *Date: 2022/6/20
 *Author: 锅得铁
 *#
 */
class AddBillViewModelFactory(private val mBillSync: IBillSync) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === AddBillViewModel::class.java) {
            return AddBillViewModel(mBillSync = mBillSync) as T
        }
        throw IllegalArgumentException("类型不匹配")
    }
}