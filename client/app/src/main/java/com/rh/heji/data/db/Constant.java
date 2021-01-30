package com.rh.heji.data.db;

public interface Constant {
    public static final int STATUS_SYNCED = 1;//已同步的
    public static final int STATUS_DELETE = -1;//本地删除的
    public static final int STATUS_NOT_SYNC = 0;//未同步的
    public static final int STATUS_UPDATE = 2;//已更改的
}
