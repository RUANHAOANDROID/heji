package com.rh.heji;

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

    /**
     * 初始化View
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 执行在onCreateView的时候
     *
     * @return layoutID
     */
    protected abstract int layoutId();

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
