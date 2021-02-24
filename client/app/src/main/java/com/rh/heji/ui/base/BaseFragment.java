package com.rh.heji.ui.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.MainActivity;
import com.rh.heji.R;
import com.rh.heji.utlis.Logger;

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
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpToolBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    protected void setUpToolBar() {
        try {
            getToolBar().getMenu().clear();
        } catch (Exception e) {
            LogUtils.e("not include toolbar");
        }
    }

    public androidx.appcompat.widget.Toolbar getToolBar() {
        return view.findViewById(R.id.toolbar);
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

    public Drawable blackDrawable() {
        int ico = androidx.appcompat.R.drawable.abc_ic_ab_back_material;
        return getResources().getDrawable(ico, getMainActivity().getTheme());
    }
}
