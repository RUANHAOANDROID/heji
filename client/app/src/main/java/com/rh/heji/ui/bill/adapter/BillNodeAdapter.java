package com.rh.heji.ui.bill.adapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BillNodeAdapter extends BaseNodeAdapter {
    /**
     * 自行根据数据、位置等信息，返回 item 类型
     */
    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> data, int position) {
        BaseNode node = data.get(position);
        if (node instanceof DayIncomeNode) {
            return 0;
        } else if (node instanceof DayBillsNode) {
            return 1;
        }
        return -1;
    }
}
