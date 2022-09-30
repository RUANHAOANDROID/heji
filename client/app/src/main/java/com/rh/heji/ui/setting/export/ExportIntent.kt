package com.rh.heji.ui.setting.export

import com.rh.heji.ui.base.IAction
import com.rh.heji.ui.base.IUiState

/**
 *
 * @date 2022/10/1
 * @author 锅得铁
 * @since v1.0
 */
sealed class ExportUiState : IUiState {
    class Success(val path: String) : ExportUiState()
    class Error(val t: Throwable) : ExportUiState()
}

sealed class ExportAction : IAction {
    class ExportExcel(val fileName: String) : ExportAction()
}