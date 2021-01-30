package com.rh.heji.data.repository

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.Constants
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Constant
import com.rh.heji.network.BaseResponse
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.request.BillEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import top.zibin.luban.Luban
import java.io.File

class BillRepository {
    val hejiNetwork = HejiNetwork.getInstance()
    var billDao = AppDatabase.INSTANCE.billDao()
    val imgDao = AppDatabase.INSTANCE.imageDao()

    /**
     * 保存账单至Server
     */
    suspend fun pushBill(billEntity: BillEntity) {
        val response = hejiNetwork.billPush(billEntity)
        if (response.code == 0) {
            response.data.let {
                var bill = billEntity.toBill()
                bill.synced = Constant.STATUS_SYNCED
                billDao.update(Bill())
                uploadImage(bill.id)
            }
        }

    }


    suspend fun deleteBill(_id: String) {
        var response = hejiNetwork.billDelete(_id)
        response.data.let {
            billDao.delete(Bill(_id))
        }
    }

    suspend fun updateBill(billEntity: BillEntity) {
        var response = hejiNetwork.billUpdate(billEntity)
        response.data.let {
            val toBill = billEntity.toBill()
            toBill.synced = Constant.STATUS_SYNCED
            billDao.update(toBill)
        }
    }

    suspend fun pullBill(startTime: String = "0", endTime: String = "0") {
        var response = hejiNetwork.billPull(startTime, endTime)
        response.data.let {
            if (it.isNotEmpty()) {
                it.stream().forEach { entity ->
                    billDao.install(entity.toBill())
                }
            }
        }
    }

    /**
     * 上传账单图片
     */
    private suspend fun uploadImage(_id: String) {
        val images = imgDao.findByBillImgIdNotAsync(_id)
        if (images.isNotEmpty()) {
            images.forEach { image ->
                var img = File(image.localPath)
                val length = img.length()
                LogUtils.i("图片大小", length)
                if (length > Constants.FILE_LENGTH_1M * 2) { //图片超过设定值则压缩
                    LogUtils.i("图片大小超过2M,压缩图片", Constants.FILE_LENGTH_1M * 2)
                    val fileList = Luban.with(App.getContext()).load(img).get()
                    if (!fileList.isEmpty() && fileList.size > 0) {
                        img = fileList[0]
                    }
                }
                val fileName = img.name
                val requestBody = RequestBody.create("image/png".toMediaTypeOrNull(), img)
                val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", fileName, requestBody)
                val time = img.lastModified()
                val response: BaseResponse<String> = hejiNetwork.billImageUpload(part, _id, time)
                response.data.let { imgUUID ->
                    image.onlinePath = imgUUID
                    image.synced = Constant.STATUS_SYNCED
                    AppDatabase.getInstance().imageDao().update(image)
                    LogUtils.d("图片上传成功：", imgUUID)
                }

            }
        }
    }

}