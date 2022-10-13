package com.rh.heji.ui.list

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.android.material.appbar.AppBarLayout
import com.lxj.xpopup.XPopup
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
import com.rh.heji.ui.bill.create.ArgAddBill
import com.rh.heji.ui.bill.create.CreateBillFragmentArgs
import com.rh.heji.ui.bill.popup.PopupBillInfo
import com.rh.heji.uiState
import com.rh.heji.utlis.ClickUtils
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.CardDecoration
import java.math.BigDecimal
import java.util.*


class BillListFragment : BaseFragment() {

    private val binding: FragmentBillsHomeBinding by lazy {
        FragmentBillsHomeBinding.inflate(layoutInflater)
    }
    private lateinit var subTotalLayoutBinding: LayoutBillsTopBinding
    private lateinit var stubTotalView: ViewStub

    private val homeViewModel: BillListViewModel by lazy { ViewModelProvider(mainActivity)[BillListViewModel::class.java] }
    private lateinit var adapter: NodeBillsAdapter
     override fun initView(rootView: View) {
        stubTotalView = rootView.findViewById(R.id.total)
        adapter = NodeBillsAdapter()
        rootView.post {
            initBillsAdapter()
            binding.fab.setOnClickListener {
                val bill = Bill(billTime = Date())
                val bundle = CreateBillFragmentArgs.Builder(
                    ArgAddBill(false, bill)
                ).build().toBundle()
                bundle.putBoolean("isModify", true)
                Navigation.findNavController(rootView).navigate(
                    R.id.nav_bill_add, bundle
                )
            }

            toolBar.post {
                swipeRefreshLayout(binding.refreshLayout) {
                    notifyData(
                        homeViewModel.yearMonth().year,
                        homeViewModel.yearMonth().month
                    )
                }
                binding.materialupAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    val isFullyShow = verticalOffset >= 0
                    binding.refreshLayout.isEnabled = isFullyShow
                })
            }
        }
        stubTotalView.setOnInflateListener { stub, inflated ->   //提前设置避免多次设置
            subTotalLayoutBinding = LayoutBillsTopBinding.bind(inflated)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiState(homeViewModel) {
            when (it) {
                is BillListUiState.Bills -> {
                    if (it.nodeList.isNullOrEmpty() || it.nodeList.size <= 0) {
                        adapter.setDiffNewData(mutableListOf())//设置DiffCallback使用setDiffNewData避免setList
                        binding.homeRecycler.minimumHeight = getRootViewHeight()//占满一屏
                        stubTotalView.visibility = View.GONE
                        adapter.setEmptyView(R.layout.layout_empty)
                    } else {
                        stubTotalView.visibility = View.VISIBLE
                        adapter.setDiffNewData(it.nodeList)
                    }
                    //adapter.loadMoreModule.loadMoreEnd()//单月不分页，直接显示没有跟多
                    hideRefreshing(binding.refreshLayout)
                }
                is BillListUiState.Summary -> {
                    totalIncomeExpense(it.income)
                }
                is BillListUiState.Error -> {
                    ToastUtils.showLong(it.t.message)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        notifyData( homeViewModel.yearMonth().year,
            homeViewModel.yearMonth().month)
    }
    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = ""
        toolBar.post {
            showYearMonthTitle(
                { year, month ->
                    notifyData(year, month)
                    mainActivity.mainViewModel.globalYearMonth = YearMonth(year, month)
                },
                homeViewModel.yearMonth().year, //默认为当前时间,
                homeViewModel.yearMonth().month//默认为当前月份
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

    override fun layout(): View =binding.root
    /**
     * 汇总收支
     *
     * @param year  年
     * @param month 月
     */
    private fun totalIncomeExpense(incomeExpense: Income) {
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
            ClickUtils.debouncing {
                if (adapter.getItem(position) is DayIncomeNode) { //日视图
                    val dayIncomeNode = adapter.getItem(position) as DayIncomeNode
                    val dayIncome = dayIncomeNode.dayIncome
                    val calendar = Calendar.getInstance()
                    calendar[dayIncome.year, dayIncome.month - 1] = dayIncome.monthDay

                    val args =
                        CreateBillFragmentArgs.Builder(
                            ArgAddBill(
                                false,
                                Bill(billTime = calendar.time)
                            )
                        )
                            .build() //选择的日期
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

        notifyData(homeViewModel.yearMonth().year, homeViewModel.yearMonth().month)
    }


    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private fun showBillItemPop(billTab: Bill) {
        val popupView = PopupBillInfo(bill = billTab, activity = mainActivity, delete = {
            notifyData(homeViewModel.yearMonth().year, homeViewModel.yearMonth().month)
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
    private fun notifyData(year: Int, month: Int) {
        val yearMonth = YearMonth(year, month)
        homeViewModel.doAction(BillListAction.MonthBill(yearMonth))
        homeViewModel.doAction(BillListAction.Summary(yearMonth))
    }
}
