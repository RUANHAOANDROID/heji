package com.rh.heji.data.repository

import com.rh.heji.data.DataRepository
import com.rh.heji.data.db.Image

/**
 * Date: 2022/4/2
 * Author: 锅得铁
 * #
 */
class ImageRepository : DataRepository() {
    fun preDelete(imageID: String) {
        imgDao.preDelete(imageID = imageID)
    }
}