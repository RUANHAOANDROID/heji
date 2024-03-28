package com.hao.heji.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hao.heji.App
import com.hao.heji.data.converters.BookUsersConverters
import com.hao.heji.data.converters.DateConverters
import com.hao.heji.data.converters.MoneyConverters
import com.hao.heji.data.db.*

/**
 * @date: 2020/8/28
 * @author: 锅得铁
 * #
 */
@Database(
    entities = [
        Book::class,
        BookUser::class,
        Category::class,
        Bill::class,
        Dealer::class,
        Image::class,
    ],
    version = 1
)
@TypeConverters(DateConverters::class, MoneyConverters::class, BookUsersConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookUserDao(): BookUSerDao
    abstract fun billDao(): BillDao
    abstract fun imageDao(): ImageDao
    abstract fun categoryDao(): CategoryDao
    abstract fun dealerDao(): DealerDao
    abstract fun billImageDao(): BillWithImageDao

    override fun clearAllTables() {}

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null//暴力丢弃强插升级

        //.addMigrations(MIGRATION_1_2)
//        //默认数据库名称
//        @Deprecated("")
//        fun getInstance(context: Context = App.context): AppDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: buildDatabase(context, "heji.db").also { INSTANCE = it }
//            }

        //默认数据库名称
        fun getInstance(userName: String, context: Context = App.context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, "${userName}.db").also { INSTANCE = it }
            }

        private fun buildDatabase(
            context: Context,
            dbName: String
        ) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, dbName
        )
            .fallbackToDestructiveMigration() //暴力丢弃强插升级
            .allowMainThreadQueries() //.addMigrations(MIGRATION_1_2)
            .build()

        /**
         * 升级
         */
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //do
            }
        }


    }

    fun reset() {
        INSTANCE?.let {
            if (it.isOpen) {
                it.close()
            }
        }
        INSTANCE = null
    }
}