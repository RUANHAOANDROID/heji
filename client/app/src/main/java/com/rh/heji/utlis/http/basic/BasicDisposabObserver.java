package com.rh.heji.utlis.http.basic;

import io.reactivex.observers.DisposableObserver;

/**
 * Date: 2019-10-15
 * Author: 锅得铁
 * 不带进度条的请求，加载假数据尤为重要切记使用 CompositeDisposable.add（Observer）
 */
public abstract class BasicDisposabObserver<T> extends DisposableObserver<T> {

    public BasicDisposabObserver(){
    }
    @Override
    protected void onStart() {

    }

    @Override
    public void onNext(T baseResponse) {
        onSuccess(baseResponse);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e);
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }
    public abstract  void onSuccess(T baseResponse);
    public abstract  void onFailure(Throwable e);
}
