package com.rh.heji.ui.calendar

import com.chad.library.adapter.base.entity.node.BaseNode
import com.haibin.calendarview.Calendar
import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState

internal sealed interface CalenderAction : IAction {
    class Update(val year: Int, val month: Int) : CalenderAction
    class GetDayBills(val calendar: Calendar) : CalenderAction
}

internal sealed interface CalenderUiState : IUiState {
    class DayBills(val data: Collection<BaseNode>) : CalenderUiState
    class Calender(val data: Map<String, Calendar>) : CalenderUiState
}
