package com.rh.heji.ui.report

import android.text.SpannableString
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.github.mikephil.charting.data.*
import com.lxj.xpopup.XPopup
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.Bill
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
    override fun initView(rootView: View) {
        binding = FragmentReportBinding.bind(rootView)
        incomeExpenditureInfo()
        lineChartStyle(binding.lineChart)
        pieChartStyle(binding.pieChartCategory)
        initCategoryListView()
        initTotalTitleView()
        initTotalListView()
        reportViewModel.everyNodeIncomeExpenditure.observe(this, {
            setLineChartNodes(reportViewModel.yearMonth,it)
        })
        reportViewModel.categoryProportion
            .observe(this, { categoryDataList ->
                setPieChartData(categoryDataList)
                categoryTotalAdapter.setList(categoryDataList)
            })

        reportViewModel.reportBillsList.observe(this, {
            monthYearBillsAdapter.setList(it)
        })
        AppCache.getInstance().appViewModule.dbObservable.observe(this, {
            if (it.entity is Bill) {
                reportViewModel.refreshData(BillType.EXPENDITURE.type())
            }
        })
        binding.emptyStub.setOnInflateListener { stub, inflated ->

        }
    }

    /**
     * 收支总览
     */
    private fun incomeExpenditureInfo() {
        reportViewModel.incomeExpenditure.observe(this,
        {

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
                var avg = if (reportViewModel.isAllYear) {
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

        })
    }

    /**
     * 分类报表 列表视图
     */
    private fun initCategoryListView() {
        binding.recyclerCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategory.adapter = categoryTotalAdapter
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
     * 年|月 报表视图
     */
    private fun initTotalListView() {
        //basic
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
        //listener
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

    }

    /**
     * 报表标题【日期】-【收入】-【指出】-【结余】
     */
    private fun initTotalTitleView() {
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

    /**
     * 没有数据时显示空试图
     */
    private fun showEmptyView() {
        if (binding.tvDayAVGValue.text.equals("0.00")) {
            binding.emptyStub.visibility = View.VISIBLE
            binding.nestedSccrollView.visibility = View.GONE
        } else {
            binding.emptyStub.visibility = View.GONE
            binding.nestedSccrollView.visibility = View.VISIBLE
        }
    }

}