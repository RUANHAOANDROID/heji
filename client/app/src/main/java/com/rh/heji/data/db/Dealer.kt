package com.rh.heji.data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 2020/9/22
 * Author: 锅得铁
 * #经手人
 */
@Entity(tableName = "bill_dealer")
public class Dealer {
    @PrimaryKey()
    @NotNull
    @ColumnInfo(name = "dealer_name")
    String userName;

    public Dealer(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
