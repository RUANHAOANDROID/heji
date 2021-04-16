package com.rh.heji.ui.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.lxj.xpopup.XPopup
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.query.Income
import com.rh.heji.databinding.FragmentBillsHomeBinding
import com.rh.heji.databinding.LayoutBillsTopBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.ui.bill.iteminfo.BillInfoPop
import com.rh.heji.ui.bill.iteminfo.BillInfoPop.PopClickListener
import com.rh.heji.widget.CardDecoration
import java.math.BigDecimal
import java.util.*


class BillsHomeFragment : BaseFragment(), ViewStub.OnInflateListener {

    lateinit var binding: FragmentBillsHomeBinding
    lateinit var subTotalLayoutBinding: LayoutBillsTopBinding
    val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    val adapter: NodeBillsAdapter by lazy { NodeBillsAdapter()  }

    //最后点击时间
    private var lastClickTime = 0L


    public override fun initView(rootView: View) {
        binding = FragmentBillsHomeBinding.bind(rootView)
        rootView.post {
            initBillsAdapter()
            binding.fab.setOnClickListener { v: View ->
                val calendar = Calendar.getInstance() //当前日期
                Navigation.findNavController(rootView).navigate(R.id.nav_income, AddBillFragmentArgs.Builder(calendar).build().toBundle())
            }
            initSwipeRefreshLayout()
            AppCache.instance.appViewModule.asyncLiveData.observe(this, asyncNotifyObserver(homeViewModel.year, homeViewModel.month))
        }
        binding.total.setOnInflateListener(this)//提前设置避免多次设置
    }

    private fun initSwipeRefreshLayout() {
        toolBar.post {
            binding.refreshLayout.setProgressViewOffset(true, 0, 180)//设置缩放，起始位置，最终位置
            //设置bar头部折叠监听
            binding.materialupAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val isFullyShow = verticalOffset >= 0
                binding.refreshLayout.isEnabled = isFullyShow
            })
            binding.refreshLayout.setOnRefreshListener {
                binding.refreshLayout.isRefreshing = false
                notifyData(homeViewModel.year, homeViewModel.month)
            }
            //设置刷新提示View颜色（在最后）
            binding.refreshLayout.setColorSchemeResources(R.color.colorPrimary)
        }
        //binding.refreshLayout.setDistanceToTriggerSync(toolBar.height*2)

    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.post {
            showYearMonthTitle({ year, month -> notifyData(year, month) }, homeViewModel.year, homeViewModel.month)
            toolBar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_dehaze_24, mainActivity.theme)
            toolBar.setNavigationOnClickListener { v: View ->
                //展开侧滑菜单
                mainActivity.openDrawer()
            }
            binding.imgCalendar.setOnClickListener { Navigation.findNavController(rootView).navigate(R.id.nav_calendar_note) }
            binding.imgTotal.setOnClickListener { Navigation.findNavController(rootView).navigate(R.id.nav_gallery) }
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
    private fun totalIncomeExpense(year: Int, month: Int) {

        homeViewModel.getIncomeExpense(year, month).observe(mainActivity, Observer { incomeExpense: Income? ->

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

            if (income == "0" && expenses == "0") {
                binding.total.visibility = View.GONE
            } else {
                binding.total.visibility = View.VISIBLE
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
                tvSurplus.setTextColor(mainActivity.getColor(R.color.income))
            } else {
                tvSurplus.setTextColor(mainActivity.getColor(R.color.expenditure))
            }
            tvSurplus.text = totalRevenue.toString()
        }
    }

    /**
     * 初始化账单列表适配器
     */
    private fun initBillsAdapter() {
        adapter.recyclerView = binding.homeRecycler
        //binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.homeRecycler.layoutManager = LinearLayoutManager(mainActivity)
        binding.homeRecycler.adapter = adapter
        binding.homeRecycler.addItemDecoration(CardDecoration())
        //binding.materialupAppBar.background.alpha = Constants.BACKGROUND_ALPHA
        class Diff : ItemCallback<BaseNode>() {
            override fun areItemsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
                if (oldItem is DayIncomeNode && newItem is DayIncomeNode) {
                    return oldItem.dayIncome == newItem.dayIncome
                }
                if (oldItem is DayBillsNode && newItem is DayBillsNode) {
                    val idSame = oldItem.bill.id == newItem.bill.id
                    val moneySame = oldItem.bill.money == newItem.bill.money
                    return idSame && moneySame
                }
                return false
            }

            override fun areContentsTheSame(oldItem: BaseNode, newItem: BaseNode): Boolean {
                if (oldItem is DayBillsNode && newItem is DayBillsNode) {
                    val moneySame = oldItem.bill.money == newItem.bill.money
                    val remarkSame = oldItem.bill.remark == newItem.bill.remark
                    return moneySame && remarkSame
                }
                return false
            }
        }
        adapter.setDiffCallback(Diff())
        adapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
            if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                lastClickTime = System.currentTimeMillis()
                if (adapter.getItem(position) is DayIncomeNode) { //日视图
                    val dayIncomeNode = adapter.getItem(position) as DayIncomeNode
                    val dayIncome = dayIncomeNode.dayIncome
                    val calendar = Calendar.getInstance()
                    calendar[dayIncome.year, dayIncome.month - 1] = dayIncome.monthDay
                    val args = AddBillFragmentArgs.Builder(calendar).build() //选择的日期
                    Navigation.findNavController(rootView).navigate(R.id.nav_income, args.toBundle())
                } else { //日详细列表ITEM
                    val dayBills = adapter.getItem(position) as DayBillsNode
                    val bill = dayBills.bill
                    showBillItemPop(bill)
                }
            }
        }
        binding.homeRecycler.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) { //向下滑动隐藏
                binding.fab.hide()
            } else {//向上滑动显示
                binding.fab.show()
            }
        }

        headView()
        notifyData(homeViewModel.year, homeViewModel.month)
    }

    private fun headView() {


    }

    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private fun showBillItemPop(billTab: Bill) {
        val popupView = BillInfoPop(mainActivity)
        popupView.popClickListener = object : PopClickListener {
            override fun delete(_id: String) {
                notifyData(homeViewModel.year, homeViewModel.month)
            }

            override fun update(_id: String) {}
        }
        XPopup.Builder(context) //.maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)//默认wrap更具实际布局
                //.isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                //.hasBlurBg(true)//模糊默认false
                //.hasShadowBg(true)//默认true
                .asCustom(popupView) /*.enableDrag(false)*/
                .show()
        popupView.post {
            popupView.bill = billTab //账单信息
            popupView.setBillImages(ArrayList()) //首先把图片重置
            if (billTab.imgCount > 0) {
                homeViewModel.getBillImages(billTab.getId()).observe(viewLifecycleOwner, Observer<List<Image>> { images: List<Image> ->
                    popupView.setBillImages(images)
                }
                )
            }
        }
        popupView.show()
    }

    /**
     * 刷新列表数据、收支情况数据
     *
     * @param year  年
     * @param month 月
     */
    fun notifyData(year: Int, month: Int) {
        homeViewModel.year = year
        homeViewModel.month = month

        homeViewModel.billsNodLiveData.observe(this, billsObserver)
        homeViewModel.getBillsData()
        totalIncomeExpense(year, month)
    }

    private val billsObserver = { baseNodes: MutableList<BaseNode> ->
        if (baseNodes.isNullOrEmpty() || baseNodes.size <= 0) {
            val emptyView = layoutInflater.inflate(R.layout.layout_empty, null)
            adapter.setNewInstance(mutableListOf())
            binding.homeRecycler.minimumHeight = ScreenUtils.getScreenHeight() - toolBar.height - navigationBarHeight//占满一屏
            adapter.setEmptyView(emptyView)
            adapter.notifyDataSetChanged()
        } else {
            binding.total.visibility = View.VISIBLE
            adapter.setDiffNewData(baseNodes)
        }
        //adapter.loadMoreModule.loadMoreEnd()//单月不分页，直接显示没有跟多
        binding.refreshLayout.isRefreshing = false
    }

    companion object {
        // 两次点击间隔不能少于1000ms
        private const val FAST_CLICK_DELAY_TIME = 500
    }

    override fun onInflate(stub: ViewStub, inflated: View) {
        subTotalLayoutBinding = LayoutBillsTopBinding.bind(inflated)
    }


}
