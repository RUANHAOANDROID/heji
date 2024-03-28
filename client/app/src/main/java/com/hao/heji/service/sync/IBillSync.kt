package com.hao.heji.service.sync

import com.hao.heji.data.db.Bill
import com.hao.heji.data.db.Image

/**
 * 账单同步
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
interface IBillSync {
    /**
     * 比对本地和服务端账本差异
     *
     */
    fun compare()

    /**
     * 获取账单
     */

    fun getBills(year: String)

    /**
     * 删除账本
     *
     */
    fun delete(billID: String)

    /**
     * 新增账本
     *
     */
    fun add(bill: Bill)

    /**
     * 更新账本
     *
     */

    fun update(book: Bill)

    /**
     * 删除照片
     */
    fun deleteImage(image: Image)

    /**
     * 添加照片
     */
    fun addImage(image: Image)
}