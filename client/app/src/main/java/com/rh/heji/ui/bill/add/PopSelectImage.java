package com.rh.heji.ui.bill.add;

import android.content.Context;
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
import com.rh.heji.ui.bill.add.adapter.BillPhotoAdapter;
import com.rh.heji.utlis.matisse.MatisseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Date: 2020/10/12
 * Author: 锅得铁
 * #
 */
public class PopSelectImage extends BottomPopupView {
    private int MAX_SELECT_COUNT = 3;
    private BillPhotoAdapter ticketAdpater = new BillPhotoAdapter();
    ;
    private MainActivity activity;
    RecyclerView selectImgRecycler;

    public interface OnDeleteClickListener {
        void async(List<String> urls);
    }

    private OnDeleteClickListener onDeleteClickListener;

    public PopSelectImage(@NonNull Context context, MainActivity activity) {
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
        return R.layout.pop_layout_select_ticket_image;
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

    public void clear() {
        ticketAdpater.getData().clear();
        ticketAdpater.notifyDataSetChanged();
    }
}
