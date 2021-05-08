package com.rh.heji.ui.report

import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.rh.heji.R
import com.rh.heji.data.db.query.Income
import com.rh.heji.databinding.FragmentReportBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.home.BillsHomeViewModel
import com.rh.heji.utlis.MyTimeUtils
import java.math.BigDecimal
import java.util.*


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
        binding.lineChart.setTouchEnabled(false)
        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        //xAxis.axisMinimum = 1f
        xAxis.textSize = 12f
        xAxis.axisLineColor = Color.parseColor("#93A8B1")
        xAxis.textColor = Color.parseColor("#566974")
        xAxis.mLabelWidth
        xAxis.granularity = 1f
        xAxis.valueFormatter = DateFormater()
        xAxis.labelCount = 11//强制显示X
        xAxis.labelRotationAngle = 30f
        binding.lineChart.axisRight.isEnabled = false
    }

    private val lists: MutableList<MutableList<Entry>>? = null
    private val times: List<String>? = null

    /**
     * 设置
     */
    private fun setInConsume() {
        val xAxisInConsume = binding.lineChart.xAxis
        val list = mutableListOf(
                Entry(1f, 134f),
                Entry(2f, 212f),
                Entry(3f, 34f),
                Entry(4f, 4567f),
                Entry(5f, 5123f),
                Entry(6f, 64f),
                Entry(7f, 756f),
                Entry(8f, 8f),
                Entry(9f, 97f),
                Entry(10f, 1860f),
                Entry(11f, 11211f),
                Entry(12f, 11211f))

        var set1 = LineDataSet(list, "收入")

        val data = LineData(set1)
        binding.lineChart.data = data
        binding.lineChart.invalidate()
    }

    fun initPieChartCategory() {
        val chart = binding.pieChartCategory
        chart.setUsePercentValues(true)
        chart.getDescription().setEnabled(false)
        chart.setExtraOffsets(20f, 20f, 20f, 20f)

        chart.setDragDecelerationFrictionCoef(0.95f)

        //chart.setCenterTextTypeface(tfLight)
        //chart.setCenterText(generateCenterSpannableText())

        chart.setDrawHoleEnabled(true)
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)
        chart.setHoleRadius(30f)
        chart.setTransparentCircleRadius(35f)

        chart.setDrawCenterText(true)

        chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true)
        chart.setHighlightPerTapEnabled(true)

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
            }

            override fun onNothingSelected() {
            }
        })


        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        //chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
        setCategoryData("test",0f,4,5f)
    }
    protected val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    )

    protected val parties = arrayOf(
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    )
    fun setCategoryData(category: String, pa: Float, count: Int=4, range: Float=8f) {
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until count) {
            entries.add(PieEntry((Math.random() * range + range / 5).toFloat() ,
                    parties[i % parties.size]))
        }

        val dataSet = PieDataSet(entries, "类别")

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
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight)
        binding.pieChartCategory.setData(data)

        //设置描述的位置
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.5f);//设置描述连接线长度
        dataSet.valueLineColor =mainActivity.getColor(R.color.colorPrimary)
        dataSet.setValueTextColors(colors)
        //设置数据的位置
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart2Length(0.5f);//设置数据连接线长度
        // undo all highlights

        // undo all highlights
        binding.pieChartCategory.highlightValues(null)

        binding.pieChartCategory.invalidate()
    }
}