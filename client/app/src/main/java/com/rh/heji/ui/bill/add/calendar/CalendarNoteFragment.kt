package com.rh.heji.ui.bill.add.calendar

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.lxj.xpopup.XPopup
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.databinding.FragmentCalendarNoteBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.ui.bill.iteminfo.BillInfoPop
import com.rh.heji.ui.bill.iteminfo.BillPopClickListenerImpl
import com.rh.heji.widget.CardDecoration

class CalendarNoteFragment : BaseFragment() {
    lateinit var binding: FragmentCalendarNoteBinding
    private val viewModel by lazy { getViewModel(CalendarNoteViewModule::class.java) }
    var adapter: NodeBillsAdapter? = null


    override fun layoutId(): Int {
        return R.layout.fragment_calendar_note
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        //toolBar.title = "日历记账"
        showBlack()
        viewModel.year=mainActivity.mainViewModel.globalYearMonth.year
        viewModel.month=mainActivity.mainViewModel.globalYearMonth.month
        showYearMonthTitle(selected = { year, month ->
            binding.calendarView.scrollToCalendar(year, month, 1)
            viewModel.year = year
            viewModel.month = month
        },year = viewModel.year,month = viewModel.month)

    }

    override fun initView(rootView: View) {
        binding = FragmentCalendarNoteBinding.bind(rootView)

        initFab(rootView)
        initCalendarView()
        initAdapter()
        notifyCalendar()
    }

    private fun initFab(view: View) {
        binding.addFab.setOnClickListener {
            val year = binding.calendarView.selectedCalendar.year
            val month = binding.calendarView.selectedCalendar.month
            val day = binding.calendarView.selectedCalendar.day
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month - 1, day)
            val args = AddBillFragmentArgs.Builder(calendar).build()//选择的日期
            Navigation.findNavController(view).navigate(R.id.nav_income, args.toBundle())
        }
        binding.todayFab.setOnClickListener {
            binding.calendarView.scrollToCurrent()
            binding.todayFab.hide()
        }
        fabShow()
    }

    private val dayBillsObserver = Observer<Collection<BaseNode>> {
        adapter?.setNewInstance(it as MutableList<BaseNode>)
        adapter?.notifyDataSetChanged()
    }

    private fun initAdapter() {
        binding.recycler.layoutManager = LinearLayoutManager(mainActivity)
        adapter = NodeBillsAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(CardDecoration(8))

        viewModel.dayBillsLiveData.observe(this, dayBillsObserver)
        adapter?.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) is DayBillsNode) {
                var billNode = adapter.getItem(position) as DayBillsNode
                showBillItemPop(billNode.bill)
            }
        }
    }

    private fun initCalendarView() {
        binding.calendarView.setOnMonthChangeListener { year, month -> //月份滑动事件
            viewModel.year = year
            viewModel.month = month
            centerTitle.text = "$year.$month"
            notifyCalendar()
            fabShow()
            notifyBillsList()
        }
        binding.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {
                LogUtils.i(calendar.toString())
            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {//选择日期事件
                if (isClick) {
                    notifyBillsList()
                }
            }
        })
        viewModel.calendarLiveData.observe(this, monthObserver)
    }

    private var monthObserver = Observer<Map<String, Calendar>> {
        if (it.isEmpty()) {
            binding.calendarView.clearSchemeDate();
        } else {
            binding.calendarView.setSchemeDate(it)//更新日历视图
        }

        notifyBillsList()
    }

    private fun fabShow() {
        val thisMonth =
            android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.MONTH) + 1;
        if (viewModel.month == thisMonth)
            binding.todayFab.hide()
        else
            binding.todayFab.show()

    }

    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private fun showBillItemPop(bill: Bill) {
        val clickListener = object : BillPopClickListenerImpl() {
            override fun delete(bill: Bill) {
                super.delete(bill)
                notifyCalendar()
            }

            override fun update(bill: Bill) {
                notifyCalendar()
            }
        }
        val popupView = BillInfoPop(mainActivity,popClickListener=clickListener)
        XPopup.Builder(mainActivity) //.maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)//默认wrap更具实际布局
            //.isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            //.hasBlurBg(true)//模糊默认false
            //.hasShadowBg(true)//默认true
            .asCustom(popupView) /*.enableDrag(false)*/
            .show()
        popupView.post {
            popupView.bill = bill //账单信息
            popupView.setBillImages(ArrayList()) //首先把图片重置
            if (bill.imgCount > 0) {
                viewModel.getBillImages(bill.id).observe(this, Observer {
                    it.let {
                        popupView.setBillImages(it)
                    }
                })
            }
        }

        popupView.show()
    }


    private fun notifyBillsList() {
        binding.calendarView.post {
            viewModel.todayBills(binding.calendarView.selectedCalendar)//刷新列表
        }

    }

    private fun notifyCalendar() {
        viewModel.updateYearMonth(viewModel.year, viewModel.month)
    }
}
