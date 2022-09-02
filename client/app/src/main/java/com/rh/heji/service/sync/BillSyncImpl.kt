package com.rh.heji.service.sync

import com.blankj.utilcode.util.LogUtils
import com.rh.heji.App
import com.rh.heji.FILE_LENGTH_1M
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Book
import com.rh.heji.data.db.Image
import com.rh.heji.data.db.STATUS
import com.rh.heji.launchIO
import com.rh.heji.network.BaseResponse
import com.rh.heji.network.HejiNetwork
import com.rh.heji.network.response.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import top.zibin.luban.Luban
import java.io.File

/**
 * 账单同步实现
 * @date 2022/6/20
 * @author 锅得铁
 * @since v1.0
 */
class BillSyncImpl(private val scope: CoroutineScope) : IBillSync {
    override fun compare() {

    }

    override fun getBills(year: String) {
        TODO("Not yet implemented")
    }

    override fun delete(billID: String) {
        LogUtils.d("sync bill delete", billID)
        scope.launchIO({
            val response = HejiNetwork.getInstance().billDelete(billID)
            if (response.success()) {
                App.dataBase.billDao().deleteById(response.data)
            }
        })
    }

    override fun add(bill: Bill) {
        scope.launchIO({
            val response = HejiNetwork.getInstance().billPush(bill)
            if (response.success()) {
                App.dataBase.billDao().update(bill.apply {
                    synced = STATUS.SYNCED
                })
                uploadImage(bill.id)
            }
        })
    }

    override fun update(bill: Bill) {
        scope.launchIO({
            val response = HejiNetwork.getInstance()
                .billUpdate(bill)
            if (response.success()) {
                App.dataBase.billDao().update(bill.apply {
                    synced = STATUS.SYNCED
                })
                uploadImage(bill.id)
            }
        })
    }

    /**
     * 上传账单图片
     */
    private suspend fun uploadImage(bid: String) {
        val images = App.dataBase.imageDao().findByBillIdNotAsync(bid)
        if (images.isNotEmpty()) {
            images.forEach { image ->
                var imgFile = File(image.localPath)
                val length = imgFile.length()
                LogUtils.d("图片大小", length)
                if (length > FILE_LENGTH_1M * 3) { //图片超过设定值则压缩
                    LogUtils.d("图片大小超过2M,压缩图片", FILE_LENGTH_1M * 3)
                    val fileList = Luban.with(App.context).load(imgFile).get()
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
                val response: BaseResponse<ImageEntity> = HejiNetwork.getInstance().imageUpload(
                    part,
                    objectId, bid, time
                )
                response.data.let {
                    image.onlinePath = response.data._id
                    image.md5 = response.data.md5
                    image.id = response.data._id
                    image.synced = STATUS.SYNCED
                    LogUtils.d("账单图片上传成功：$image")
                    image.onlinePath?.let {
                        var count = App.dataBase.imageDao()
                            .updateOnlinePath(image.id, it, image.synced)
                        if (count > 0)
                            LogUtils.d("图片更新成功：$image")
                    }
                }

            }
        }
    }
}