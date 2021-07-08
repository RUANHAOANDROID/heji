package com.rh.heji.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rh.heji.AppCache
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.converters.MoneyConverters
import com.rh.heji.data.db.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@Database(
    entities = [Bill::class, Category::class, Dealer::class, Image::class, ErrorLog::class],
    version = 1
)
@TypeConverters(DateConverters::class, MoneyConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao
    abstract fun imageDao(): ImageDao
    abstract fun categoryDao(): CategoryDao
    abstract fun dealerDao(): DealerDao
    abstract fun errorLogDao(): ErrorLogDao

    override fun clearAllTables() {}

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null//暴力丢弃强插升级

        //.addMigrations(MIGRATION_1_2)
        //默认数据库名称
        fun getInstance(): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(AppCache.instance.context, "heji.db").also { INSTANCE = it }
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
}