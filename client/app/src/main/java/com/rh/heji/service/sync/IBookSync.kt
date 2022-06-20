package com.rh.heji.service.sync

import com.rh.heji.data.db.Book

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
     * 删除账本
     *
     */
    fun delete(bookID: String)

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