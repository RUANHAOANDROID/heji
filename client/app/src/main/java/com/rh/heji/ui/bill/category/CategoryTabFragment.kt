package com.rh.heji.ui.bill.category

import android.content.Context
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentCategoryTabBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.base.FragmentViewPagerAdapter
import com.rh.heji.ui.bill.create.CreateBillFragment


/**
 * 标签 TAB 包含了收入和支出
 * @date: 2020/10/11
 * @author: 锅得铁
 *
 */
class CategoryTabFragment : BaseFragment() {

    private val tabTitles = listOf(BillType.EXPENDITURE.text(), BillType.INCOME.text())
    lateinit var binding: FragmentCategoryTabBinding
    private var currentType: BillType = BillType.EXPENDITURE

    private lateinit var mSelectedCategoryListener: ISelectedCategory

    private lateinit var addBillFragment: CreateBillFragment

    val categoryFragments = listOf(
        CategoryFragment.newInstance(BillType.EXPENDITURE),
        CategoryFragment.newInstance(BillType.INCOME)
    )

    init {
        categoryFragments.isNotEmpty()
    }

    lateinit var categoryViewModel: CategoryViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        addBillFragment = (parentFragment as CreateBillFragment)
    }


    override fun initView(view: View) {
        binding = FragmentCategoryTabBinding.bind(view)
        showPager()
        //从父Fragment拿到ViewModule
        categoryViewModel = addBillFragment.categoryViewModel
        mSelectedCategoryListener = addBillFragment
    }

    override fun layoutId(): Int {
        return R.layout.fragment_category_tab
    }

    private fun showPager() {

        val pagerAdapter = FragmentViewPagerAdapter(
            childFragmentManager,
            categoryFragments,
            tabTitles
        )

        binding.vpContent.apply {
            adapter = pagerAdapter
            //TabLayout+ViewPager联动 1
            addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tab))
        }
        binding.tab.apply {
            setupWithViewPager(binding.vpContent)
            //TabLayout+ViewPager联动 2
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.vpContent))
            getTabAt(0)!!.select()
            //mSelectedCategoryListener.selected(categoryFragments[0].getSelectedCategory()!!)//默认支出
            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabSelected", tab.position)
                    val selectType = BillType.transform(tab.text.toString())
                    currentType = selectType
                    val categoryFragment = categoryFragments[tab.position]
                    categoryFragment.rootView.post {
                        val selectedCategory = categoryFragment.getSelectedCategory()
                        mSelectedCategoryListener.selected(selectedCategory!!)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabUnselected", tab.position)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabReselected", tab.position)
                }
            })
        }
    }

    fun setIndex(index: Int = 0) {
        binding.tab.getTabAt(0)!!.select()
    }

    /**
     * 修改时预先选中类别
     *
     * @param category
     * @param type
     */
    fun setSelectCategory(category: String, type: Int) {
        //内容页绘制完成后选中类别
        binding.vpContent.post {
            if (type == BillType.EXPENDITURE.type()) {
                binding.tab.getTabAt(0)?.select()
                categoryFragments[0].setSelectCategory(category)
            } else if (type == BillType.INCOME.type()) {
                binding.tab.getTabAt(1)?.select()
                categoryFragments[1].setSelectCategory(category)
            }
        }
    }

    fun getSelectCategory(): Category {
        return if (currentType == BillType.EXPENDITURE)
            categoryFragments[0].getSelectedCategory()
        else categoryFragments[1].getSelectedCategory()
    }
}