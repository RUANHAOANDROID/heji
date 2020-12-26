package com.rh.heji.utlis.http.basic;

import android.app.Activity;

import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * `
 * Retrofit Callback基类
 */

abstract public class BaseCallback<T> implements Callback<T> {
    protected boolean isAutoCloseLoadingDialog = true;//自动关闭LoadingDialog

    //WeakReference<BaseActivity> weakReference ;
    public BaseCallback() {
    }

    /**
     * 是否自动关闭LoadingDialog
     **/
    public BaseCallback(boolean isAutoCloseLoadingDialog) {
        this.isAutoCloseLoadingDialog = isAutoCloseLoadingDialog;
    }

    /**
     * 带进度的：还没来得及测试！演示版本尽量不要通过该构造方法初始化
     *
     * @param activity
     */
    public BaseCallback(Activity activity) {
        this.isAutoCloseLoadingDialog = true;
        //weakReference  = new WeakReference<>(activity);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (isAutoCloseLoadingDialog) {
            //自动结束加载框
        }
        if (null != response && response.isSuccessful()) {
            try {
                onSuccess(response.body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onError(response.body());
            try {//提示错误信息
                String errorBody =response.errorBody().string();
                if (errorBody.contains("message")){
                    JSONObject jsonObject =new JSONObject(errorBody);
                    String message =jsonObject.getString("message");
                    ToastUtils.showLong(message);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        onError(null);

    }


    protected abstract void onSuccess(T data) throws IOException;

    protected void onError(T response) {
    }
}
