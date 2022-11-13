package com.rh.heji.ui.category.manager

import com.rh.heji.data.db.Category
import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState

/**
 *@date: 2022/10/15
 *Author: 锅得铁
 *#
 */

internal sealed interface CategoryManagerUiState : IUiState {
    class Categories(val data: MutableList<Category>) : CategoryManagerUiState
}


internal sealed interface CategoryManagerAction : IAction {
    class GetCategories(val type: Int) : CategoryManagerAction
    class DeleteCategory(val category: Category) : CategoryManagerAction
    class SaveCategory(val name: String, val type: Int) : CategoryManagerAction
}