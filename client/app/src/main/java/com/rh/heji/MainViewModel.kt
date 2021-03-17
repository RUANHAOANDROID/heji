package com.rh.heji

import androidx.lifecycle.Transformations
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Constant
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.ui.base.BaseViewModel

/**
 * Date: 2020/11/3
 * Author: 锅得铁
 * #
 */
class MainViewModel : BaseViewModel() {
    /**
     * 控制toolbar
     */
    var homeUUID: String? = null
    var reportUUID: String? = null
    var settingUUID: String? = null
    var deleteLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().observeSyncStatus(Constant.STATUS_DELETE))
    var notUploadLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().billDao().observeSyncStatus(Constant.STATUS_NOT_SYNC))
    var categoryLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().categoryDao().observeNotUploadOrDelete())
    var imagesLiveData = Transformations.distinctUntilChanged(AppDatabase.getInstance().imageDao().observerNotDownloadImages())

    var billRepository = BillRepository()
    var categoryRepository = CategoryRepository()
}