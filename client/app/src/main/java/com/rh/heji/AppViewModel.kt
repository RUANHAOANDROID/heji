package com.rh.heji

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData

class AppViewModel(application: App) : AndroidViewModel(application) {

    val loginEvent = MediatorLiveData<Event<Any>>()

}