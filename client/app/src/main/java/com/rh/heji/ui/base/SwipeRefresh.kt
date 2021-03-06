package com.rh.heji.ui.base

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rh.heji.R

/**
 *Date: 2021/7/14
 *Author: 锅得铁
 *#
 */

/**
 * 初始化下拉刷新空间
 *  在下拉时调用block()并在block完成后showRefreshing
 */
fun BaseFragment.swipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout, block: () -> Unit) {
    swipeRefreshLayout.setProgressViewOffset(true, 0, 200)//设置缩放，起始位置，最终位置
    swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
    swipeRefreshLayout.setOnRefreshListener {
        showRefreshing(swipeRefreshLayout)
        block()
    }

}

fun BaseFragment.hideRefreshing(swipeRefreshLayout: SwipeRefreshLayout) {
    swipeRefreshLayout.isRefreshing = false
}

fun BaseFragment.showRefreshing(swipeRefreshLayout: SwipeRefreshLayout) {
    swipeRefreshLayout.isRefreshing = true
}