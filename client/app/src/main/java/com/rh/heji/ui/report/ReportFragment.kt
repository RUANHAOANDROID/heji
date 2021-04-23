package com.rh.heji.ui.report

import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.rh.heji.R
import com.rh.heji.data.db.query.Income
import com.rh.heji.databinding.FragmentReportBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.home.BillsHomeViewModel
import com.rh.heji.utlis.MyTimeUtils
import java.math.BigDecimal


/**
 * 报告统计页面
 */
class ReportFragment : BaseFragment() {
    val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    lateinit var binding: FragmentReportBinding
    private val reportViewModel: ReportViewModel by lazy {
        getViewModel(ReportViewModel::class.java)
    }


    override fun layoutId(): Int {
        return R.layout.fragment_report
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "统计"
        showYearMonthTitle({ year, month ->
            ToastUtils.showLong("$year,$month")
        })
    }

    /**
     * 收入/支出 预览
     */
    private val incomeExpenditureObserver: (t: Income) -> Unit = {
        it?.let { income ->
            if (income.expenditure == null) income.expenditure = BigDecimal("0")
            if (income.income == null) income.income = BigDecimal("0")
            binding.tvIncomeValue.text = income.income.toString()
            binding.tvExpenditureValue.text = income.expenditure.toString()
            val jieYu = income.income!!.minus(income.expenditure!!)//结余
            binding.tvJieYuValue.text = jieYu.toString()
            val dayCount = MyTimeUtils.lastDayOfMonth(homeViewModel.year, homeViewModel.month).split("-")[2].toInt()//月份天数
            binding.tvDayAVGValue.text = jieYu.divide(BigDecimal(dayCount), 2, BigDecimal.ROUND_DOWN)?.toString()//平均值
        }

    }

    override fun initView(rootView: View) {
        binding = FragmentReportBinding.bind(rootView)
        reportViewModel.text.observe(viewLifecycleOwner, { })
        homeViewModel.getIncomeExpense(homeViewModel.year, homeViewModel.month).observe(this, incomeExpenditureObserver)

    }

    private val pieClassify: (any: Any) -> Unit = {

    }
    private val lineChart: (any: Any) -> Unit = {
        binding.lineChart
        val xAxisInConsume: XAxis = binding.lineChart.xAxis
        xAxisInConsume.setDrawAxisLine(true)
        xAxisInConsume.setDrawGridLines(false)
        xAxisInConsume.axisMinimum = 0f
        xAxisInConsume.textSize = 13f
        xAxisInConsume.position = XAxis.XAxisPosition.BOTTOM
        xAxisInConsume.axisLineColor = Color.parseColor("#93A8B1")
        xAxisInConsume.textColor = Color.parseColor("#566974")
        val left: YAxis = binding.lineChart.axisLeft
        left.axisMinimum = 0f
        binding.lineChart.axisRight.isEnabled = false
        left.setDrawGridLines(false)
        left.axisLineColor = Color.parseColor("#93A8B1")
        left.textColor = Color.parseColor("#566974")
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.description.isEnabled = false
        binding.lineChart.extraBottomOffset = 5f
        binding.lineChart.setTouchEnabled(false)
    }
    private val lists: MutableList<MutableList<Entry>>? = null
    private val times: List<String>? = null
    /**
     * 设置
     */
    private fun setInConsume() {
        val xAxisInConsume =binding.lineChart.xAxis
        val   yVals = MutableList
        var set1: LineDataSet? = LineDataSet(,"")
        var set2: LineDataSet? = null
        var size = 0
        set1!!.setDrawCircleHole(false)
        set1.setCircleColor(Color.RED)
        set1.circleRadius = 2f
        set1.color = Color.RED
        set1.setDrawValues(false)
        set1.setDrawFilled(true)
        set1.fillColor = Color.RED
        set1.fillAlpha = 50
        set2!!.setDrawCircleHole(false)
        set2.setCircleColor(Color.GREEN)
        set2.circleRadius = 2f
        set2.color = Color.GREEN
        set2.setDrawValues(false)
        set2.setDrawFilled(true)
        set2.fillColor = Color.GREEN
        set2.fillAlpha = 50
        val data = LineData(set1, set2)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }
}