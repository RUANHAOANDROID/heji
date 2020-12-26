package com.rh.heji;

import androidx.lifecycle.ViewModel;

/**
 * Date: 2020/11/3
 * Author: 锅得铁
 * #
 */
public class MainViewModel extends ViewModel {
    /**
     * 控制toolbar
     */
    String homeUUID;
    String reportUUID;
    String settingUUID;

    public String getHomeUUID() {
        return homeUUID;
    }

    public void setHomeUUID(String homeUUID) {
        this.homeUUID = homeUUID;
    }

    public String getReportUUID() {
        return reportUUID;
    }

    public void setReportUUID(String reportUUID) {
        this.reportUUID = reportUUID;
    }

    public String getSettingUUID() {
        return settingUUID;
    }

    public void setSettingUUID(String settingUUID) {
        this.settingUUID = settingUUID;
    }
}
