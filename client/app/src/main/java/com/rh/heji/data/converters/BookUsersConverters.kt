package com.rh.heji.data.converters

import androidx.room.TypeConverter
import com.rh.heji.data.db.BookUser
import com.rh.heji.moshi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types

object BookUsersConverters {

    @FromJson
    @JvmStatic
    @TypeConverter
    fun str2Users(value: String?): MutableList<BookUser> {
        return if (value.isNullOrEmpty()||value =="null") {
            mutableListOf()
        } else (moshi.adapter<MutableList<BookUser>>(type()).fromJson(value))!!

    }

    @ToJson
    @JvmStatic
    @TypeConverter
    fun users2Str(users: MutableList<BookUser>?): String {
        return if (users?.isEmpty() == true) "null" else moshi.adapter<MutableList<BookUser>>(type())
            .toJson(users)
    }

    private fun type() = Types.newParameterizedType(MutableList::class.java,
        BookUser::class.java)
}