package com.rh.heji.ui.category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.tabs.TabLayout;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.databinding.FragmentCategoryTabBinding;
import com.rh.heji.ui.bill.add.AddBillFragment;
import com.rh.heji.ui.base.FragmentViewPagerAdapter;

import java.util.Arrays;

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * #标签TAB
 */
public class CategoryTabFragment extends BaseFragment {
    public final String[] TAB_TITLES = {BillType.EXPENDITURE.text(), BillType.INCOME.text()};
    FragmentCategoryTabBinding binding;
    private BillType type = BillType.EXPENDITURE;
    public CategoryFragment[] fragments;
    CategoryViewModule categoryViewModule;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        AddBillFragment addBillFragment = (AddBillFragment) getParentFragment();
        categoryViewModule = addBillFragment.getCategoryViewModule();
    }

    @Override
    protected void initView(View view) {
        binding = FragmentCategoryTabBinding.bind(view);
        showPager();
        //binding.close.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_category_tab;
    }


    private void showPager() {

        fragments = new CategoryFragment[]{
                CategoryFragment.newInstance(BillType.EXPENDITURE.type()),
                CategoryFragment.newInstance(BillType.INCOME.type()),
        };
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter(getChildFragmentManager(), Arrays.asList(fragments), Arrays.asList(TAB_TITLES));
        binding.vpContent.setAdapter(pagerAdapter);
        binding.tab.setupWithViewPager(binding.vpContent);

        //TabLayout+ViewPager联动
        binding.vpContent.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tab));
        binding.tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.vpContent));
        binding.tab.getTabAt(0).select();
        //binding.tab.getTabAt(0).setIcon(R.drawable.ic_route_blue_32dp);
        //binding.tab.getTabAt(1).setIcon(R.drawable.ic_point_red_32dp);
        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragments[tab.getPosition()].setCategory();
                LogUtils.d("onTabSelected", tab.getPosition());
                categoryViewModule.setType(BillType.transform(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LogUtils.d("onTabUnselected", tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                LogUtils.d("onTabReselected", tab.getPosition());
            }
        });
    }

}
