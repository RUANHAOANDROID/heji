package com.rh.heji.ui.category;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Category;
import com.rh.heji.data.db.CategoryDao;

import java.util.List;

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 分类
 */
public class CategoryViewModule extends ViewModel {
    CategoryDao categoryDao = AppDatabase.getInstance().categoryDao();
    Category selectCategory;
    MediatorLiveData<Category> categoryLiveData = new MediatorLiveData<>();

    MediatorLiveData<List<Category>> incomeCategoryLiveData = new MediatorLiveData<>();
    MediatorLiveData<List<Category>> expenditureCategory = new MediatorLiveData<>();

    public CategoryViewModule() {
        /**
         * 始终保持单一的观察对象
         * source 其他来源的LiveData
         * observer 观察变化
         */
        incomeCategoryLiveData.addSource(categoryDao.findIncomeOrExpenditure(BillType.INCOME.type()), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> incomeCategories) {
                incomeCategoryLiveData.setValue(incomeCategories);
            }
        });
        expenditureCategory.addSource(categoryDao.findIncomeOrExpenditure(BillType.EXPENDITURE.type()), expenditureCategories -> {
            expenditureCategory.setValue(expenditureCategories);
        });
    }

    public LiveData<Category> getSelectCategory() {
        return categoryLiveData;
    }


    /**
     * 选中的标签
     *
     * @param selectCategory
     */
    public void setSelectCategory(Category selectCategory) {
        this.selectCategory = selectCategory;
        categoryLiveData.postValue(selectCategory);
    }

    /**
     * 获取收入标签
     *
     * @return
     */
    public LiveData<List<Category>> getIncomeCategory() {
        return incomeCategoryLiveData;
    }

    public LiveData<List<Category>> getExpenditureCategory() {
        return expenditureCategory;
    }
}
