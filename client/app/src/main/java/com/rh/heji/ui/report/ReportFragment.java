package com.rh.heji.ui.report;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rh.heji.R;
import com.rh.heji.ui.base.BaseFragment;

/**
 * 报告统计页面
 */
public class ReportFragment extends BaseFragment {

    private ReportViewModel reportViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reportViewModel = getViewModel(ReportViewModel.class);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected void initView(View view) {
        final TextView textView = view.findViewById(R.id.text_gallery);
        reportViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }
}