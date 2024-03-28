package com.hao.heji.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 *@date: 2021/7/15
 *@author: 锅得铁
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

suspend fun <T> (() -> T).runMainThread() = withContext(Dispatchers.Main) { invoke() }

@ObsoleteCoroutinesApi
fun ViewModel.launchNewThread(block: suspend () -> Unit, error: suspend (Throwable) -> Unit = {}) =
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