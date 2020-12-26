package com.rh.heji.ui.home.adapter;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.R;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public class ItemDateEntity extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return BillInfoAdapter.ITEM_DATE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_date;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {

    }
}
