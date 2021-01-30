package com.rh.heji

import com.rh.heji.data.repository.BillRepository
import com.rh.heji.network.request.BillEntity
import com.rh.heji.ui.base.BaseViewModel

/**
 * Date: 2020/11/3
 * Author: 锅得铁
 * #
 */
class MainViewModel : BaseViewModel() {
    /**
     * 控制toolbar
     */
    var homeUUID: String? = null
    var reportUUID: String? = null
    var settingUUID: String? = null
    val billRepository: BillRepository by lazy { com.rh.heji.data.repository.BillRepository }

    fun asyncBillSave(billEntity: BillEntity) {
        launch({ billRepository.saveBill(billEntity) }, { it.printStackTrace() })
    }

    fun asyncBillUpdate(billEntity: BillEntity) {
        launch({ billRepository.updateBill(billEntity) }, { it.printStackTrace() })
    }

    fun asyncBillDelete(_id: String) {
        launch({ billRepository.deleteBill(_id) }, { it.printStackTrace() })
    }

    fun asyncBillPull(_id: String) {
        launch({ billRepository.pullBill() }, { it.printStackTrace() })
    }
}