package com.rh.heji.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.lxj.xpopup.XPopup
import com.rh.heji.Constants
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.query.Income
import com.rh.heji.databinding.FragmentBillsHomeBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.YearSelectPop
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.ui.bill.iteminfo.BillInfoPop
import com.rh.heji.ui.bill.iteminfo.BillInfoPop.PopClickListener
import com.rh.heji.widget.CardDecoration
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

class BillsHomeFragment : BaseFragment() {
    lateinit var binding: FragmentBillsHomeBinding
    val homeViewModel: BillsHomeViewModel by lazy { getActivityViewModel(BillsHomeViewModel::class.java) }
    lateinit var adapter: NodeBillsAdapter
    private var homeUUID: String? = null

    //最后点击时间
    private var lastClickTime = 0L
    private lateinit var toolBarCenterTitle: TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeUUID = UUID.randomUUID().toString()
        mainActivity.mainViewModel.homeUUID = homeUUID
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(layoutId(), container, false)
        initView(view)
        return view
    }

    public override fun initView(view: View) {
        binding = FragmentBillsHomeBinding.bind(view)
        initBillsAdapter()
        binding.fab.setOnClickListener { v: View? ->
            val calendar = Calendar.getInstance() //当前日期
            Navigation.findNavController(view).navigate(R.id.nav_income, AddBillFragmentArgs.Builder(calendar).build().toBundle())
        }
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        addYearMonthView()
        val toolbar = toolBar
        toolbar.inflateMenu(R.menu.home)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_dehaze_24, mainActivity.theme)
        toolbar.setNavigationOnClickListener { v: View? ->
            //展开侧滑菜单
            mainActivity.openDrawer()
        }
        toolbar.menu.findItem(R.id.item1).setOnMenuItemClickListener { item: MenuItem? ->
            ToastUtils.showLong("aa")
            Navigation.findNavController(view).navigate(R.id.nav_calendar_note)
            false
        }
        toolbar.menu.findItem(R.id.item2).setOnMenuItemClickListener { item: MenuItem? ->
            ToastUtils.showLong("aa")
            Navigation.findNavController(view).navigate(R.id.nav_gallery)
            false
        }
    }

    override fun layoutId(): Int {
        return R.layout.fragment_bills_home
    }

    override fun onResume() {
        super.onResume()
        val thisYear = homeViewModel.year
        val thisMonth = homeViewModel.month
        notifyData(thisYear, thisMonth)
    }

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
                   income =it.toString()
                }
                data.expenditure?.let {
                    expenses = it.toString()
                }

            }
            binding.homeHeadView.tvTotalExpensesValue.text = expenses
            binding.homeHeadView.tvTotalIncomeValue.text = income
            refreshHeadView()
        })
    }

    /**
     * 刷新头部收支
     */
    private fun refreshHeadView() {
        val income = BigDecimal(binding.homeHeadView.tvTotalIncomeValue.text.toString())
        val expenses = BigDecimal(binding.homeHeadView.tvTotalExpensesValue.text.toString())
        val totalRevenue = income.subtract(expenses)
        if (totalRevenue.toLong() > 0) {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(mainActivity.getColor(R.color.income))
        } else {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(mainActivity.getColor(R.color.expenditure))
        }
        binding.homeHeadView.tvTotalRevenueValue.text = totalRevenue.toString()
    }

    /**
     * 初始化账单列表适配器
     */
    private fun initBillsAdapter() {
        adapter = NodeBillsAdapter()
        //binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.homeRecycler.layoutManager = LinearLayoutManager(mainActivity)
        binding.homeRecycler.adapter = adapter
        //binding.homeRecycler.addItemDecoration(new CardViewDecoration(getResources(), 5));
        binding.homeRecycler.addItemDecoration(CardDecoration())
        binding.materialupAppBar.background.alpha = Constants.BACKGROUND_ALPHA
        adapter!!.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View?, position: Int ->
            if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                lastClickTime = System.currentTimeMillis()
                if (adapter.getItem(position) is DayIncomeNode) { //日视图
                    val dayIncomeNode = adapter.getItem(position) as DayIncomeNode
                    val dayIncome = dayIncomeNode.dayIncome
                    val calendar = Calendar.getInstance()
                    calendar[dayIncome.year, dayIncome.month - 1] = dayIncome.monthDay
                    val args = AddBillFragmentArgs.Builder(calendar).build() //选择的日期
                    Navigation.findNavController(view!!).navigate(R.id.nav_income, args.toBundle())
                } else { //日详细列表ITEM
                    val dayBills = adapter.getItem(position) as DayBillsNode
                    val bill = dayBills.bill
                    showBillItemPop(bill)
                }
            }
        }
        binding.nestedSccrollView.setOnScrollChangeListener(View.OnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) { //向下滑动隐藏
                binding.fab.hide()
            } else {
                binding.fab.show()
            }
        })
    }

    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private fun showBillItemPop(billTab: Bill) {
        val popupView = BillInfoPop(context!!)
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
        //homeViewModel.getBills().observe(getMainActivity(), listObserver);
        homeViewModel.billsNodLiveData.observe(this, Observer<List<BaseNode?>> {
            baseNodes: List<BaseNode?>? -> adapter?.setNewInstance(baseNodes as MutableList<BaseNode>?)
        })
        homeViewModel.getBillsData()
        totalIncomeExpense(year, month)
    }

    /**
     * 该Menu属于全局所以在这里控制
     */
    fun addYearMonthView() {
        toolBarCenterTitle = toolBar.findViewById(R.id.toolbar_center_title)
        toolBarCenterTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_baseline_arrow_down_white_32), null)
        toolBarCenterTitle.compoundDrawablePadding = 8
        val thisYear = homeViewModel.year
        val thisMonth = homeViewModel.month
        val yearMonth = "$thisYear.$thisMonth"
        toolBarCenterTitle.text = yearMonth
        toolBarCenterTitle.setOnClickListener(View.OnClickListener { v: View? ->
            XPopup.Builder(mainActivity) //.hasBlurBg(true)//模糊
                    .hasShadowBg(true)
                    .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT) //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(YearSelectPop(mainActivity) { year: Int, month: Int ->
                        toolBarCenterTitle.text = "$year.$month"
                        val fragments = mainActivity.fragments
                        fragments.forEach(Consumer { fragment: Fragment? ->
                            if (fragment is BillsHomeFragment) {
                                fragment.notifyData(year, month)
                            }
                        })
                    }) /*.enableDrag(false)*/
                    .show()
        })
    }

    companion object {
        // 两次点击间隔不能少于1000ms
        private const val FAST_CLICK_DELAY_TIME = 500
    }
}