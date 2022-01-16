package com.rh.heji.ui.home

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewStub
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.android.material.appbar.AppBarLayout
import com.lxj.xpopup.XPopup
import com.rh.heji.AppViewModel
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.dto.Income
import com.rh.heji.databinding.FragmentBillsHomeBinding
import com.rh.heji.databinding.LayoutBillsTopBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.base.hideRefreshing
import com.rh.heji.ui.base.swipeRefreshLayout
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.ui.bill.iteminfo.PopBillInfo
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.CardDecoration
import java.math.BigDecimal
import java.util.*


class BillsHomeFragment : BaseFragment() {
    companion object {
        // 两次点击间隔不能少于1000ms
        private const val FAST_CLICK_DELAY_TIME = 500
    }

    private lateinit var binding: FragmentBillsHomeBinding
    private lateinit var subTotalLayoutBinding: LayoutBillsTopBinding
    private lateinit var stubTotalView: ViewStub

    val homeViewModel: BillsHomeViewModel by lazy { ViewModelProvider(mainActivity)[BillsHomeViewModel::class.java] }
    private lateinit var adapter: NodeBillsAdapter

    //最后点击时间
    private var lastClickTime = 0L


    public override fun initView(rootView: View) {
        binding = FragmentBillsHomeBinding.bind(rootView)
        stubTotalView = rootView.findViewById(R.id.total)
        adapter = NodeBillsAdapter()
        rootView.post {
            initBillsAdapter()
            binding.fab.setOnClickListener {
                val bill = Bill(billTime = Date())
                Navigation.findNavController(rootView).navigate(
                    R.id.nav_bill_add, AddBillFragmentArgs.Builder(
                        bill
                    ).build().toBundle()
                )
            }

            toolBar.post {
                swipeRefreshLayout(binding.refreshLayout) {
                    notifyData(
                        homeViewModel.selectYearMonth.year,
                        homeViewModel.selectYearMonth.month
                    )
                }
                binding.materialupAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    val isFullyShow = verticalOffset >= 0
                    binding.refreshLayout.isEnabled = isFullyShow
                })
            }
            AppViewModel.get().asyncLiveData.observe(
                this,
                asyncNotifyObserver(
                    homeViewModel.selectYearMonth.year,
                    homeViewModel.selectYearMonth.month
                )
            )
        }
        stubTotalView.setOnInflateListener { stub, inflated ->   //提前设置避免多次设置
            subTotalLayoutBinding = LayoutBillsTopBinding.bind(inflated)
        }
        homeViewModel.monthDataChange().observe(this, billsObserver)
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.post {
            showYearMonthTitle(
                { year, month ->
                    notifyData(year, month)
                    mainActivity.mainViewModel.globalYearMonth = YearMonth(year, month)
                },
                homeViewModel.selectYearMonth.year, //默认为当前时间,
                homeViewModel.selectYearMonth.month//默认为当前月份
            )
            toolBar.navigationIcon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_dehaze_24,
                mainActivity.theme
            )
            toolBar.setNavigationOnClickListener {
                //展开侧滑菜单
                mainActivity.openDrawer()
            }
            binding.imgCalendar.setOnClickListener {
                Navigation.findNavController(rootView).navigate(R.id.nav_calendar_note)
            }
            binding.imgTotal.setOnClickListener {
                Navigation.findNavController(rootView).navigate(R.id.nav_report)
            }
        }

    }

    override fun layoutId(): Int {
        return R.layout.fragment_bills_home
    }

    private fun asyncNotifyObserver(thisYear: Int, thisMonth: Int): Observer<Any> =
        Observer { notifyData(thisYear, thisMonth) }

    /**
     * 汇总收支
     *
     * @param year  年
     * @param month 月
     */
    private fun totalIncomeExpense() {
        homeViewModel.getIncomeExpense().observe(mainActivity, { incomeExpense: Income? ->

            var income = "0"
            var expenses = "0"
            incomeExpense?.let { data ->
                data.income?.let {
                    income = it.toString()
                }
                data.expenditure?.let {
                    expenses = it.toString()
                }

            }

            if ((income == "0" && expenses == "0") || (income == "0.00" && expenses == "0.00")) {
                stubTotalView.visibility = View.GONE
            } else {
                stubTotalView.visibility = View.VISIBLE
                notifyTotalLayout(expenses, income)
            }
        })
    }

    private fun notifyTotalLayout(expenses: String, income: String) {
        if (this::subTotalLayoutBinding.isInitialized) {
            val tvSurplus = subTotalLayoutBinding.tvSurplus
            val tvExpenditure = subTotalLayoutBinding.tvExpenditure
            val tvIncome = subTotalLayoutBinding.tvIncome

            tvExpenditure.text = expenses
            tvIncome.text = income
            val income1 = BigDecimal(tvIncome.text.toString())
            val expenses1 = BigDecimal(tvExpenditure.text.toString())
            val totalRevenue = income1.subtract(expenses1)
            if (totalRevenue.toLong() > 0) {
                tvSurplus.setTextColor(ContextCompat.getColor(tvSurplus.context, R.color.income))
            } else {
                tvSurplus.setTextColor(
                    ContextCompat.getColor(
                        tvSurplus.context,
                        R.color.expenditure
                    )
                )
            }
            tvSurplus.text = totalRevenue.toPlainString()
        }
    }

    /**
     * 初始化账单列表适配器
     */
    private fun initBillsAdapter() {
        adapter.recyclerView = binding.homeRecycler
        binding.homeRecycler.layoutManager = LinearLayoutManager(mainActivity)
        binding.homeRecycler.adapter = adapter
        binding.homeRecycler.addItemDecoration(CardDecoration(8))
        class Diff : ItemCallback<BaseNode>() {
            override fun areItemsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
                return oldItem == newItem
            }
        }
        adapter.setDiffCallback(Diff())
        adapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, position: Int ->
            if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                lastClickTime = System.currentTimeMillis()
                if (adapter.getItem(position) is DayIncomeNode) { //日视图
                    val dayIncomeNode = adapter.getItem(position) as DayIncomeNode
                    val dayIncome = dayIncomeNode.dayIncome
                    val calendar = Calendar.getInstance()
                    calendar[dayIncome.year, dayIncome.month - 1] = dayIncome.monthDay

                    val args =
                        AddBillFragmentArgs.Builder(Bill(billTime = calendar.time)).build() //选择的日期
                    Navigation.findNavController(rootView)
                        .navigate(R.id.nav_bill_add, args.toBundle())
                } else { //日详细列表ITEM
                    val dayBills = adapter.getItem(position) as DayBillsNode
                    val bill = dayBills.bill
                    showBillItemPop(bill)
                }
            }
        }
        binding.homeRecycler.setOnScrollChangeListener { _: View?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) { //向下滑动隐藏
                binding.fab.hide()
            } else {//向上滑动显示
                binding.fab.show()
            }
        }

        notifyData(homeViewModel.selectYearMonth.year, homeViewModel.selectYearMonth.month)
    }


    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private fun showBillItemPop(billTab: Bill) {
        val popupView = PopBillInfo(bill = billTab, activity = mainActivity, delete = {
            notifyData(homeViewModel.selectYearMonth.year, homeViewModel.selectYearMonth.month)
        }, update = {})
        XPopup.Builder(requireContext()) //.maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)//默认wrap更具实际布局
            //.isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            //.hasBlurBg(true)//模糊默认false
            //.hasShadowBg(true)//默认true
            .asCustom(popupView) /*.enableDrag(false)*/
            .show()


    }

    /**
     * 刷新列表数据、收支情况数据
     *
     * @param year  年
     * @param month 月
     */
    fun notifyData(year: Int, month: Int) {
        homeViewModel.selectYearMonth = YearMonth(year, month)
        homeViewModel.refreshMonthData()
        totalIncomeExpense()
    }

    @SuppressLint("InflateParams")
    private val billsObserver = { baseNodes: MutableList<BaseNode> ->
        if (baseNodes.isNullOrEmpty() || baseNodes.size <= 0) {
            adapter.setDiffNewData(mutableListOf())//设置DiffCallback使用setDiffNewData避免setList
            binding.homeRecycler.minimumHeight = getRootViewHeight()//占满一屏
            stubTotalView.visibility = View.GONE
            adapter.setEmptyView(R.layout.layout_empty)
        } else {
            stubTotalView.visibility = View.VISIBLE
            adapter.setDiffNewData(baseNodes)
        }
        //adapter.loadMoreModule.loadMoreEnd()//单月不分页，直接显示没有跟多
        hideRefreshing(binding.refreshLayout)
    }
}
