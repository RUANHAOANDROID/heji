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
    public val thisYear: Int
        public get() = Calendar.getInstance()[Calendar.YEAR]
    public val thisMonth: Int
        public get() = Calendar.getInstance()[Calendar.MONTH] + 1
}