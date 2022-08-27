package com.rh.heji.ui.bill.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.service.sync.IBillSync

/**
 *Date: 2022/6/20
 *Author: 锅得铁
 *#
 */
class CreateBillViewModelFactory(private val mBillSync: IBillSync) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === CreateBillViewModel::class.java) {
            return CreateBillViewModel(mBillSync = mBillSync) as T
        }
        throw IllegalArgumentException("类型不匹配")
    }
}