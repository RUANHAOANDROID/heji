package com.hao.heji

import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

class AppViewModel(application: App) : AndroidViewModel(application) {

    val loginEvent = MutableSharedFlow<Event<Any>>()
}