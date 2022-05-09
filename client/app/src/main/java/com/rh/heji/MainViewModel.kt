package com.rh.heji

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Image
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.launchIO
import java.util.*

/**
 * Date: 2020/11/3
 * @author: 锅得铁
 * # APP运行时 UI常量共享存储
 */
class MainViewModel : BaseViewModel() {
    var billRepository = BillRepository()
    var categoryRepository = CategoryRepository()

    private val imageLiveData = MediatorLiveData<MutableList<Image>>()

    /**
     * 全局选择的年月（home to subpage）
     */
    var globalYearMonth: YearMonth =
        YearMonth(Calendar.getInstance()[Calendar.YEAR], Calendar.getInstance()[Calendar.MONTH] + 1)

    /**
     * 当前年月
     */
    val currentYearMonth by lazy {
        YearMonth(
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH] + 1
        )
    }

    fun getBillImages(billId: String): LiveData<MutableList<Image>> {
        return AppDatabase.getInstance().imageDao().findByBillId(billId)
            .asLiveData(viewModelScope.coroutineContext)
    }
}