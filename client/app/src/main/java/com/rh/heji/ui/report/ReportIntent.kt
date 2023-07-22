package com.rh.heji.ui.report

import com.github.mikephil.charting.data.PieEntry
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.dto.BillTotal
import com.rh.heji.data.db.dto.Income
import com.rh.heji.data.db.dto.IncomeTimeSurplus
import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState
import com.rh.heji.utils.YearMonth

/**
 * @see ReportViewModel.doAction
 *
 */
sealed interface ReportAction : IAction {

    /**
     * 选择时间
     * @property yearMonth 月 或 全年
     */
    class SelectTime(val yearMonth: YearMonth) : ReportAction

    /**
     * 统计总览
     */
    class Total(val yearMonth: YearMonth) : ReportAction

    /**
     * 获取折线图数据
     */
    class GetLinChartData(val type: Int) : ReportAction

    /**
     * 获取分类占比数据
     */
    class GetProportionChart(val type: Int) : ReportAction

    /**
     * 获取统计报表
     */
    object GetReportList : ReportAction

    /**
     * 获取账单图片
     *
     * @property bid
     */
    class GetImages(val img_ids: MutableList<String>) : ReportAction

    /**
     * 获取分类账单列表
     *
     */
    class GetCategoryBillList(val type: Int, val category: String) : ReportAction

    /**
     * 获取统计账单下的详情列表
     *
     * @property yearMonth
     */
    class GetReportBillInfoList(val yearMonth: YearMonth) : ReportAction


}

/**
 * @see ReportFragment
 *
 */
sealed interface ReportUiState : IUiState {
    /**
     * 统计总览
     */
    class Total(val data: Income) : ReportUiState

    /**
     * 折线图
     *
     * @property type
     * @property data
     */
    class LinChart(val type: Int, val data: MutableList<BillTotal> = mutableListOf()) :
        ReportUiState {
        var all: ArrayList<MutableList<BillTotal>> = arrayListOf()
    }

    /**
     * 占比（饼状和条形）
     *
     * @property data
     */
    class ProportionChart(val type: Int, val data: MutableList<PieEntry>) : ReportUiState

    /**
     * 报表
     * @property data
     */
    class ReportList(val data: MutableList<IncomeTimeSurplus>) : ReportUiState

    /**
     * 账单图片
     *
     * @property data images
     */
    class Images(val data: MutableList<Image>) : ReportUiState

    /**
     * 分类账单列表 show Popup bill list
     *
     * @property data
     */
    class CategoryList(val category: String, val data: MutableList<Bill>) : ReportUiState

    /**
     * 列表 day list
     *
     * @property data
     */
    class ReportBillInfoList(val time: String, val data: MutableList<Bill>) : ReportUiState
}