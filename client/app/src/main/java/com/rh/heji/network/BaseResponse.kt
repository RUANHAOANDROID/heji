package com.rh.heji.network

/**
 * Date: 2020/9/23
 * Author: 锅得铁
 * #
 */
data class BaseResponse<T>(
    var code: Int,
    var msg: String,
    var date: T
)