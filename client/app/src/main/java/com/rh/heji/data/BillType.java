package com.rh.heji.data;

/**
 * Date: 2020/11/20
 * Author: 锅得铁
 * #
 */
public enum BillType {

    INCOME(+1, "收入"),
    EXPENDITURE(-1, "支出");
    private int type;
    private String text;

    BillType(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public int type() {
        return type;
    }

    public String text() {
        return text;
    }

    public static BillType transform(int type) {
        if (type == BillType.INCOME.type()) {
            return BillType.INCOME;
        }else {
            return BillType.EXPENDITURE;
        }
//        else if (type == BillType.EXPENDITURE.type()) {
//            return BillType.EXPENDITURE;
//        }
//        return null;
    }

    public static BillType transform(String text) {
        if (text.equals(BillType.INCOME.text())) {
            return BillType.INCOME;
        } else if (text.equals(BillType.EXPENDITURE.text())) {
            return BillType.EXPENDITURE;
        }
        return null;
    }
}
