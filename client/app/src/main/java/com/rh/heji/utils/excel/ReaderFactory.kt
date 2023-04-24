package com.rh.heji.utils.excel

import java.io.File

object ReaderFactory {

    fun getReader(fileName: String): IReader? {
        val suffix = fileName.split(".")
        if (suffix[1] == ("xls")) {
            return XLSFileReader()
        }
        if (suffix[1] == "csv") {
            return CSVFileReader()
        }
        return null
    }

    fun getReader(suffix: SUFFIX): IReader? {
        if (suffix == SUFFIX.XLS) {
            return XLSFileReader()
        }
        if (suffix == SUFFIX.CSV) {
            return CSVFileReader()
        }
        return null
    }
}

enum class SUFFIX {
    CSV, XLS
}