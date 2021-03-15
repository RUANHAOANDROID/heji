package com.rh.heji.widget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.util.XPopupUtils;
import com.rh.heji.R;

import static com.rh.heji.ui.bill.adapter.BillNodeProviderKt.TYPE_TITLE;

public class CardDecoration extends RecyclerView.ItemDecoration {

    private void drawCardBackground(@NonNull Canvas c, @NonNull RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);// Item View
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewAdapterPosition();
            if (parent.getAdapter().getItemViewType(position) == TYPE_TITLE) {
                child.setBackground(XPopupUtils.createDrawable(parent.getResources().getColor(R.color._xpopup_light_color), 15, 15, 0, 0));
            } else if (isLastInItemList(parent, position) || isLastItem(parent, position)) {
                child.setBackground(XPopupUtils.createDrawable(parent.getResources().getColor(R.color._xpopup_light_color), 0, 0, 15, 15));
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView
            parent, @NonNull RecyclerView.State state) {
        //super.onDrawOver(c, parent, state);
        drawCardBackground(c, parent);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        Resources resources = parent.getContext().getResources();

        int padding = getPadding8dp(resources);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int position = params.getViewAdapterPosition();
        int viewType = parent.getAdapter().getItemViewType(position);

        if (viewType == TYPE_TITLE) {
            // header
            outRect.set(0, (int) (padding / 2), 0, 0);
        } else {
            if (isLastInItemList(parent, position) || isLastItem(parent, position)) {
                // last item before next header
                outRect.set(0, 0, 0, (int) (padding / 2));
            }
        }
//        outRect.inset((int) size16dp, 0);
        outRect.left = (int) padding;
        outRect.right = (int) padding;
        if (isLastItem(parent, position)) {//最后一个
            outRect.bottom = (int) padding / 2;
        }
    }

    private boolean isLastItem(RecyclerView parent, int position) {
        return position == parent.getAdapter().getItemCount() - 1;
    }

    private boolean isLastInItemList(RecyclerView parent, int position) {
        return parent.getAdapter().getItemViewType(position + 1) == TYPE_TITLE;
    }

    private int getPadding8dp(Resources resources) {
        float size16dp = 8f;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size16dp, resources.getDisplayMetrics());
    }
}
