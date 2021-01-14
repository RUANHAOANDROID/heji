package com.rh.heji.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import static com.rh.heji.data.db.Bill.STATUS_NOT_SYNC;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #收入/支出 类型标签
 */
@Entity(tableName = "bill_category",primaryKeys = {"category","level","type"})
public class Category {
    public static final int STATUS_SYNCED = 1;//已同步的
    public static final int STATUS_DELETE = -1;//本地删除的
    public static final int STATUS_NOT_SYNC = 0;//未同步的
    
    @NonNull
    @ColumnInfo(name = "category")
    String category;
    @ColumnInfo(name = "level")
    int level;
    /**
     * 收入、支出
     */
    @ColumnInfo(name = "type")
    int type;
    /**
     * 是否在记账页面显示
     */
    @Ignore
    @ColumnInfo(name = "visibility", defaultValue = "1")
    int visibility;

    @Ignore
    public boolean selected = false;

    @ColumnInfo(name = "sync_status",defaultValue = "0")
    int synced = STATUS_NOT_SYNC;

    public Category() {
    }

    @Ignore
    public Category(@NonNull String label, int level, int szType) {
        this.category = label;
        this.level = level;
        this.type = szType;
    }

    @NonNull
    public String getLabel() {
        return category;
    }

    public void setLabel(@NonNull String label) {
        this.category = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category1 = (Category) o;
        return type == category1.type &&
                category.equals(category1.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, type);
    }

    @Override
    public String toString() {
        return "Category{" +
                "category='" + category + '\'' +
                ", level=" + level +
                ", type=" + type +
                ", visibility=" + visibility +
                ", selected=" + selected +
                ", synced=" + synced +
                '}';
    }
}
