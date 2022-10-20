package com.rh.heji

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rh.heji.data.db.Image
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.utlis.YearMonth
import java.util.*

/**
 * @date: 2020/11/3
 * @author: 锅得铁
 * # APP运行时 UI常量共享存储
 */
class MainViewModel : ViewModel() {
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
}