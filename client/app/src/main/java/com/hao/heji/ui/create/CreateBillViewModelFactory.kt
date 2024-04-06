package com.hao.heji.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hao.heji.service.sync.IBillSync
import com.hao.heji.service.ws.SyncPusher

/**
 *@date: 2022/6/20
 *Author: 锅得铁
 *#
 */
internal class CreateBillViewModelFactory(private val syncPusher: SyncPusher?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === CreateBillViewModel::class.java) {
            return CreateBillViewModel(syncPusher = syncPusher) as T
        }
        throw IllegalArgumentException("类型不匹配")
    }
}