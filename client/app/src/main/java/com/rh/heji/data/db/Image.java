package com.rh.heji.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Date: 2020/11/19
 * Author: 锅得铁
 * #
 */
@Entity(tableName = Image.TAB_NAME)
public class Image {

    public static final String TAB_NAME = "bill_img";
    public static final String COLUMN_ID = "bill_img_id";
    public static final String COLUMN_PATH = "img_path";
    public static final String COLUMN_ONLINE_PATH = "img_online_path";
    public static final String COLUMN_STATUS = "sync_status";

    @PrimaryKey(autoGenerate = true)
    long id;


    @ForeignKey(entity = Bill.class, parentColumns = "bill_id", childColumns = "bill_img_id", onDelete = ForeignKey.CASCADE)
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    String billImageID;

    @ColumnInfo(name = COLUMN_PATH)
    String localPath;

    @ColumnInfo(name = COLUMN_ONLINE_PATH)
    String onlinePath;

    @ColumnInfo(name = COLUMN_STATUS,defaultValue = "0")
    int synced ;

    public Image() {
    }

    @Ignore
    public Image(String imgID) {
        this.billImageID = imgID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getOnlinePath() {
        return onlinePath;
    }

    public void setOnlinePath(String onlinePath) {
        this.onlinePath = onlinePath;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    @NonNull
    public String getBillImageID() {
        return billImageID;
    }

    public void setBillImageID(@NonNull String billImageID) {
        this.billImageID = billImageID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id &&
                billImageID.equals(image.billImageID) &&
                Objects.equals(localPath, image.localPath) &&
                Objects.equals(onlinePath, image.onlinePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, billImageID, localPath, onlinePath);
    }
}
