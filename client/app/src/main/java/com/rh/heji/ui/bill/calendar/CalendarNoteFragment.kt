package com.rh.heji.ui.bill.calendar

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.rh.heji.R
import com.rh.heji.data.db.Bill
import com.rh.heji.databinding.FragmentCalendarNoteBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.create.ArgAddBill
import com.rh.heji.ui.bill.create.CreateBillFragmentArgs
import com.rh.heji.ui.popup.PopupBillInfo
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.CardDecoration

class CalendarNoteFragment : BaseFragment() {
    val binding: FragmentCalendarNoteBinding by lazy {
        FragmentCalendarNoteBinding.inflate(layoutInflater)
    }
    private val popupView by lazy {
        PopupBillInfo.create(
            activity = mainActivity,
            delete = { notifyCalendar() },
            update = {})
    }
    private val viewModel by lazy { ViewModelProvider(this).get(CalendarNoteViewModule::class.java) }
    var adapter: NodeBillsAdapter? = null


    override fun layout(): View {
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.selectYearMonth = mainActivity.viewModel.globalYearMonth
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        //toolBar.title = "日历记账"
        showBlack()
        viewModel.selectYearMonth.apply {
            showYearMonthTitle(
                year = this.year, month = this.month,
                onTabSelected = { year, month ->
                    binding.calendarView.scrollToCalendar(year, month, 1)
                    this.year = year
                    this.month = month
                },
            )
        }
    }

    override fun initView(rootView: View) {
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
            val args =
                CreateBillFragmentArgs.Builder(
                    ArgAddBill(
                        isModify = false,
                        bill = Bill(billTime = calendar.time)
                    )
                )
                    .build()//选择的日期
            Navigation.findNavController(view).navigate(R.id.nav_bill_add, args.toBundle())
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
                popupView.show(billNode.bill)
            }
        }
    }

    private fun initCalendarView() {
        viewModel.selectYearMonth.apply {
            binding.calendarView.scrollToCalendar(year, month, 1)
        }
        binding.calendarView.setOnMonthChangeListener { year, month -> //月份滑动事件
            viewModel.selectYearMonth = YearMonth(year, month)
            centerTitle.text = "$year.$month"
            notifyCalendar()
            fabShow()
            notifyBillsList()
        }
        binding.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {
                LogUtils.d(calendar.toString())
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
            binding.calendarView.clearSchemeDate()
        } else {
            binding.calendarView.setSchemeDate(it)//更新日历视图
        }

        notifyBillsList()
    }

    private fun fabShow() {
        val currentYearMonth = mainActivity.viewModel.currentYearMonth
        if (viewModel.selectYearMonth == currentYearMonth)
            binding.todayFab.hide()
        else
            binding.todayFab.show()

    }

    private fun notifyBillsList() {
        binding.calendarView.post {
            viewModel.todayBills(binding.calendarView.selectedCalendar)//刷新列表
        }

    }

    private fun notifyCalendar() {
        viewModel.updateYearMonth(viewModel.selectYearMonth.year, viewModel.selectYearMonth.month)
    }
}
