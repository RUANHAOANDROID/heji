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


/**
 * 标签 TAB 包含了收入和支出
 * Date: 2020/10/11
 * @author: 锅得铁
 *
 */
class CategoryTabFragment : BaseFragment() {
    private val tabTitles = arrayOf(BillType.EXPENDITURE.text(), BillType.INCOME.text())
    lateinit var binding: FragmentCategoryTabBinding
    private var currentType: BillType = BillType.EXPENDITURE

    val categoryFragments = arrayOf(
        CategoryFragment.newInstance(BillType.EXPENDITURE),
        CategoryFragment.newInstance(BillType.INCOME)
    )
    lateinit var categoryViewModel: CategoryViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val addBillFragment = parentFragment as AddBillFragment
        //从父Fragment拿到ViewModule
        categoryViewModel = addBillFragment.categoryViewModel
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
            categoryFragments.toList(),
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
                val selectType = BillType.transform(tab.text.toString())
                categoryViewModel.type = selectType
                currentType = selectType
                categoryFragments[tab.position].setCategory()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                LogUtils.d("onTabUnselected", tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                LogUtils.d("onTabReselected", tab.position)
            }
        })
        categoryViewModel.getCategoryType().observe(this) {
            if (currentType == it) return@observe

            val selectTab = if (it.type() == 1) 1 else 0
            val tabAt = binding.tab.getTabAt(selectTab)
            tabAt?.let { tab ->
                if (!tab.isSelected)
                    tab.select()
            }
        }
    }

    fun setCategory(category: String, type: Int) {
        if (type == BillType.EXPENDITURE.type()) {
            binding.tab.getTabAt(0)?.select()
            categoryFragments[0].setCategory(category)
        } else if (type == BillType.INCOME.type()) {
            binding.tab.getTabAt(1)?.select()
            categoryFragments[1].setCategory(category)
        }

    }
}