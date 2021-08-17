package com.rh.heji.ui.bill.category

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.databinding.FragmentCategoryTabBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.base.FragmentViewPagerAdapter
import com.rh.heji.ui.bill.add.AddBillFragment
import com.rh.heji.ui.bill.category.CategoryFragment.Companion.newInstance

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * #标签TAB
 */
class CategoryTabFragment : BaseFragment() {
    private val tabTitles = arrayOf(BillType.EXPENDITURE.text(), BillType.INCOME.text())
    lateinit var binding: FragmentCategoryTabBinding
    val fragments = arrayOf(
        newInstance(BillType.EXPENDITURE),
        newInstance(BillType.INCOME)
    )
    lateinit var categoryViewModule: CategoryViewModule
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val addBillFragment = parentFragment as AddBillFragment
        categoryViewModule = addBillFragment.categoryViewModule
    }

    override fun initView(view: View) {
        binding = FragmentCategoryTabBinding.bind(view)
        showPager()
    }

    override fun layoutId(): Int {
        return R.layout.fragment_category_tab
    }

    private fun showPager() {

        val pagerAdapter = FragmentViewPagerAdapter(
            childFragmentManager,
            fragments.toList(),
            tabTitles.toList()
        )
        binding.vpContent.adapter = pagerAdapter
        binding.tab.setupWithViewPager(binding.vpContent)

        //TabLayout+ViewPager联动
        binding.vpContent.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tab))
        binding.tab.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.vpContent))
        binding.tab.getTabAt(0)!!.select()
        binding.tab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                LogUtils.d("onTabSelected", tab.position)
                categoryViewModule.type = BillType.transform(tab.position)
                fragments[tab.position].setCategory()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                LogUtils.d("onTabUnselected", tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                LogUtils.d("onTabReselected", tab.position)
            }
        })
        categoryViewModule.getCategoryType().observe(this, {
            binding.tab.getTabAt(if (it.type() == 1) 1 else 0)?.let { tab ->
                if (!tab.isSelected)
                    tab.select()
            }
        })
    }

    fun setCategory(category: String, type: Int) {
        if (type == BillType.EXPENDITURE.type()) {
            binding.tab.getTabAt(0)?.select()
            fragments[0].setCategory(category)
        } else if (type == BillType.INCOME.type()) {
            binding.tab.getTabAt(1)?.select()
            fragments[1].setCategory(category)
        }

    }
}