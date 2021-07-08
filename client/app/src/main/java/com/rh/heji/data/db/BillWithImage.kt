package com.rh.heji.data.db

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Date: 2020/9/15
 * Author: 锅得铁
 * #
 */
data class BillWithImage(
    @JvmField
    @Embedded
    var bill: Bill? = null,

    @JvmField
    @Relation(parentColumn = "bill_id", entityColumn = "_bid")
    var images: List<Image>? = null
) {

}