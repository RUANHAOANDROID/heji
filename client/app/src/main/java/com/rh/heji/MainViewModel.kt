package com.rh.heji

import androidx.lifecycle.Transformations
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Constant
import com.rh.heji.data.repository.BillRepository
import com.rh.heji.data.repository.CategoryRepository
import com.rh.heji.ui.base.BaseViewModel
import java.util.*

/**
 * Date: 2020/11/3
 * Author: 锅得铁
 * #
 */
class MainViewModel : BaseViewModel() {
    var billRepository = BillRepository()
    var categoryRepository = CategoryRepository()
    var selectYear: Int = Calendar.getInstance()[Calendar.YEAR] //默认为当前时间
    var selectMonth: Int = Calendar.getInstance()[Calendar.MONTH] + 1//默认为当前月份

}