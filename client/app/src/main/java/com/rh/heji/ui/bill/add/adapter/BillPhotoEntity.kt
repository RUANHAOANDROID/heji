package com.rh.heji.ui.bill.add.adapter

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
class BillPhotoEntity {
    //文件路径
    var path: String? = null

    //文件创建时间  File(it).lastModified()
    var createTime: String? = null

    //文件类型
    var type = 0
}