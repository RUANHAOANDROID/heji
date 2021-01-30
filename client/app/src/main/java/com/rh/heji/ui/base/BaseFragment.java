package com.rh.heji.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.rh.heji.MainActivity;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public abstract class BaseFragment extends Fragment {
    public static final String TAG = "BaseFragment";
    protected View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null)
                viewGroup.removeView(view);
        } else {
            view = inflater.inflate(layoutId(), container, false);
            initView(view);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restoreVies();
    }

    /**
     * 执行在onCreateView的时候
     *
     * @return layoutID
     */
    protected abstract int layoutId();

    /**
     * 初始化View
     *
     * @param view
     */
    protected abstract void initView(View view);


    /**
     * 设定MainActivity全局控件
     */
    protected void setUpViews() {

    }

    /**
     * 恢复Main全局控件
     */
    protected void restoreVies() {

    }


    public <T extends ViewModel> T getViewModel(Class<T> clazz) {
        return new ViewModelProvider(this).get(clazz);
    }

    public <T extends ViewModel> T getActivityViewModel(Class<T> clazz) {
        return new ViewModelProvider(getActivity()).get(clazz);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
