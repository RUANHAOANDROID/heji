package com.rh.heji.data.repository

import com.rh.heji.data.DataRepository
import com.rh.heji.data.db.Image

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