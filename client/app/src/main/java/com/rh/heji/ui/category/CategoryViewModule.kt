package com.rh.heji.ui.category

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.CategoryDao
import com.rh.heji.data.db.Constant
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.data.repository.CategoryRepository

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 分类
 */
class CategoryViewModule : ViewModel() {
    var type: BillType = BillType.EXPENDITURE

    var categoryRepository = CategoryRepository()
    var categoryDao: CategoryDao = AppDatabase.getInstance().categoryDao()

    var selectCategory = Category(ObjectId().toString())
        set(value) {
            field = value//field 为type本身 (field领域)
            selectCategoryLiveData.postValue(value)
        }
    var selectCategoryLiveData = MediatorLiveData<Category>();

    var incomeCategoryLiveData = MediatorLiveData<List<Category>>()
    var expenditureCategory = MediatorLiveData<List<Category>>()


    init {
        /**
         * 始终保持单一的观察对象
         * source 其他来源的LiveData
         * observer 观察变化
         */
        incomeCategoryLiveData.addSource(categoryDao.findIncomeOrExpenditure(BillType.INCOME.type())) { incomeCategories -> incomeCategoryLiveData.value = incomeCategories }
        expenditureCategory.addSource(categoryDao.findIncomeOrExpenditure(BillType.EXPENDITURE.type())) { expenditureCategories: List<Category> -> expenditureCategory.setValue(expenditureCategories) }
    }


    /**
     * 获取收入标签
     *
     * @return
     */
    val incomeCategory: LiveData<List<Category>>
        get() = incomeCategoryLiveData

    fun saveCategory(name: String?, type: Int) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("您必须填写分类名称")
            return
        }
        val category = Category(ObjectId().toString())
        category.type = type
        category.category = name!!
        category.level = 0
        category.synced = Constant.STATUS_NOT_SYNC
        val categories = AppDatabase.getInstance().categoryDao().findByNameAndType(name, type)
        if (categories != null && categories.size > 0) {
            val _id = categories[0]._id
            category._id = _id
            AppDatabase.getInstance().categoryDao().update(category)
        }
        AppDatabase.getInstance().categoryDao().insert(category)
        ToastUtils.showShort("保存成功")
    }
}