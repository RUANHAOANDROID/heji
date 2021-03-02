package com.rh.heji.data.repository

import android.text.TextUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.Constant
import com.rh.heji.network.BaseResponse
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.CategoryEntity
import retrofit2.Response
import java.io.IOException

class CategoryRepository {
    val network = HejiNetwork.getInstance()
    val categoryDao = AppDatabase.getInstance().categoryDao()
    suspend fun pushCategory(category: CategoryEntity) {
        val response = network.categoryPush(category)
        response.let {
            val dbCategory = category.toDbCategory()
            dbCategory?.synced = Constant.STATUS_SYNCED
            categoryDao.update(dbCategory)
        }
    }

    suspend fun deleteCategory(_id: String) {
        val response = network.categoryDelete(_id)
        response.let {
            var category = Category(_id)
            categoryDao.delete(category)
        }
    }

    suspend fun pullCategory() {
        val response: BaseResponse<List<CategoryEntity>> = network.categoryPull()
        val categories = response.data
        if (categories.isNotEmpty()) {
            categories.stream().forEach { entity: CategoryEntity ->
                val _id = AppDatabase.getInstance().categoryDao().findCategoryID(entity._id)
                if (TextUtils.isEmpty(_id)) {
                    val dbCategory = entity.toDbCategory()
                    dbCategory!!.synced = Constant.STATUS_SYNCED
                    AppDatabase.getInstance().categoryDao().insert(dbCategory)
                }
            }
        }
    }
}