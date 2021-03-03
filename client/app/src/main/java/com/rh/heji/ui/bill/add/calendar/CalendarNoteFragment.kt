package com.rh.heji.ui.bill.add.calendar

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.rh.heji.ui.bill.YearSelectPop
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter
import com.rh.heji.ui.bill.adapter.DayBillsNode
import com.rh.heji.ui.bill.adapter.DayIncome
import com.rh.heji.ui.bill.adapter.DayIncomeNode
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.widget.CardViewDecoration
import kotlinx.android.synthetic.main.fragment_calendar_note.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CalendarNoteFragment : BaseFragment() {
    lateinit var binding: FragmentCalendarNoteBinding
    private val viewModel by lazy { getViewModel(CalendarNoteViewModule::class.java) }

    override fun layoutId(): Int {
        return R.layout.fragment_calendar_note
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        //toolBar.title = "日历记账"
        showBlack()
        addYearMonthView()
    }

    override fun initView(view: View) {
        binding = FragmentCalendarNoteBinding.bind(view)

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
        binding.recycler.layoutManager = LinearLayoutManager(mainActivity)
        val adapter = NodeBillsAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(CardViewDecoration(mainActivity.resources, 10f))
        viewModel.dayBillsLiveData.observe(viewLifecycleOwner, Observer {
            adapter.setNewInstance(it as MutableList<BaseNode>)
            adapter.notifyDataSetChanged()
        })
        initCalendarView()
    }

    private fun initCalendarView() {
        binding.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {
                LogUtils.i(calendar.toString())
            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                if (isClick) {

                }
                viewModel.todayBills(calendar)
                val thisMonth = android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.MONTH) + 1;
                if (calendar.month == thisMonth) {
                    binding.todayFab.hide()
                } else {
                    binding.todayFab.show()
                }
                viewModel.updateYearMonth(calendar.year, calendar.month)
                setTitleYearMonth(calendar.year, calendar.month)
            }
        })
        binding.calendarView.post { viewModel.todayBills(calendarView.selectedCalendar) }
        viewModel.calendarLiveData.observe(viewLifecycleOwner, monthObserver())
    }

    private fun monthObserver(): Observer<Map<String, Calendar>> =
            Observer {
                binding.calendarView.setSchemeDate(it)
            }

    /**
     * 该Menu属于全局所以在这里控制
     */
    private fun addYearMonthView() {
        var toolBarCenterTitle = toolBar.findViewById<TextView>(R.id.toolbar_center_title)
        toolBarCenterTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_baseline_arrow_down_white_32), null)
        toolBarCenterTitle.compoundDrawablePadding = 8

        setTitleYearMonth(viewModel.year, viewModel.month)
        viewModel.updateYearMonth(viewModel.year, viewModel.month)
        toolBarCenterTitle.setOnClickListener(View.OnClickListener { v: View? ->
            XPopup.Builder(mainActivity) //.hasBlurBg(true)//模糊
                    .hasShadowBg(true)
                    .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT) //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(YearSelectPop(mainActivity) { year: Int, month: Int ->
                        setTitleYearMonth(year, month)
                        binding.calendarView.scrollToCalendar(year, month, 1)

                    }) /*.enableDrag(false)*/
                    .show()
        })
    }

    private fun setTitleYearMonth(year: Int, month: Int) {
        var toolBarCenterTitle = toolBar.findViewById<TextView>(R.id.toolbar_center_title)
        toolBarCenterTitle.text = "$year.$month"
        viewModel.year = year
        viewModel.month = month
    }

}