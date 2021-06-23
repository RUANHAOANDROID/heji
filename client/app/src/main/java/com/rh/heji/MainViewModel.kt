package com.rh.heji

import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.YearMonth
import java.util.*

/**
 * Date: 2020/11/3
 * Author: 锅得铁
 * # APP运行时 UI常量共享存储
 */
class MainViewModel : BaseViewModel() {
    var billRepository = BillRepository()
    var categoryRepository = CategoryRepository()
    var globalYearMonth: YearMonth =
        YearMonth(Calendar.getInstance()[Calendar.YEAR], Calendar.getInstance()[Calendar.MONTH] + 1)
}