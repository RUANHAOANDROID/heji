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
        fun transform(type: Int): BillType {
            if (type == INCOME.valueInt()) {
                return INCOME
            }
            return if (type == EXPENDITURE.valueInt()) {
                EXPENDITURE
            } else {
                ALL
            }
        }

        fun transform(text: String): BillType {
            if (text == INCOME.valueString()) {
                return INCOME
            } else if (text == EXPENDITURE.valueString()) {
                return EXPENDITURE
            }
            return ALL
        }
    }

    override fun toString(): String {
        return "BillType(name=$name,type=$type,text=$text,ordinal=$ordinal)"
    }
}