package com.rh.heji.ui.report

import android.graphics.Color
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.lxj.xpopup.XPopup
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.query.Income
import com.rh.heji.data.db.query.IncomeTimeSurplus
import com.rh.heji.databinding.FragmentReportBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.report.pop.BottomListPop
import com.rh.heji.utlis.ColorUtils
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.DividerItemDecorator
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors


/**
 * 报告统计页面
 */
class ReportFragment : BaseFragment() {
    //private val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    private val reportViewModel: ReportViewModel by lazy { getViewModel(ReportViewModel::class.java) }
    private val categoryTotalAdapter: CategoryTotalAdapter = CategoryTotalAdapter(mutableListOf())
    private val monthYearBillsAdapter: MonthYearBillAdapter = MonthYearBillAdapter(mutableListOf())

    lateinit var binding: FragmentReportBinding

    val colors = ColorUtils.groupColors()

    override fun onStart() {
        super.onStart()
        reportViewModel.yearMonth = mainActivity.mainViewModel.globalYearMonth
    }

    override fun layoutId(): Int {
        return R.layout.fragment_report
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "统计"
        showYearMonthTitle(
            selected = { year, month ->

                if (month == 0) {//全年
                    reportViewModel.allYear = year
                } else {//单月
                    reportViewModel.yearMonth = YearMonth(year, month)
                }

            },
            year = reportViewModel.yearMonth.year,
            month = reportViewModel.yearMonth.month,
            showAllYear = true
        )
    }

    /**
     * 收入/支出 预览 Observer
     */
    private val incomeExpenditureObserver: (t: Income) -> Unit = {

        it?.let { money ->
            if (money.income == null) money.income = MoneyConverters.ZERO_00()
            if (money.expenditure == null) money.expenditure = MoneyConverters.ZERO_00()
            binding.tvIncomeValue.text = money.income.toString()
            binding.tvExpenditureValue.text = money.expenditure.toString()
            val jieYu = money.income!!.minus(money.expenditure!!)//结余
            binding.tvJieYuValue.text = jieYu.toString()
            val dayCount = MyTimeUtils.lastDayOfMonth(
                reportViewModel.yearMonth.year,
                reportViewModel.yearMonth.month
            )
                .split("-")[2].toInt()//月份天数
            binding.tvDayAVGValue.text =
                jieYu.divide(BigDecimal(dayCount), 2, BigDecimal.ROUND_DOWN).toPlainString()

            showEmptyView()

            //----列表标题年/月平均值
            var avg = "0.00"
            avg = if (reportViewModel.isAllYear) {
                var month12 = BigDecimal(12)
                "月均支出：${
                    money.expenditure!!.divide(
                        month12,
                        2,
                        BigDecimal.ROUND_DOWN
                    )
                }  收入：${money.expenditure!!.div(month12)}"
            } else {
                val monthDayCount = BigDecimal(
                    MyTimeUtils.getMonthLastDay(
                        reportViewModel.yearMonth.year,
                        reportViewModel.yearMonth.month
                    )
                )
                "日均支出：${money.expenditure!!.divide(monthDayCount, 2, BigDecimal.ROUND_DOWN)}  收入：${
                    money.income!!.div(
                        monthDayCount
                    )
                }"
            }
            binding.tvYearMonthAVG.text = SpannableString.valueOf(avg)
        }

    }

    private fun showEmptyView() {
        if (binding.tvDayAVGValue.text.equals("0.00")) {
            binding.emptyStub.visibility = View.VISIBLE
            binding.nestedSccrollView.visibility = View.GONE
        } else {
            binding.emptyStub.visibility = View.GONE
            binding.nestedSccrollView.visibility = View.VISIBLE
        }
    }

    /**
     *  view start
     */
    override fun initView(rootView: View) {
        binding = FragmentReportBinding.bind(rootView)
        incomeExpenditureInfo()
        lineChart()
        reportViewModel.everyNodeIncomeExpenditure.observe(this, {
            setLineChartNodes(it)
        })

        initPieChartCategory()
        updateMonthYearBillListView()
        AppCache.getInstance().appViewModule.dbObservable.observe(this, {
            if (it.entity is Bill) {
                reportViewModel.refreshData(BillType.EXPENDITURE.type())
            }
        })
        binding.emptyStub.setOnInflateListener { stub, inflated ->

        }
    }

    private fun incomeExpenditureInfo() {
        reportViewModel.incomeExpenditure.observe(this, incomeExpenditureObserver)
    }

    /**
     * init line chart
     */
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
                val spannableString = SpannableString("${e.x.toInt()}日\n${e.data}:${e.y}")
                markerContext.text = spannableString
                super.refreshContent(e, highlight)
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

    /**
     * 设置 折线图节点数据
     */
    private fun setLineChartNodes(bills: List<Bill>) {
        val xAxisInConsume = binding.lineChart.xAxis
        val dayCount = MyTimeUtils.getMonthLastDay(
            reportViewModel.yearMonth.year,
            reportViewModel.yearMonth.month
        )
        val list = mutableListOf<Entry>()
        val dayMap = mutableMapOf<Int, Entry>()
        for (day in 0..dayCount) {
            dayMap.replace(day, Entry(0f, 0f))
        }
        list.clear()
        val entries = bills.stream().map {
            val day =
                DateConverters.date2Str(it.billTime)!!.split(" ")[0].split("-")[2].toFloat()
            val type = if (it.type == -1) "支出" else "收入"
            dayMap.replace(day.toInt(), Entry(day, it.money.toFloat(), type))
            return@map Entry(day, it.money.toFloat(), type)
        }.collect(Collectors.toList())

        val set1 = LineDataSet(entries, "收入")

        val data = LineData(set1)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }

    /**
     * 初始化 饼状图统计
     */
    private fun initPieChartCategory() {
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
        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        //chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
        chart.setUsePercentValues(true)
        chart.setDrawEntryLabels(true)
        reportViewModel.categoryProportion
            .observe(this, { list ->
                val entries = ArrayList<PieEntry>()
                list.forEach {
                    entries.add(it)
                }
                setCategoryData(entries)
                updateCategoryListView(entries)
            })
        chart.invalidate()
    }


    private fun setCategoryData(entries: ArrayList<PieEntry>) {


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

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);

        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pieChartCategory))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight)
        binding.pieChartCategory.data = data

        //设置描述的位置
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueLinePart1Length = 0.4f;//设置描述连接线长度
        dataSet.valueLinePart2Length = 0.4f;//设置数据连接线长度
        dataSet.isUsingSliceColorAsValueLineColor = true
        dataSet.setValueTextColors(colors)
        // undo all highlights

        // undo all highlights
        binding.pieChartCategory.highlightValues(null)

        binding.pieChartCategory.invalidate()
    }

    /**
     * 分类列表
     */
    private fun updateCategoryListView(entries: ArrayList<PieEntry>) {
        binding.recyclerCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.adapter = categoryTotalAdapter
        categoryTotalAdapter.setNewInstance(entries)
        categoryTotalAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            val categoryItem: PieEntry = adapter.getItem(position) as PieEntry
            val bills =   AppDatabase.getInstance().billDao().findByCategoryAndMonth(
                categoryItem.label,
                reportViewModel.yearMonth.toString(),
                BillType.EXPENDITURE.type()
            )
            val bottomListPop = BottomListPop(activity = mainActivity, data = bills)
            bottomListPop.titleView.text = categoryItem.label + "(${bills.size}条)"
            XPopup.Builder(requireContext())
                .maxHeight(rootView.height - toolBar.height)//与最大高度与toolbar对齐
                .asCustom(bottomListPop)
                .show()
        })
    }

    /**
     * 年|月 账单报表
     */
    private fun updateMonthYearBillListView() {

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        binding.recyclerBaobiao.layoutManager = linearLayoutManager
        binding.recyclerBaobiao.adapter = monthYearBillsAdapter
        binding.recyclerBaobiao.setHasFixedSize(true)
        binding.recyclerBaobiao.isNestedScrollingEnabled = false
        binding.recyclerBaobiao.addItemDecoration(
            DividerItemDecorator(
                resources.getDrawable(
                    R.drawable.inset_recyclerview_divider,
                    mainActivity.theme
                )
            )
        )
        reportViewModel.reportBillsList.observe(this, {
            monthYearBillsAdapter.setNewInstance(it)
        })
        monthYearBillsAdapter.setOnItemClickListener { adapter, view, position ->
            val itemEntity: IncomeTimeSurplus = adapter.getItem(position) as IncomeTimeSurplus
            val yearMonthDay = "${reportViewModel.yearMonth.year}-${itemEntity.time}"
            val bills =   AppDatabase.getInstance().billDao().findByDay(yearMonthDay)
            val bottomListPop = BottomListPop(activity = mainActivity, data = bills)
            bottomListPop.titleView.text = "$yearMonthDay (${bills.size}条)"
            XPopup.Builder(requireContext())
                .maxHeight(rootView.height - toolBar.height)//与最大高度与toolbar对齐
                .asCustom(bottomListPop)
                .show()
        }
        billTotalListLayout()
    }

    private fun billTotalListLayout() {
        val year = reportViewModel.isAllYear
        if (year) {
            binding.layoutTotalList.tvDate.text = "月份"
        }
        val textColor = resources.getColor(
            R.color.textRemark,
            mainActivity.theme
        )
        binding.layoutTotalList.tvSurplus.setTextColor(textColor)
        binding.layoutTotalList.tvIncome.setTextColor(textColor)
        binding.layoutTotalList.tvExpenditure.setTextColor(textColor)
        binding.layoutTotalList.tvDate.setTextColor(textColor)

    }

}