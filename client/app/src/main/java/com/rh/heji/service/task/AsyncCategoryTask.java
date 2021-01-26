package com.rh.heji.service.task;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.network.BaseResponse;
import com.rh.heji.data.network.HeJiServer;
import com.rh.heji.data.network.request.CategoryEntity;
import com.rh.heji.service.DataSyncService;
import com.rh.heji.utlis.http.basic.ServiceCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Date: 2020/10/27
 * Author: 锅得铁
 * # 同步Category
 */
public class AsyncCategoryTask implements Runnable {
    final HeJiServer heJiServer = (HeJiServer) ServiceCreator.getInstance().createService(HeJiServer.class);
    List<Category> categories;

    public AsyncCategoryTask(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public void run() {
        if (null != categories && categories.size() > 0)
            categories.forEach(category -> {
                if (category.getSynced() == Category.STATUS_DELETE) {
                    deleteCategory(category);
                } else if (category.getSynced() == Category.STATUS_NOT_SYNC) {
                    pushCategory(category);
                }

            });
    }

    private void deleteCategory(Category category) {
        try {
            Response<BaseResponse<?>> response = heJiServer.deleteCategoryByName(category.getCategory()).execute();
            if (null != response && response.isSuccessful()) {
                if (response.code() == 200) {
                    String str = String.valueOf(response.body());
                    AppDatabase.getInstance().categoryDao().delete(category);//删除
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushCategory(Category category) {
        try {
            Response<BaseResponse<?>> response = heJiServer.addCategory(new CategoryEntity(category)).execute();
            if (null != response && response.isSuccessful()) {
                if (response.code() == 200) {
                    String str = String.valueOf(response.body());
                    category.setSynced(Category.STATUS_SYNCED);//已上传
                    AppDatabase.getInstance().categoryDao().update(category);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多个上传
     *
     * @param data
     */
    private void postCategories(List<Category> data) {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        data.forEach(category -> {
            categoryEntities.add(new CategoryEntity(category));
        });
        try {
            Response<BaseResponse<?>> response = heJiServer.addCategories(categoryEntities).execute();
            if (null != response && response.isSuccessful()) {
                if (response.code() == 200) {
                    String str = String.valueOf(response.body());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
