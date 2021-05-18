package com.rh.heji.ui.report

import android.graphics.Color
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.TimeUtils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.query.Income
import com.rh.heji.databinding.FragmentReportBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.home.BillsHomeViewModel
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import org.w3c.dom.Entity
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors


/**
 * 报告统计页面
 */
class ReportFragment : BaseFragment() {
    private val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    private val reportViewModel: ReportViewModel by lazy { getViewModel(ReportViewModel::class.java) }
    private val categoryTotalAdapter :CategoryTotalAdapter = CategoryTotalAdapter(mutableListOf())
    val monthYearBillsAdapter :MonthYearBillAdapter = MonthYearBillAdapter(mutableListOf())

    lateinit var binding: FragmentReportBinding
    override fun onStart() {
        super.onStart()
        reportViewModel.yearMonth = YearMonth(homeViewModel.year, homeViewModel.month)
    }

    override fun layoutId(): Int {
        return R.layout.fragment_report
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "统计"
        showYearMonthTitle({ year, month ->

            if (month == 0) {//全年

            } else {//单月
                reportViewModel.yearMonth = YearMonth(year, month)
            }

        }, showAllYear = true)
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
        initPieChartCategory()
    }

    private fun lineChart() {
        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
            }

            override fun onNothingSelected() {
            }
        });
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.setTouchEnabled(true)
        var markerView: MarkerView = object : MarkerView(mainActivity, R.layout.marker_linechart) {
            val markerContext = findViewById<TextView>(R.id.tvContext)
            override fun refreshContent(e: Entry, highlight: Highlight?) {
                super.refreshContent(e, highlight)

                markerContext.text = SpannableString("${e.x.toInt()}日\n${e.data}:${e.y}")
            }
        }
        markerView.chartView = binding.lineChart
        binding.lineChart.marker = markerView

        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        //xAxis.axisMinimum = 1f
        xAxis.textSize = 12f
        xAxis.axisLineColor = Color.parseColor("#93A8B1")
        xAxis.textColor = Color.parseColor("#566974")
        xAxis.mLabelWidth
        xAxis.granularity = 1f
        //xAxis.valueFormatter = LargeValueFormatter()
        //xAxis.labelCount = 11//强制显示X 不设置则缩
        xAxis.labelRotationAngle = 30f
        binding.lineChart.axisRight.isEnabled = false

        binding.lineChart.axisLeft.valueFormatter = LargeValueFormatter()
        binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter()
    }

    private val lists: MutableList<MutableList<Entry>>? = null
    private val times: List<String>? = null

    /**
     * 设置
     */
    private fun setInConsume() {
        val xAxisInConsume = binding.lineChart.xAxis
        var dayCount = MyTimeUtils.getMonthLastDay(reportViewModel.yearMonth.year, reportViewModel.yearMonth.month)
        val list = mutableListOf<Entry>()
        val dayMap = mutableMapOf<Int,Entry>()
        for (day in 0..dayCount) {
            dayMap.replace(day,Entry(0f,0f))
        }
        list.clear()
        var entrys = AppDatabase.getInstance().billDao().findByMonth(reportViewModel.yearMonth.toString()).stream().map {
            val day = DateConverters.date2Str(it.billTime).split(" ")[0].split("-")[2].toFloat()
            var type = if (it.type == -1) "支出" else "收入"
            dayMap.replace(day.toInt(),Entry(day, it.money.toFloat(), type))
            return@map Entry(day, it.money.toFloat(), type)
        }.collect(Collectors.toList())

        var set1 = LineDataSet(entrys, "收入")

        val data = LineData(set1)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }

    fun initPieChartCategory() {
        val chart = binding.pieChartCategory
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 5f, 5f, 5f)

        chart.dragDecelerationFrictionCoef = 0.95f

        //chart.setCenterTextTypeface(tfLight)
        //chart.setCenterText(generateCenterSpannableText())

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)
        chart.holeRadius = 50f
        chart.transparentCircleRadius = 55f
        chart.centerText = "收/支比例"
        chart.setDrawCenterText(true)

        chart.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pieEntry = e as PieEntry
                chart.centerText = SpannableString("${pieEntry.label}\n${pieEntry.data}")

            }

            override fun onNothingSelected() {
            }
        })


        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
//        val l: Legend = chart.getLegend()
//        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(false)
//        l.xEntrySpace = 7f
//        l.yEntrySpace = 0f
//        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        //chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
        chart.setUsePercentValues(true)
        chart.setDrawEntryLabels(true)
        reportViewModel.categoryProportion(reportViewModel.yearMonth).observe(this, androidx.lifecycle.Observer {
            val entries = ArrayList<PieEntry>()
            it.forEach {
                entries.add(it)
            }
            setCategoryData(entries)
            updateCategoryListView(entries)
            updateMonthYearBillListView()
        })
        chart.invalidate()
    }



    fun setCategoryData(entries: ArrayList<PieEntry>) {


        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//
//        for (i in 0 until count) {
//            entries.add(PieEntry((Math.random() * range + range / 5).toFloat(),
//                    parties[i % parties.size]))
//        }

        val dataSet = PieDataSet(entries, " ")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors


        // add a lot of colors
        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);

        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChartCategory))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight)
        binding.pieChartCategory.setData(data)

        //设置描述的位置
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        dataSet.valueLinePart1Length = 0.5f;//设置描述连接线长度
        //dataSet.valueLineColor = mainActivity.getColor(R.color.colorPrimary)
        dataSet.setValueTextColors(colors)
        //设置数据的位置
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        dataSet.valueLinePart2Length = 0.5f;//设置数据连接线长度
        // undo all highlights

        // undo all highlights
        binding.pieChartCategory.highlightValues(null)

        binding.pieChartCategory.invalidate()
    }

    fun updateCategoryListView(entries: ArrayList<PieEntry>){
        binding.recyclerCategory.layoutManager =LinearLayoutManager(activity)
        binding.recyclerCategory.adapter=categoryTotalAdapter
        var data=AppDatabase.getInstance().billDao().findByMonthGroupByCategory("2021-05",BillType.EXPENDITURE.type())
        categoryTotalAdapter.setNewInstance(entries)
    }
    fun updateMonthYearBillListView(){
        binding.recyclerBaobiao.layoutManager =LinearLayoutManager(activity)
        binding.recyclerBaobiao.adapter=monthYearBillsAdapter
        var data =AppDatabase.getInstance().billDao().listIncomeExpSurplusByMonth("2021-05")
        monthYearBillsAdapter.setNewInstance(data);
    }
    fun ininBaobiao() {
        binding.tvBaobiao.text = "月报表"
    }
}