package com.rh.heji.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rh.heji.service.sync.IBookSync


/**
 * view model factory
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
open class BookViewModelFactory(private val mBookSync: IBookSync) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass === BookViewModel::class.java) {
            return BookViewModel(mBookSync = mBookSync) as T
        }
        throw IllegalArgumentException("类型不匹配")
    }

}