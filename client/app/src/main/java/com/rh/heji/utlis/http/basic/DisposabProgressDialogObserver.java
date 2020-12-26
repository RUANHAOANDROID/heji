package com.rh.heji.utlis.http.basic;

import android.app.Activity;
import android.widget.Toast;


import io.reactivex.observers.DisposableObserver;

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * 带进度的请求 DisposableObserver
 * 切记使用 CompositeDisposable.add（Observer）
 *
 * @param <T>
 */
public abstract class DisposabProgressDialogObserver<T> extends DisposableObserver<T> {

    Activity activity;
    private String loadTip = "加载中...";

    public DisposabProgressDialogObserver(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onStart() {
        showDialog();
    }

    @Override
    public void onNext(T baseResponse) {
        onSuccess(baseResponse);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
        hideDialog();
        tip();
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        hideDialog();
    }

    private void tip() {
    }

    private void showDialog() {
    }

    private void hideDialog() {
    }

    public abstract void onSuccess(T baseResponse);

    public abstract void onFailure(Throwable e);
}
