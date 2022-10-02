package com.rh.heji.ui.user

/**
 * @date: 2020/12/16
 * @author: 锅得铁
 * #
 */
data class UserInfo(val name: String,
                    val tel: String?,
                    val password: String?,
                    val authenticate: String,
                    var authorities: List<Authority>? = null)