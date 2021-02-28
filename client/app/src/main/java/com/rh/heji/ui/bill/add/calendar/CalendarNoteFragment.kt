package com.rh.heji.ui.bill.add.calendar

import android.view.View
import com.haibin.calendarview.Calendar
import com.rh.heji.R
import com.rh.heji.databinding.FragmentCalendarNoteBinding
import com.rh.heji.ui.base.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CalendarNoteFragment : BaseFragment() {
    lateinit var binding: FragmentCalendarNoteBinding
    override fun layoutId(): Int {
        return R.layout.fragment_calendar_note
    }

    override fun initView(view: View) {
        binding = FragmentCalendarNoteBinding.bind(view)
        val calendar: Calendar = Calendar()
        calendar.year = 2021
        calendar.month = 2
        calendar.day = 26
        calendar.scheme = "125"
        var map = mutableMapOf<String, Calendar>()

        val key1: String = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase").toString()
        val calender1: Calendar = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase")
        val key2: String = getSchemeCalendar(2021, 3, 27, mainActivity.getColor(R.color.income), "ase").toString()
        val calender2: Calendar = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase")
        val key3: String = getSchemeCalendar(2021, 4, 27, mainActivity.getColor(R.color.income), "ase").toString()
        val calender3: Calendar = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase")
        val key4: String = getSchemeCalendar(2021, 5, 27, mainActivity.getColor(R.color.income), "ase").toString()
        val calender4: Calendar = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase")
        val key5: String = getSchemeCalendar(2021, 6, 27, mainActivity.getColor(R.color.income), "ase").toString()
        val calender5: Calendar = getSchemeCalendar(2021, 2, 27, mainActivity.getColor(R.color.income), "ase")

        map[key1] = calender1;
        map[key2] = calender2;
        map[key3] = calender3;
        map[key4] = calender4;
        map[key5] = calender5;
        binding.calendarView.setSchemeDate(map)
    }

    private fun getSchemeCalendar(year: Int, month: Int, day: Int, color: Int, text: String): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        calendar.addScheme(color, "假")
        calendar.addScheme(if (day % 2 == 0) -0xff3300 else -0x2ea012, "节")
        calendar.addScheme(if (day % 2 == 0) -0x9a0000 else -0xbe961f, "记")
        return calendar
    }
}