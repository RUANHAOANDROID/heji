package com.hao.heji.ui.calendar

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.hao.heji.R
import com.hao.heji.data.db.Bill
import com.hao.heji.databinding.FragmentCalendarNoteBinding
import com.hao.heji.render
import com.hao.heji.today
import com.hao.heji.ui.base.BaseFragment
import com.hao.heji.ui.adapter.DayBillsNode
import com.hao.heji.ui.adapter.NodeBillsAdapter
import com.hao.heji.ui.create.ArgAddBill
import com.hao.heji.ui.create.CreateBillFragmentArgs
import com.hao.heji.ui.popup.PopupBillInfo
import com.hao.heji.utils.YearMonth
import com.hao.heji.widget.CardDecoration

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
    private val viewModel by lazy { ViewModelProvider(this)[CalendarNoteViewModule::class.java] }
    lateinit var adapter: NodeBillsAdapter

    override fun layout(): View {
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.selectYearMonth = mainActivity.viewModel.globalYearMonth
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        super.setUpToolBar()
        showBlack()

        val today = today()
        val selectYearMonth = viewModel.selectYearMonth

        with(selectYearMonth) {
            showYearMonthTitle(
                year = year, month = month,
                onTabSelected = { year, month ->
                    binding.calendarView.scrollToCalendar(year, month, 1)
                    this.year = year
                    this.month = month
                },
            )

            if (year != today.year || month != today.month) {
                binding.calendarView.scrollToCalendar(year, month, today.day)
            } else {
                notifyCalendar()
            }
        }
    }

    override fun initView(rootView: View) {
        initFab(rootView)
        initCalendarView()
        initAdapter()
        render(viewModel) {
            when (it) {
                is CalenderUiState.DayBills -> {
                    adapter.setNewInstance(it.data as MutableList<BaseNode>)
                    adapter.notifyDataSetChanged()
                }

                is CalenderUiState.Calender -> {
                    if (it.data.isEmpty()) {
                        binding.calendarView.clearSchemeDate()
                    } else {
                        binding.calendarView.setSchemeDate(it.data)//更新日历视图
                    }
                    notifyBillsList()
                }

                is CalenderUiState.Images -> popupView.setImages(it.data)
            }
        }
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
                        bill = Bill(time = calendar.time)
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

    private fun initAdapter() {
        binding.recycler.layoutManager = LinearLayoutManager(mainActivity)
        adapter = NodeBillsAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(CardDecoration(8))
        adapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItem(position) is DayBillsNode) {
                var billNode = adapter.getItem(position) as DayBillsNode
                popupView.show(billNode.bill)
                if (billNode.bill.images.isNotEmpty()) {
                    viewModel.doAction(CalenderAction.GetImages(billNode.bill.id))
                }
            }
        }
    }

    private fun initCalendarView() {
        viewModel.selectYearMonth.apply {
            binding.calendarView.scrollToCalendar(year, month, day)
        }
        binding.calendarView.apply {
            setOnMonthChangeListener { year, month -> //月份更改侦听
                viewModel.selectYearMonth = YearMonth(year, month)
                centerTitle.text = "$year.$month"
                notifyCalendar()
                fabShow()
            }
            setOnCalendarSelectListener(object : OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: Calendar) {
                    LogUtils.d(calendar.toString())
                }

                override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {//选择日期事件
                    if (isClick) {
                        notifyBillsList()
                    }
                }
            })
        }
    }

    private fun fabShow() {
        val currentYearMonth = mainActivity.viewModel.currentYearMonth
        if (viewModel.selectYearMonth == currentYearMonth)
            binding.todayFab.hide()
        else
            binding.todayFab.show()
    }

    //更新日历天账单列表
    private fun notifyBillsList() {
        binding.calendarView.post {
            viewModel.doAction(CalenderAction.GetDayBills((binding.calendarView.selectedCalendar)))
        }
    }

    //更新日历
    private fun notifyCalendar() {
        viewModel.doAction(
            CalenderAction.Update(
                viewModel.selectYearMonth.year,
                viewModel.selectYearMonth.month
            )
        )
    }
}
