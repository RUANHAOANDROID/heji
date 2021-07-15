package com.rh.heji.ui.bill.category

import android.text.TextUtils
import androidx.lifecycle.LiveData

import androidx.lifecycle.MediatorLiveData
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Constant
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.utlis.launchIO

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 分类
 */
class CategoryViewModule : BaseViewModel() {
    private val categoryDao by lazy{   AppDatabase.getInstance().categoryDao()}
    var type: BillType = BillType.EXPENDITURE
        set(value) {
            field = value
            typeLiveData.postValue(value)
        }

    private val typeLiveData: MediatorLiveData<BillType> = MediatorLiveData<BillType>()

    fun getCategoryType(): LiveData<BillType> {
        return typeLiveData
    }


    var selectCategory = Category(category = "管理")
        set(value) {
            if (value.category =="管理") return
            field = value//field 为type本身 (field领域)
            selectCategoryLiveData.postValue(value)
        }
    private val selectCategoryLiveData = MediatorLiveData<Category>()

    fun getSelectCategory(): LiveData<Category> {
        return selectCategoryLiveData
    }

    val incomeCategory by lazy { MediatorLiveData<MutableList<Category>>() }

    val expenditureCategory by lazy { MediatorLiveData<MutableList<Category>>() }


    init {
        /**
         * 始终保持单一的观察对象
         * source 其他来源的LiveData
         * observer 观察变化
         */
        incomeCategory.addSource(
            categoryDao.findIncomeOrExpenditure(BillType.INCOME.type())
        ) { incomeCategories ->
            incomeCategory.value = incomeCategories
        }
        expenditureCategory.addSource(
            categoryDao
                .findIncomeOrExpenditure(BillType.EXPENDITURE.type())
        ) { expenditureCategories: MutableList<Category> ->
            expenditureCategory.setValue(
                expenditureCategories
            )
        }
    }

    fun saveCategory(name: String, type: Int) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("您必须填写分类名称")
            return
        }
        launchIO({
            val category = Category(category = name,type = type,level = 0)
            category.synced = Constant.STATUS_NOT_SYNC
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

    private val deleteLiveData = MediatorLiveData<Boolean>()
    fun deleteCategory(category: Category): LiveData<Boolean> {

        launchIO({
            category.synced = Constant.STATUS_DELETE
            categoryDao.update(category)
            deleteLiveData.postValue(true)
        }, {})
        return deleteLiveData
    }

}