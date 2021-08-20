package com.rh.heji

import com.rh.heji.data.BillType
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Category
import com.rh.heji.ui.user.JWTParse


var currentBook = Book(
    name = "个人账本",
    createUser = "local",
    type = "日常账本"
)
var currentUser: JWTParse.User = JWTParse.User("local", mutableListOf(), "")

val incomeDefaultCategory = Category(category = "其他", level = 0, type = BillType.INCOME.type())
val expenditureDefaultCategory =
    Category(category = "其他", level = 0, type = BillType.EXPENDITURE.type())
///**
// * 当前账本
// */
//var currentBook = Book(
//    id = mmkv()!!.decodeString(CURRENT_BOOK_ID).toString(),
//    name = mmkv()!!.decodeString(CURRENT_BOOK).toString()
//)
//    set(value) {
//        mmkv().let { mmkv ->
//            mmkv!!.encode(CURRENT_BOOK_ID, value.id)
//            mmkv!!.encode(CURRENT_BOOK, value.name)
//        }
//        field = value
//    }
