package com.rh.heji.data

import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.HejiNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * ·····························
 *      ADD
 *  DATA ADD    -->LOCAL DB
 *  DATA UPLOAD -->SERVER DB
 *  DATA UPDATE -->LOCAL DB -STATUS
 *  ····························
 *      DELETE
 *  DATE DELETE  -->LOCAL DB RE_DELETE
 *  DATE DELETE  -->SERVER DELETE
 *  DATE DELETE  -->LOCAL DB DELETE
 *  ····························
 *      UPDATE
 *  DATE UPDATE  -->UPDATE LOCAL DB
 *  DATE UPDATE  -->UPDATE SEVER
 *  DATE UPDATE  -->UPDATE LOCAL DB STATUS
 *  ····························
 *      QUERY
 *  DATE QUERY  -->LOCAL DB
 *  DATE QUERY  -->SERVER PULL
 *  DATE UPDATE -->UPSERT TO LOCAL DB
 *  ····························
 */
open class DataRepository {
    companion object NETWORK {
        const val OK = 0
    }

    protected val network = HejiNetwork.getInstance()
    protected val database = AppDatabase.getInstance()
    protected val bookDao = database.bookDao()
    protected val billDao = database.billDao()
    protected val categoryDao = database.categoryDao()
    val imgDao =   AppDatabase.getInstance().imageDao()
}