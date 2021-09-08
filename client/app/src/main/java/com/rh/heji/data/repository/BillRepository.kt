package com.rh.heji.data.repository

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.AppViewModule
import com.rh.heji.FILE_LENGTH_1M
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.DataRepository
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.STATUS
import com.rh.heji.network.BaseResponse
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.response.ImageEntity
import com.rh.heji.utlis.launch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import top.zibin.luban.Luban
import java.io.File

class BillRepository : DataRepository() {

    suspend fun deleteBill(_id: String) {
        var response = network.billDelete(_id)
        response.data.let {
            AppDatabase.getInstance().imageDao().deleteBillImage(_id)
            billDao.delete(Bill(_id))
        }
    }

    suspend fun updateBill(bill: Bill) {
        var response = network.billUpdate(bill)
        response.data.let {
            bill.synced = STATUS.SYNCED
            billDao.update(bill) //已上传
        }
    }

    suspend fun pullBill(startTime: String = "0", endTime: String = "0") {
        var response = network.billPull(startTime, endTime)
        response.data.let {
            if (it.isNotEmpty()) {
                it.forEach { bill ->
                    billDao.install(bill)
                }
            }
        }
    }

    /**
     * 上传账单图片
     */
    private suspend fun uploadImage(bill_id: String) {
        val images = imgDao.findByBillIdNotAsync(bill_id)
        if (images.isNotEmpty()) {
            images.forEach { image ->
                var imgFile = File(image.localPath)
                val length = imgFile.length()
                LogUtils.d("图片大小", length)
                if (length > FILE_LENGTH_1M * 3) { //图片超过设定值则压缩
                    LogUtils.d("图片大小超过2M,压缩图片", FILE_LENGTH_1M * 3)
                    val fileList = Luban.with(App.context()).load(imgFile).get()
                    if (fileList.isNotEmpty() && fileList.size > 0) {
                        imgFile = fileList[0]
                    }
                }
                val fileName = imgFile.name
                val requestBody = imgFile.asRequestBody("image/png".toMediaTypeOrNull())
                val part: MultipartBody.Part =
                    MultipartBody.Part.createFormData("file", fileName, requestBody)
                val time = imgFile.lastModified()
                val objectId = image.id
                val response: BaseResponse<ImageEntity> = network.imageUpload(
                    part,
                    objectId, bill_id, time
                )
                response.data.let {
                    image.onlinePath = response.data._id
                    image.md5 = response.data.md5
                    image.id = response.data._id
                    image.synced = STATUS.SYNCED
                    LogUtils.d("账单图片上传成功：$image")
                    image.onlinePath?.let {
                        var count = AppDatabase.getInstance().imageDao()
                            .updateOnlinePath(image.id, it, image.synced)
                        if (count > 0)
                            LogUtils.d("图片更新成功：$image")
                    }


                }

            }
        }
    }

    /**
     * 添加账单，保存到数据库就算成功，同步交给AppViewModule
     */
    suspend fun addBill(bill: Bill, images: MutableList<Image> = mutableListOf()): Flow<Long> {
        return flow {
            val bid = bill.id//避免发射后UI重置ID
            var count = billDao.install(bill)
            if (count > 0){
                emit(count)//--发射
                bill.id=bid
                AppViewModule.get().launch({
                    network.billPush(bill).let {
                        if (it.code == OK) {
                            bill.synced = STATUS.SYNCED
                            billDao.update(bill)
                        }
                    }

                })
            }
            if (images.size>0){
                AppDatabase.getInstance().imageDao().install(images)
                uploadImage(bid)
            }


        }.flowOn(Dispatchers.IO)
    }
}