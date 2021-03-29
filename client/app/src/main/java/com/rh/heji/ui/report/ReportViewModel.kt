package com.rh.heji.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ReportViewModel : ViewModel() {
    private val mText: MutableLiveData<String> = MutableLiveData()
    val text: LiveData<String>
        get() = mText

    init {
        mText.value = "This is gallery fragment"
    }
     val thisYear: Int
         get() = Calendar.getInstance()[Calendar.YEAR]
     val thisMonth: Int
         get() = Calendar.getInstance()[Calendar.MONTH] + 1
}