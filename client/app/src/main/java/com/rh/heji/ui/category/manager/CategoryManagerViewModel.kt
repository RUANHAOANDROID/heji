package com.rh.heji.ui.category.manager

import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.App
import com.rh.heji.Config
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.ui.base.BaseViewModel
import com.rh.heji.ui.category.manager.CategoryManagerAction.GetCategories
import com.rh.heji.utils.launchIO
import kotlinx.coroutines.flow.collect

/**
 * @date: 2020/10/11
 * @author: 锅得铁
 * # 分类
 */
internal class CategoryManagerViewModel :
    BaseViewModel<CategoryManagerAction, CategoryManagerUiState>() {
    private val categoryDao = App.dataBase.categoryDao()

    override fun doAction(action: CategoryManagerAction) {
        super.doAction(action)
        when (action) {
            is GetCategories -> {
                launchIO({
                    categoryDao.observeIncomeOrExpenditure(
                        Config.book.id,
                        action.type
                    ).collect {
                        send(CategoryManagerUiState.Categories(it))
                    }
                })
            }
            is CategoryManagerAction.DeleteCategory -> {
                deleteCategory(action.category)
            }
            is CategoryManagerAction.SaveCategory -> {
                saveCategory(action.name, action.type)
            }
        }
    }

    //保存标签
    private fun saveCategory(name: String, type: Int) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("您必须填写分类名称")
            return
        }
        launchIO({
            val category = Category(name = name, bookId = Config.book.id).apply {
                this.type = type
                level = 0
            }
            category.syncStatus = STATUS.NOT_SYNCED
            val exist = categoryDao.exist(category.hashCode())
            if (exist > 0) {
                ToastUtils.showShort("标签已经存在")
            } else {
                categoryDao.insert(category)
                ToastUtils.showShort("保存成功")
            }
        }, {
            ToastUtils.showLong(it.message)
        })
    }

    /**
     * 删除标签，通过Flow更新页面
     *
     * @param category
     */
    private fun deleteCategory(category: Category) {
        launchIO({
            category.syncStatus = STATUS.DELETED
            categoryDao.update(category)
            //deleteLiveData.postValue(true)
        }, {
            ToastUtils.showLong(it.message)
        })
    }

}