package com.rh.heji.ui.home.adapter;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
public class ItemInfoEntity extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return BillInfoAdapter.ITEM_INFO;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {

    }
}
