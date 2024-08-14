package com.hao.heji.sync.impl

import com.blankj.utilcode.util.LogUtils
import com.hao.heji.App
import com.hao.heji.FILE_LENGTH_1M
import com.hao.heji.data.db.STATUS
import com.hao.heji.network.BaseResponse
import com.hao.heji.network.HttpManager
import com.hao.heji.network.response.ImageEntity
import kotlinx.coroutines.CoroutineScope
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
class BillSyncImpl(private val scope: CoroutineScope)  {
    /**
     * 上传账单图片
     */
    private suspend fun uploadImage(bid: String) {
        val images = App.dataBase.imageDao().findByBillID(bid, STATUS.NEW)
        if (images.isNotEmpty()) {
            images.forEach { image ->
                var imgFile = File(image.localPath)
                val length = imgFile.length()
                LogUtils.d("图片大小", length)
                if (length > FILE_LENGTH_1M * 3) { //图片超过设定值则压缩
                    LogUtils.d("图片大小超过3M,压缩图片", FILE_LENGTH_1M * 3)
                    val fileList = Luban.with(App.context).load(imgFile).get()
                    if (fileList.isNotEmpty() && fileList.size > 0) {
                        imgFile = fileList[0]
                    }
                }
                val requestBody = imgFile.asRequestBody("image/png".toMediaTypeOrNull())
                val part: MultipartBody.Part =
                    MultipartBody.Part.createFormData("file", imgFile.name, requestBody)
                val time = imgFile.lastModified()
                val objectId = image.id
                val response: BaseResponse<ImageEntity> = HttpManager.getInstance().imageUpload(
                    part,
                    objectId, bid, time
                )
                response.data?.let {
                    image.onlinePath = it._id
                    image.md5 =it.md5
                    image.id = it._id
                    image.syncStatus = STATUS.SYNCED
                    LogUtils.d("账单图片上传成功：$image")
                    image.onlinePath?.let {
                        var count = App.dataBase.imageDao()
                            .updateOnlinePath(image.id, it, image.syncStatus)
                        if (count > 0)
                            LogUtils.d("图片更新成功：$image")
                    }
                }

            }
        }
    }
}