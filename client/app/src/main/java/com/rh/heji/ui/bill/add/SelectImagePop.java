package com.rh.heji.ui.bill.add;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.core.BottomPopupView;
import com.matisse.entity.ConstValue;
import com.rh.heji.MainActivity;
import com.rh.heji.R;
import com.rh.heji.ui.bill.add.adapter.TicketPhotoAdapter;
import com.unistrong.mapoffline.utils.matisse.MatisseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Date: 2020/10/12
 * Author: 锅得铁
 * #
 */
public class SelectImagePop extends BottomPopupView {
    private int MAX_SELECT_COUNT = 3;
    private TicketPhotoAdapter  ticketAdpater = new TicketPhotoAdapter();;
    private MainActivity activity;
    RecyclerView selectImgRecycler;

    public interface OnDeleteClickListener {
        void async(List<String> urls);
    }

    private OnDeleteClickListener onDeleteClickListener;

    public SelectImagePop(@NonNull Context context, MainActivity activity) {
        super(context);
        this.activity = activity;
    }

    private View getFooterView(View.OnClickListener listener) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.footer_add, selectImgRecycler, false);
        view.setOnClickListener(listener);
        return view;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_select_ticket_image;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        selectImgRecycler = findViewById(R.id.selectImgRecycler);
        selectImgRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //selectImgRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //selectImgRecycler.addItemDecoration(new GridSpaceItemDecoration(3, 10,10));

        ticketAdpater.addFooterView(getFooterView(v -> {
            int count = MAX_SELECT_COUNT - ticketAdpater.getData().size();
            if (count <= 0) {
                ToastUtils.showLong("最多只能添加" + MAX_SELECT_COUNT + "张照片");
                return;
            }
            MatisseUtils.INSTANCE.selectMultipleImage(activity, ConstValue.REQUEST_CODE_CHOOSE, count);
        }));
        selectImgRecycler.setAdapter(ticketAdpater);

        final OnItemChildClickListener listener = (adapter, view, position) -> {
            if (view.getId() == R.id.imgDelete) {
                ticketAdpater.removeAt(position);
                if (onDeleteClickListener != null) {
                    List<String> urls = new ArrayList<>();
                    adapter.getData().stream().forEach(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            urls.add(o.toString());
                        }
                    });
                    onDeleteClickListener.async(urls);
                }
            }
        };
        ticketAdpater.setOnItemChildClickListener(listener);

    }

    public void setDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setData(List<String> imgUrls) {
        if (!imgUrls.isEmpty()) {
            if (null != ticketAdpater) {
                ticketAdpater.setNewInstance(imgUrls);
                ticketAdpater.notifyDataSetChanged();
            }
        }
    }

    /**
     * 描述 : RecyclerView GridLayoutManager 等间距。
     * <p>
     * 等间距需满足两个条件：
     * 1.各个模块的大小相等，即 各列的left+right 值相等；
     * 2.各列的间距相等，即 前列的right + 后列的left = 列间距；
     * <p>
     * 在{@link #getItemOffsets(Rect, View, RecyclerView, RecyclerView.State)} 中针对 outRect 的left 和right 满足这两个条件即可
     * <p>
     * 作者 : shiguotao
     * 版本 : V1
     * 创建时间 : 2020/3/19 4:54 PM
     */
    public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final String TAG = "GridSpaceItemDecoration";

        private int mSpanCount;//横条目数量
        private int mRowSpacing;//行间距
        private int mColumnSpacing;// 列间距

        /**
         * @param spanCount     列数
         * @param rowSpacing    行间距
         * @param columnSpacing 列间距
         */
        public GridSpaceItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
            this.mSpanCount = spanCount;
            this.mRowSpacing = rowSpacing;
            this.mColumnSpacing = columnSpacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // 获取view 在adapter中的位置。
            int column = position % mSpanCount; // view 所在的列

            outRect.left = column * mColumnSpacing / mSpanCount; // column * (列间距 * (1f / 列数))
            outRect.right = mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount; // 列间距 - (column + 1) * (列间距 * (1f /列数))

            Log.e(TAG, "position:" + position
                    + "    columnIndex: " + column
                    + "    left,right ->" + outRect.left + "," + outRect.right);

            // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
            if (position >= mSpanCount) {
                outRect.top = mRowSpacing; // item top
            }
        }
    }

}
