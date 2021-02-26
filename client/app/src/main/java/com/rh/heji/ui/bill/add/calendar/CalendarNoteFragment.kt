package com.rh.heji.ui.bill.add.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val calendar:Calendar =Calendar()
        calendar.year=2021
        calendar.month=2
        calendar.day=26
        calendar.scheme="125"
        var map = mutableMapOf<String, Calendar>()
        map["-100"] = calendar;
        binding.calendarView.setSchemeDate(map)
    }

}