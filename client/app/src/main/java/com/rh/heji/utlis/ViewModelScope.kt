package com.rh.heji.utlis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 *Date: 2021/7/15
 *Author: 锅得铁
 *#
 */
fun ViewModel.launchIO(
    block: suspend () -> Unit,
    error: suspend (Throwable) -> Unit = { it.printStackTrace() }
) = viewModelScope.launch(Dispatchers.IO) {
    try {
        block()
    } catch (e: Throwable) {
        error(e)
        e.printStackTrace()
    }
}

fun ViewModel.launch(
    block: suspend () -> Unit,
    error: suspend (Throwable) -> Unit = { it.printStackTrace() }
) = viewModelScope.launch {
    try {
        block()
    } catch (e: Throwable) {
        error(e)
        e.printStackTrace()
    }
}

suspend fun runMainThread(
    block: suspend () -> Unit
) = withContext(Dispatchers.Main) {
    block()
}
suspend fun runIOThread(
    block: suspend () -> Unit
) = withContext(Dispatchers.IO) {
    block()
}
suspend fun <T> (() -> T).runMainThread() =withContext(Dispatchers.Main) { invoke() }

@kotlinx.coroutines.ObsoleteCoroutinesApi
fun ViewModel.launchNewThread(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) =
    viewModelScope.launch(
        newSingleThreadContext("rh_newThread")
    ) {
        try {
            block()
        } catch (e: Throwable) {
            error(e)
            e.printStackTrace()
        }
    }