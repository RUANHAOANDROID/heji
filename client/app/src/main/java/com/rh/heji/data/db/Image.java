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

    @NonNull
    @PrimaryKey()
    String _id;


    @ForeignKey(entity = Bill.class, parentColumns = "bill_id", childColumns = "bill_img_id", onDelete = ForeignKey.CASCADE)
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    String billImageID;

    String md5;

    private String ext;

    @ColumnInfo(name = COLUMN_PATH)
    String localPath;

    @ColumnInfo(name = COLUMN_ONLINE_PATH)
    String onlinePath;

    @ColumnInfo(name = COLUMN_STATUS, defaultValue = "0")
    int synced;

    public Image() {
    }

    @Ignore
    public Image(String _id, String bill_id) {
        this._id = _id;
        this.billImageID = bill_id;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
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
        return _id == image._id &&
                billImageID.equals(image.billImageID) &&
                Objects.equals(localPath, image.localPath) &&
                Objects.equals(onlinePath, image.onlinePath);
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, billImageID, localPath, onlinePath);
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
