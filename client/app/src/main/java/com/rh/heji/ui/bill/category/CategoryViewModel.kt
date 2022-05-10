package com.rh.heji.ui.bill.category

import android.text.TextUtils
import androidx.lifecycle.LiveData

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils

import com.rh.heji.App
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO

/**
 * Date: 2020/10/11
 * @author: 锅得铁
 * # 分类
 */
class CategoryViewModel : BaseViewModel() {
    private val categoryDao = App.dataBase.categoryDao()

    //支出标签
    private val expenditureCategory = MediatorLiveData<MutableList<Category>>()

    //收入标签
    private val incomeCategory = MediatorLiveData<MutableList<Category>>()


    private val deleteLiveData = MediatorLiveData<Boolean>()

    init {
        /**
         * 始终保持单一的观察对象
         * source 其他来源的LiveData
         * observer 观察变化
         */
        incomeCategory.addSource(
            categoryDao.findIncomeOrExpenditure(App.currentBook!!.id, BillType.INCOME.type())
                .asLiveData(viewModelScope.coroutineContext)
        ) { incomeCategories ->
            incomeCategory.value = incomeCategories.apply {
                add(size, Category(category = "其他"))
            }
        }
        expenditureCategory.addSource(
            categoryDao
                .findIncomeOrExpenditure(App.currentBook!!.id, BillType.EXPENDITURE.type())
                .asLiveData(viewModelScope.coroutineContext)
        ) { expenditureCategories: MutableList<Category> ->
            expenditureCategory.value = expenditureCategories.apply {
                add(size, Category(category = "其他"))
            }
        }
    }

    fun getExpenditureCategory(): LiveData<MutableList<Category>> {
        return expenditureCategory
    }

    fun getIncomeCategory(): LiveData<MutableList<Category>> {
        return incomeCategory
    }

    //---------------------Category manager--------------------
    fun saveCategory(name: String, type: Int) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("您必须填写分类名称")
            return
        }
        launchIO({
            val category = Category(category = name, type = type, level = 0)
            category.synced = STATUS.NOT_SYNCED
            val categories = categoryDao.findByNameAndType(name, type)
            if (categories.size > 0) {
                val _id = categories[0].id
                category.id = _id
                categoryDao.update(category)
            }
            categoryDao.insert(category)
            ToastUtils.showShort("保存成功")
        }, {})
    }


    fun deleteCategory(category: Category): LiveData<Boolean> {
        launchIO({
            category.synced = STATUS.DELETED
            categoryDao.update(category)
            deleteLiveData.postValue(true)
        }, {})
        return deleteLiveData
    }

}