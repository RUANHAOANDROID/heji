package com.hao.heji

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hao.heji.config.Config
import com.hao.heji.service.ws.SyncWebSocket
import kotlinx.coroutines.flow.MutableSharedFlow

class AppViewModel(application: App) : AndroidViewModel(application) {

    val loginEvent = MutableSharedFlow<Event<Any>>()

}