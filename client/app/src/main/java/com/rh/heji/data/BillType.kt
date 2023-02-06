package com.rh.heji.data

/**
 * @date: 2020/11/20
 * @author: 锅得铁
 * #
 */
enum class BillType(private val type: Int, private val text: String) {
    INCOME(+1, "收入"), EXPENDITURE(-1, "支出"), ALL(0, "收支");

    fun valueInt(): Int = type
    fun valueString(): String = text

    companion object {
        fun transform(type: Int) = when (type) {
            INCOME.type -> INCOME
            EXPENDITURE.type -> EXPENDITURE
            else -> ALL
        }
        fun transform(text: String) = when (text) {
            INCOME.text -> INCOME
            EXPENDITURE.text -> EXPENDITURE
            else -> ALL
        }
    }

    override fun toString(): String {
        return "BillType(name=$name,type=$type,text=$text,ordinal=$ordinal)"
    }
}