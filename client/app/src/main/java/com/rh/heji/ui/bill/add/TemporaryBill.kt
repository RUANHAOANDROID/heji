package com.rh.heji.ui.bill.add

import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image

/**
 * Date: 2022/4/2
 * Author: 锅得铁
 * # 临时账单，修改或新增时修改该临时对象，保存时根据临时对象存储到库
 */
class TemporaryBill {
    var bill: Bill? = null
    var images: MutableList<Image>? = null
}