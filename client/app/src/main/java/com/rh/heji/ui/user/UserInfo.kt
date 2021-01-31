package com.rh.heji.ui.user

/**
 * Date: 2020/12/16
 * Author: 锅得铁
 * #
 */
data class UserInfo(val name: String,
                    val tel: String?,
                    val password: String?,
                    val authenticate: String,
                    var authorities: List<Authority>? = null) {

}