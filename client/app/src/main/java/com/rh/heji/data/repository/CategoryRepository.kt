package com.rh.heji.data.repository

import android.text.TextUtils
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.BaseResponse
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.CategoryEntity
import retrofit2.Response
import java.io.IOException

class CategoryRepository {
    val network = HejiNetwork.getInstance()
    private val categoryDao =   AppDatabase.getInstance().categoryDao()
    suspend fun pushCategory(category: CategoryEntity) {
        val response = network.categoryPush(category)
        response.let {
            val dbCategory = category.toDbCategory()
            dbCategory?.synced = STATUS.SYNCED
            categoryDao.update(dbCategory)
        }
    }

    suspend fun deleteCategory(_id: String) {
        val response = network.categoryDelete(_id)
        response.let {
            categoryDao.deleteById(_id)
        }
    }

    suspend fun pullCategory() {
        val response: BaseResponse<List<CategoryEntity>> = network.categoryPull()
        val categories = response.date
        if (categories.isNotEmpty()) {
            categories.forEach { entity: CategoryEntity ->
                val _id =   AppDatabase.getInstance().categoryDao().findByID(entity.id)
                if (TextUtils.isEmpty(_id)) {
                    val dbCategory = entity.toDbCategory()
                    dbCategory!!.synced = STATUS.SYNCED
                      AppDatabase.getInstance().categoryDao().insert(dbCategory)
                }
            }
        }
    }
}