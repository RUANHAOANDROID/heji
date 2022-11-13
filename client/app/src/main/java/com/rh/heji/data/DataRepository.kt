package com.rh.heji.data

import com.rh.heji.App
import com.rh.heji.network.HttpManager

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

    protected val network = HttpManager.getInstance()
    protected val database = App.dataBase
    protected val bookDao = database.bookDao()
    protected val billDao = database.billDao()
    protected val categoryDao = database.categoryDao()
    val imgDao =   App.dataBase.imageDao()
}