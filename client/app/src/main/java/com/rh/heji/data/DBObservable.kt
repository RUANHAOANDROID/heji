package com.rh.heji.data

import androidx.room.Entity

/**
 *Date: 2021/6/17
 *Author: 锅得铁
 *# entity 为 @Entity类型
 */
class DBObservable(var crud: CRUD, var entity: Any)

enum class CRUD {
    CREATE, READ, UPDATE, DELETE
}
