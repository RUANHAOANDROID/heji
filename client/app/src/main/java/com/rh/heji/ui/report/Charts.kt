package com.rh.heji.ui.report

import android.graphics.Color
import android.text.SpannableString
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.rh.heji.R
import com.rh.heji.currentYearMonth
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors


/**
 * 折线图样式设置
 */
fun ReportFragment.lineChartStyle(lineChart: LineChart) {
    lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry, h: Highlight) {
        }

        override fun onNothingSelected() {
        }
    });
    lineChart.setDrawGridBackground(false)
    lineChart.setTouchEnabled(true)
    var markerView: MarkerView = object : MarkerView(mainActivity, R.layout.marker_linechart) {
        val markerContext = findViewById<TextView>(R.id.tvContext)
        override fun refreshContent(e: Entry, highlight: Highlight?) {
            var sourceString = "${e.x.toInt()}日\n${e.data}:${e.y}"
            if (e.y == 0f)
                sourceString = "${e.x.toInt()}日\n无记录"
            val spannableString = SpannableString(sourceString)
            markerContext.text = spannableString
            super.refreshContent(e, highlight)
        }
    }
    markerView.chartView = lineChart
    lineChart.marker = markerView

    val xAxis: XAxis = lineChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
    xAxis.setDrawAxisLine(false)
    xAxis.setDrawGridLines(false)
    //xAxis.axisMinimum = 1f
    xAxis.textSize = 16f
//    xAxis.axisLineColor = Color.parseColor("#93A8B1")
//    xAxis.textColor = Color.parseColor("#566974")
    xAxis.mLabelWidth
    xAxis.granularity = 1f
    //xAxis.valueFormatter = LargeValueFormatter()
    //xAxis.labelCount = 11//强制显示X 不设置则缩
    xAxis.labelRotationAngle = 45f
    lineChart.axisRight.isEnabled = false
    lineChart.extraLeftOffset=6f
    lineChart.axisLeft.valueFormatter = LargeValueFormatter()
    lineChart.xAxis.valueFormatter = IndexAxisValueFormatter()
}

private fun lineChartConvertAdapter(bills: List<Bill>, yearMonth: YearMonth): LineDataSet {
    val map = mutableMapOf<String, Entry>()
    if (yearMonth.isYear()) {
        for (day in 1..12) {
            val x = if (day < 10) "0$day" else day.toString()
            val entry = Entry(x.toFloat(), 0f)
            map[x] = entry
        }
        bills.stream().map {
            val month = DateConverters.date2Str(it.billTime)!!.split(" ")[0].split("-")[1]
            map.replace(
                month,
                Entry(month.toFloat(), it.money.toFloat(), BillType.transform(it.type).text())
            )
            return@map Entry(
                month.toFloat(),
                it.money.toFloat(),
                BillType.transform(it.type).text()
            )
        }.collect(Collectors.toList())
        val entries = map.values.toMutableList()
        return LineDataSet(entries, BillType.transform(bills[0].type).text())
    } else {
        var dayCount = MyTimeUtils.getMonthLastDay(
            yearMonth.year,
            yearMonth.month
        )

        if (currentYearMonth == yearMonth) {
            dayCount = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        }
        for (day in 1..dayCount) {
            val x = if (day < 10) "0$day" else day.toString()
            val entry = Entry(x.toFloat(), 0f)
            map[x] = entry
        }
        bills.stream().map {
            val day = DateConverters.date2Str(it.billTime)!!.split(" ")[0].split("-")[2]
            map.replace(
                day,
                Entry(day.toFloat(), it.money.toFloat(), BillType.transform(it.type).text())
            )
            return@map Entry(day.toFloat(), it.money.toFloat(), BillType.transform(it.type).text())
        }.collect(Collectors.toList())

        val entries = map.values.toMutableList()
        return LineDataSet(entries, BillType.transform(bills[0].type).text())
    }
}

fun ReportFragment.setIncomeLineChartNodes(yearMonth: YearMonth, bills: List<Bill>) {
    lineChartConvertAdapter(bills, yearMonth).apply {
        color=resources.getColor(R.color.income)
        mode=LineDataSet.Mode.LINEAR
        circleRadius=1f
        circleHoleRadius=0f
        circleColors= this.values.stream().map {
            return@map  if (it.y>0) resources.getColor(R.color.income)
            else resources.getColor(R.color.transparent) }.collect(Collectors.toList())
        valueTextSize=12f
        setValueTextColors(circleColors)
        setDrawFilled(true)
        fillDrawable = resources.getDrawable(R.drawable.shape_gradient_income, mainActivity.theme)
        binding.lineChart.data = LineData(this)
        binding.lineChart.invalidate()
    }
}

fun ReportFragment.setExpenditureLineChartNodes(yearMonth: YearMonth, bills: List<Bill>) {
    lineChartConvertAdapter(bills, yearMonth).apply {
        color=resources.getColor(R.color.expenditure)
        mode=LineDataSet.Mode.LINEAR
        circleRadius=1f
        circleHoleRadius=0f
        circleColors= this.values.stream().map {
            return@map  if (it.y>0) resources.getColor(R.color.expenditure)
            else resources.getColor(R.color.transparent) }.collect(Collectors.toList())
        valueTextSize=12f
        setValueTextColors(circleColors)
        setDrawFilled(true)
        fillDrawable = resources.getDrawable(R.drawable.shape_gradient_expenditure, mainActivity.theme)
        binding.lineChart.data = LineData(this)
        binding.lineChart.invalidate()
    }
}

fun ReportFragment.setIELineChartNodes(
    yearMonth: YearMonth,
    expenditures: List<Bill>,
    incomes: List<Bill>
) {
	val expenditureDataSet=lineChartConvertAdapter(expenditures, yearMonth).apply {

        color=resources.getColor(R.color.expenditure)
        mode=LineDataSet.Mode.LINEAR
        circleRadius=1f
        circleHoleRadius=0f
        circleColors= this.values.stream().map {
            return@map  if (it.y>0) resources.getColor(R.color.expenditure)
            else resources.getColor(R.color.transparent) }.collect(Collectors.toList())
        valueTextSize=12f
        setValueTextColors(circleColors)
        setDrawFilled(true)
        fillDrawable = resources.getDrawable(R.drawable.shape_gradient_expenditure, mainActivity.theme)
        //fillColor =resources.getColor(R.color.expenditure)
    }
	val incomeDataSet=lineChartConvertAdapter(incomes, yearMonth).apply {
        color=resources.getColor(R.color.income)
        mode=LineDataSet.Mode.LINEAR
        circleRadius=1f
        circleHoleRadius=0f
        circleColors= this.values.stream().map {
            return@map  if (it.y>0) resources.getColor(R.color.income)
            else resources.getColor(R.color.transparent) }.collect(Collectors.toList())
        valueTextSize=12f
        setValueTextColors(circleColors)
        setDrawFilled(true)
        fillDrawable = resources.getDrawable(R.drawable.shape_gradient_income, mainActivity.theme)
        //fillColor =resources.getColor(R.color.income)
    }
    binding.lineChart.data = LineData(expenditureDataSet,incomeDataSet )
    binding.lineChart.invalidate()
}

/**
 * 饼图样式
 */
fun ReportFragment.pieChartStyle(pieChart: PieChart) {

    pieChart.setUsePercentValues(true)
    pieChart.description.isEnabled = false
    pieChart.setExtraOffsets(5f, 5f, 5f, 5f)

    pieChart.dragDecelerationFrictionCoef = 0.95f

    //chart.setCenterTextTypeface(tfLight)
    //chart.setCenterText(generateCenterSpannableText())

    pieChart.isDrawHoleEnabled = true
    pieChart.setHoleColor(Color.WHITE)

    pieChart.setTransparentCircleColor(Color.WHITE)
    pieChart.setTransparentCircleAlpha(110)
    pieChart.holeRadius = 50f
    pieChart.transparentCircleRadius = 55f
    pieChart.centerText = "收/支比例"
    pieChart.setDrawCenterText(true)

    pieChart.rotationAngle = 0f
    // enable rotation of the chart by touch
    // enable rotation of the chart by touch
    pieChart.isRotationEnabled = true
    pieChart.isHighlightPerTapEnabled = true

    // chart.setUnit(" €");
    // chart.setDrawUnitsInChart(true);

    // add a selection listener

    // chart.setUnit(" €");
    // chart.setDrawUnitsInChart(true);

    // add a selection listener
    pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            val pieEntry = e as PieEntry
            pieChart.centerText = SpannableString("${pieEntry.label}\n${pieEntry.data}")

        }

        override fun onNothingSelected() {
        }
    })

    pieChart.animateY(1400, Easing.EaseInOutQuad)
    // entry label styling
    pieChart.setEntryLabelColor(Color.WHITE)
    //chart.setEntryLabelTypeface(tfRegular)
    pieChart.setEntryLabelTextSize(12f)
    pieChart.setUsePercentValues(true)
    pieChart.setDrawEntryLabels(true)
    pieChart.invalidate()
}

/**
 * 饼图数据
 */
fun ReportFragment.setPieChartData(entries: MutableList<PieEntry>) {

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
