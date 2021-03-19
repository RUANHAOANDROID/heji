package com.rh.heji.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.rh.heji.App;
import com.rh.heji.data.db.Bill;
import com.rh.heji.data.db.BillDao;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.CategoryDao;
import com.rh.heji.data.db.Dealer;
import com.rh.heji.data.db.DealerDao;
import com.rh.heji.data.db.ImageDao;
import com.rh.heji.data.db.Image;


/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
@androidx.room.Database(
        entities = {
                Bill.class,
                Category.class,
                Dealer.class,
                Image.class
        },
        version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract BillDao billDao();

    public abstract ImageDao imageDao();

    public abstract CategoryDao categoryDao();

    public abstract DealerDao dealerDao();

    public static AppDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    //默认数据库名称
                    String dbName = "heji.db";
                    Context context = App.getContext();
                    INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, dbName)
                            .fallbackToDestructiveMigration()//暴力丢弃强插升级
                            .allowMainThreadQueries()
                            //.addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 升级
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //do
        }
    };

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
