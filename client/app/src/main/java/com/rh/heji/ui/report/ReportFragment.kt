package com.rh.heji.ui.report

import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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
        lineChart()
        setInConsume()
    }

    private fun pieClassify() {

    }

    private fun lineChart() {
        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
            }

            override fun onNothingSelected() {
            }
        });
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.setTouchEnabled(false)
        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.position =XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f
        xAxis.textSize = 13f
        xAxis.axisLineColor = Color.parseColor("#93A8B1")
        xAxis.textColor = Color.parseColor("#566974")
        xAxis.mLabelWidth
        xAxis.granularity = 1f
        xAxis.valueFormatter =DateFormater()

    }

    private val lists: MutableList<MutableList<Entry>>? = null
    private val times: List<String>? = null

    /**
     * 设置
     */
    private fun setInConsume() {
        val xAxisInConsume = binding.lineChart.xAxis
        val list = mutableListOf(
                Entry(1f, 1f),
                Entry(2f, 4f),
                Entry(3f, 5f),
                Entry(4f, 6f),
                Entry(5f, 8f))

        var set1 = LineDataSet(list, "收入")

        val data = LineData(set1)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }

}