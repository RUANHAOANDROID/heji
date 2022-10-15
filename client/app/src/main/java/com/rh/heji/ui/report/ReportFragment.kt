package com.rh.heji.ui.report

import android.text.SpannableString
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.data.*
import com.lxj.xpopup.XPopup
import com.rh.heji.AppViewModel
import com.rh.heji.R
import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.DataBus
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.dto.BillTotal
import com.rh.heji.data.db.dto.IncomeTimeSurplus
import com.rh.heji.databinding.FragmentReportBinding
import com.rh.heji.databinding.LayoutEmptyBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.report.pop.BottomListPop
import com.rh.heji.utlis.ColorUtils
import com.rh.heji.utlis.MyTimeUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.DividerItemDecorator
import java.math.BigDecimal
import java.util.*


/**
 * 报告统计页面
 */
class ReportFragment : BaseFragment() {
    //private val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    private val reportViewModel: ReportViewModel by lazy { ViewModelProvider(this)[ReportViewModel::class.java] }
    private val categoryTotalAdapter: CategoryTotalAdapter = CategoryTotalAdapter(mutableListOf())
    private val monthYearBillsAdapter: MonthYearBillAdapter = MonthYearBillAdapter(mutableListOf())
    private lateinit var emptyStubView: ViewStub
    val binding: FragmentReportBinding by lazy { FragmentReportBinding.inflate(layoutInflater) }

    val colors = ColorUtils.groupColors()
    override fun onStart() {
        super.onStart()
        reportViewModel.yearMonth = mainActivity.viewModel.globalYearMonth
    }

    override fun layout() = binding.root

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "统计"
        showYearMonthTitle(
            year = reportViewModel.yearMonth.year,
            month = reportViewModel.yearMonth.month,
            showAllYear = true,
            onTabSelected = { year, month ->
                if (month == 0) {//全年
                    reportViewModel.allYear = year
                } else {//单月
                    reportViewModel.yearMonth = YearMonth(year, month)
                }
            }
        )
    }

    override fun initView(rootView: View) {
        emptyStubView = rootView.findViewById(R.id.emptyStub)

        incomeExpenditureInfo()

        lineChartStyle(binding.lineChart)
        binding.tvTypeExpenditure.setOnClickListener {
            reportViewModel.expenditure()
            lineChartSelectType(BillType.EXPENDITURE)
        }
        binding.tvTypeIncome.setOnClickListener {
            reportViewModel.income()
            lineChartSelectType(BillType.INCOME)
        }
        binding.tvTypeAll.setOnClickListener {
            reportViewModel.incomeAndExpenditure()
            lineChartSelectType(BillType.ALL)
        }

        pieChartStyle(binding.pieChartCategory)

        binding.tvTypeExpenditurePie.setOnClickListener {
            reportViewModel.monthCategoryProportion(BillType.EXPENDITURE)
            pieChartSelectType(BillType.EXPENDITURE)
        }
        binding.tvTypeIncomePie.setOnClickListener {
            reportViewModel.monthCategoryProportion(BillType.INCOME)
            pieChartSelectType(BillType.INCOME)
        }

        initCategoryListView()
        initTotalTitleView()
        initTotalListView()
        reportViewModel.everyNodeIncomeExpenditure.observe(this) {
            it.apply {
                if (key == BillType.EXPENDITURE.type()) {
                    setExpenditureLineChartNodes(
                        reportViewModel.yearMonth,
                        value as MutableList<BillTotal>
                    )
                }
                if (key == BillType.INCOME.type()) {
                    setIncomeLineChartNodes(
                        reportViewModel.yearMonth,
                        value as MutableList<BillTotal>
                    )
                }
                if (key == BillType.ALL.type()) {
                    val arrays = value as ArrayList<MutableList<BillTotal>>
                    setIELineChartNodes(
                        reportViewModel.yearMonth,
                        expenditures = arrays[0],
                        incomes = arrays[1]
                    )
                }
            }
        }
        reportViewModel.categoryProportion
            .observe(this) { categoryDataList ->
                setPieChartData(categoryDataList)
                categoryTotalAdapter.setList(categoryDataList)
            }

        reportViewModel.reportBillsList.observe(this) {
            monthYearBillsAdapter.setList(it)
        }
        DataBus.subscriber(this) {
            if (it.entity is Bill) {
                reportViewModel.refreshData(BillType.EXPENDITURE)
            }
        }
        emptyStubView.setOnInflateListener { stub, inflated ->
            val emptyLayoutBinding = LayoutEmptyBinding.bind(inflated)
            emptyLayoutBinding.tvContext.text = "没有更多账单数据"
            emptyLayoutBinding.tvContext.setOnClickListener {
                ToastUtils.showShort("重试加载失败")
            }
            LogUtils.d("empty view inflated")
        }
    }

    /**
     * 收支总览
     */
    private fun incomeExpenditureInfo() {
        reportViewModel.incomeExpenditure.observe(
            this
        ) {

            it?.let { money ->
                if (money.income == null) money.income = MoneyConverters.ZERO_00()
                if (money.expenditure == null) money.expenditure = MoneyConverters.ZERO_00()
                binding.tvIncomeValue.text = money.income.toString()
                binding.tvExpenditureValue.text = money.expenditure.toString()
                val jieYu = money.income!!.minus(money.expenditure!!)//结余
                binding.tvJieYuValue.text = jieYu.toPlainString()
                val dayCount = MyTimeUtils.lastDayOfMonth(
                    reportViewModel.yearMonth.year,
                    reportViewModel.yearMonth.month
                )
                    .split("-")[2].toInt()//月份天数
                binding.tvDayAVGValue.text =
                    jieYu.divide(BigDecimal(dayCount), 2, BigDecimal.ROUND_DOWN).toPlainString()

                showEmptyView()

                //----列表标题年/月平均值
                var avg = if (reportViewModel.yearMonth.isYear()) {
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
                    "日均支出：${
                        money.expenditure!!.divide(
                            monthDayCount,
                            2,
                            BigDecimal.ROUND_DOWN
                        )
                    }  收入：${
                        money.income!!.div(
                            monthDayCount
                        )
                    }"
                }
                binding.tvYearMonthAVG.text = SpannableString.valueOf(avg)
            }

        }
    }

    /**
     * 分类报表 列表视图
     */
    private fun initCategoryListView() {
        binding.recyclerCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.adapter = categoryTotalAdapter
        categoryTotalAdapter.setOnItemClickListener { adapter, view, position ->

            val categoryItem: PieEntry = adapter.getItem(position) as PieEntry
            val billType: Int = with(categoryItem) {
                val money = data as BigDecimal
                money.signum()//返回 -1 | 0 | 1  与BillType一致
            }
            val bills = App.dataBase.billDao().findByCategoryAndMonth(
                categoryItem.label,
                reportViewModel.yearMonth.yearMonthString(),
                billType
            )
            val bottomListPop = BottomListPop(activity = mainActivity, data = bills).apply {
                title.text = categoryItem.label + "(${bills.size}条)"
            }
            XPopup.Builder(requireContext())
                .maxHeight(rootView.height - toolBar.height)//与最大高度与toolbar对齐
                .asCustom(bottomListPop)
                .show()
        }
    }

    /**
     * 年|月 报表视图
     */
    private fun initTotalListView() {
        //basic
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        binding.recyclerBaobiao.apply {
            layoutManager = linearLayoutManager
            adapter = monthYearBillsAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            addItemDecoration(
                DividerItemDecorator(
                    resources.getDrawable(
                        R.drawable.inset_recyclerview_divider,
                        mainActivity.theme
                    )
                )
            )
        }

        //listener
        monthYearBillsAdapter.setOnItemClickListener { adapter, view, position ->
            val itemEntity: IncomeTimeSurplus = adapter.getItem(position) as IncomeTimeSurplus
            val yearMonthDay = "${reportViewModel.yearMonth.year}-${itemEntity.time}"
            val bills = App.dataBase.billDao().findByDay(yearMonthDay).filter {
                it.images = App.dataBase.imageDao().findImagesId(it.id)//c
                return@filter true
            }.toMutableList()
            val bottomListPop = BottomListPop(activity = mainActivity, data = bills).apply {
                title.text = "$yearMonthDay (${bills.size}条)"
            }
            XPopup.Builder(requireContext())
                .maxHeight(rootView.height - toolBar.height)//与最大高度与toolbar对齐
                .asCustom(bottomListPop)
                .show()
        }

    }

    /**
     * 报表标题【日期】-【收入】-【支出】-【结余】
     */
    private fun initTotalTitleView() {
        val year = reportViewModel.yearMonth.isYear()

        binding.layoutTotalList.apply {
            if (year) {
                tvDate.text = "月份"
            }
            val textColor = resources.getColor(R.color.textRemark, mainActivity.theme)
            tvSurplus.setTextColor(textColor)
            tvIncome.setTextColor(textColor)
            tvExpenditure.setTextColor(textColor)
            tvDate.setTextColor(textColor)
        }
    }

    /**
     * 没有数据时显示空视图
     */
    private fun showEmptyView() {
        if (binding.tvDayAVGValue.text.equals("0.00")) {
            emptyStubView.visibility = View.VISIBLE
            binding.nestedSccrollView.visibility = View.GONE
        } else {
            emptyStubView.visibility = View.GONE
            binding.nestedSccrollView.visibility = View.VISIBLE
        }
    }

    /**
     * 折线图类型
     *
     * @param  type 收入|支出|全部
     */
    private fun lineChartSelectType(type: BillType) {
        binding.tvTypeExpenditure.apply {
            setBackgroundColor(resources.getColor(R.color.transparent, mainActivity.theme))
            setTextColor(resources.getColor(R.color.textRemark, mainActivity.theme))
        }

        binding.tvTypeIncome.apply {
            setBackgroundColor(resources.getColor(R.color.transparent, mainActivity.theme))
            setTextColor(resources.getColor(R.color.textRemark, mainActivity.theme))
        }


        binding.tvTypeAll.apply {
            setBackgroundColor(resources.getColor(R.color.transparent, mainActivity.theme))
            setTextColor(resources.getColor(R.color.textRemark, mainActivity.theme))
        }

        when (type) {
            BillType.EXPENDITURE -> {
                binding.tvTypeExpenditure.apply {
                    background =
                        resources.getDrawable(R.drawable.shape_tag_left_blue, mainActivity.theme)
                    setTextColor(resources.getColor(R.color.white, mainActivity.theme))
                }
            }
            BillType.INCOME -> {
                binding.tvTypeIncome.apply {
                    setBackgroundColor(resources.getColor(R.color.colorPrimary, mainActivity.theme))
                    setTextColor(resources.getColor(R.color.white, mainActivity.theme))
                }
            }
            BillType.ALL -> {
                binding.tvTypeAll.apply {
                    background =
                        resources.getDrawable(R.drawable.shape_tag_right_blue, mainActivity.theme)
                    setTextColor(resources.getColor(R.color.white, mainActivity.theme))
                }
            }
        }
    }

    private fun pieChartSelectType(type: BillType) {
        binding.tvTypeExpenditurePie.apply {
            setBackgroundColor(resources.getColor(R.color.transparent, mainActivity.theme))
            setTextColor(resources.getColor(R.color.textRemark, mainActivity.theme))
        }

        binding.tvTypeIncomePie.apply {
            setBackgroundColor(resources.getColor(R.color.transparent, mainActivity.theme))
            setTextColor(resources.getColor(R.color.textRemark, mainActivity.theme))
        }


        when (type) {
            BillType.EXPENDITURE -> {
                binding.tvTypeExpenditurePie.apply {
                    background =
                        resources.getDrawable(R.drawable.shape_tag_left_blue, mainActivity.theme)
                    setTextColor(resources.getColor(R.color.white, mainActivity.theme))
                }
            }
            BillType.INCOME -> {
                binding.tvTypeIncomePie.apply {
                    background =
                        resources.getDrawable(R.drawable.shape_tag_right_blue, mainActivity.theme)
                    setTextColor(resources.getColor(R.color.white, mainActivity.theme))
                }
            }
            else -> {}
        }
    }
}