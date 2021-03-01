package com.rh.heji.ui.bill.add.calendar

import android.view.View
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.rh.heji.R
import com.rh.heji.databinding.FragmentCalendarNoteBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.add.AddBillFragmentArgs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CalendarNoteFragment : BaseFragment() {
    lateinit var binding: FragmentCalendarNoteBinding
    override fun layoutId(): Int {
        return R.layout.fragment_calendar_note
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        toolBar.title = "日历记账"
        showBlack()
    }

    override fun initView(view: View) {
        binding = FragmentCalendarNoteBinding.bind(view)
        var map = mutableMapOf<String, Calendar>()
        val key1: String = getSchemeCalendar(2021, 2, 27, "-2321", "+12055").toString()
        val calender1: Calendar = getSchemeCalendar(2021, 2, 27, "-2321", "+12055")

        val key2: String = getSchemeCalendar(2021, 3, 27, "-100", "+12055").toString()
        val calender2: Calendar = getSchemeCalendar(2021, 2, 27, "-100", "+12055")

        val key3: String = getSchemeCalendar(2021, 4, 27, "-100", "+12055").toString()
        val calender3: Calendar = getSchemeCalendar(2021, 2, 27, "-100", "+12055")

        val key4: String = getSchemeCalendar(2021, 5, 27, "-100", "+12055").toString()
        val calender4: Calendar = getSchemeCalendar(2021, 2, 27, "-100", "+12055")

        val key5: String = getSchemeCalendar(2021, 6, 27, "-100123421", "+12055").toString()
        val calender5: Calendar = getSchemeCalendar(2021, 2, 27, "-100123421", "+12055")

        map[key1] = calender1;
        map[key2] = calender2;
        map[key3] = calender3;
        map[key4] = calender4;
        map[key5] = calender5;
        binding.calendarView.setSchemeDate(map)
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
        binding.calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {
                LogUtils.i(calendar.toString())
            }

            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                if (isClick) {

                }
                val thisMonth = android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.MONTH) + 1;
                if (calendar.month == thisMonth) {
                    binding.todayFab.hide()
                } else {
                    binding.todayFab.show()
                }
            }
        })
    }

    /**
     * 年
     * 月
     * 日
     * 支出
     * 收入
     */
    private fun getSchemeCalendar(year: Int, month: Int, day: Int, expenditure: String = "0", income: String = "0"): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        //calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        //calendar.scheme = text
        //calendar.addScheme(color, "假")
        calendar.addScheme(mainActivity.getColor(R.color.expenditure), expenditure)
        calendar.addScheme(mainActivity.getColor(R.color.income), income)
        return calendar
    }
}