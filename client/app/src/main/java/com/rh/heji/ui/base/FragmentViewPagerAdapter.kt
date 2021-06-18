package com.rh.heji.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentViewPagerAdapter(
    fm: FragmentManager,
    private val fragmentList: List<Fragment>,
    private val textList: List<String>
) : FragmentPagerAdapter(
    fm
) {
    override fun getPageTitle(position: Int): CharSequence? {
        return textList[position]
    }

    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}