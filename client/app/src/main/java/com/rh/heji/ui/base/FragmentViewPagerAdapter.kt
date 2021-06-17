package com.rh.heji.ui.base;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> textList;
    public FragmentViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> textList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.textList = textList;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return textList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public List<Fragment> getFragmentList () {
        return fragmentList;
    }
}
