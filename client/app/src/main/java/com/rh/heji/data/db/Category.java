package com.rh.heji.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.rh.heji.data.db.mongo.ObjectId;

import java.util.Objects;

import static com.rh.heji.data.db.Constant.STATUS_NOT_SYNC;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #收入/支出 类型标签
 */
@Entity(tableName = "bill_category")
public class Category {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    String _id;

    @NonNull
    @ColumnInfo(name = "category")
    String category;

    @ColumnInfo(name = "level")
    int level;
    /**
     * 收入、支出
     */
    @NonNull
    @ColumnInfo(name = "type", defaultValue = "-1")
    int type;
    /**
     * 在账本下排序
     */
    @ColumnInfo(name = "index")
    int index;

    /**
     * 是否在记账页面显示
     */
    @Ignore
    @ColumnInfo(name = "visibility", defaultValue = "1")
    int visibility;

    @Ignore
    public boolean selected = false;

    @ColumnInfo(name = "sync_status", defaultValue = "0")
    int synced = STATUS_NOT_SYNC;

    public Category() {
    }

    @Ignore
    public Category(String _id) {
        this._id = _id;
    }

    @Ignore
    public Category(@NonNull String label, int level, int szType) {
        this.category = label;
        this.level = level;
        this.type = szType;
    }

    @Ignore
    public Category(@NonNull String _id, String label, int level, int szType) {
        this._id = _id;
        this.category = label;
        this.level = level;
        this.type = szType;
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

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
