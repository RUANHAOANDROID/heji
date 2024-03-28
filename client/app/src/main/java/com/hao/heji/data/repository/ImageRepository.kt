package com.hao.heji.data.repository

import com.hao.heji.data.DataRepository

/**
 * @date: 2022/4/2
 * @author: 锅得铁
 * #
 */
class ImageRepository : DataRepository() {
    fun preDelete(imageID: String) {
        imgDao.preDelete(imageID = imageID)
    }
}