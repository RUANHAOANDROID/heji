package com.hao.heji.ui

import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils
import com.hao.heji.config.Config
import com.hao.heji.utils.YearMonth
import java.util.*

/**
 * @date: 2020/11/3
 * @author: 锅得铁
 * # APP运行时 UI常量共享存储
 */
class MainViewModel : ViewModel() {
    init {
        LogUtils.d(
            "MainViewModel",
            "Config enableOfflineMode=${Config.enableOfflineMode}",
            "Config isInitBook=${Config.book}",
            "Config isInitUser=${Config.user}"
        )
    }
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