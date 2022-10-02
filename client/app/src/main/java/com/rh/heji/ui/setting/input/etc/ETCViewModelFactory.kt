package com.rh.heji.ui.setting.input.etc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.service.sync.IBillSync

/**
 *@date: 2022/6/20
 *Author: 锅得铁
 *#
 */
class ETCViewModelFactory(private val mBillSync: IBillSync) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === ETCViewModel::class.java)
            return ETCViewModel(mBillSync) as T
        throw IllegalArgumentException("类型不匹配")
    }
}