package com.hao.heji.sync

import com.hao.heji.data.db.Book

/**
 * 账本同步
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
interface IBookSync {
    /**
     * 比对本地和服务端账本差异
     *
     */
    fun compare()

    /**
     * 获取账本信息
     */
    fun getInfo(bid:String)

    /**
     * 删除账本
     *
     */
    fun delete(bid: String)

    /**
     * 清除账本下账单
     *
     * @param bookID
     */
    fun clearBill(bid: String)

    /**
     * 新增账本
     *
     */
    fun add(book: Book)

    /**
     * 更新账本
     *
     */

    fun update(book: Book)
}