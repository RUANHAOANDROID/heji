package com.rh.heji.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.DebouncingUtils;
import com.blankj.utilcode.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.rh.heji.data.db.Image;
import com.rh.heji.databinding.FragmentBillsHomeBinding;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.ui.bill.adapter.BillInfoAdapter;
import com.rh.heji.ui.bill.Iteminfo.BillInfoPop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rh.heji.Constants.BACKGROUND_ALPHA;

public class BillsHomeFragment extends BaseFragment {
    FragmentBillsHomeBinding binding;
    private BillsHomeViewModel homeViewModel;
    BillInfoAdapter adapter;
    private String homeUUID;
    //最后点击时间
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeUUID = UUID.randomUUID().toString();
        getMainActivity().getMainViewModel().setHomeUUID(homeUUID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(layoutId(), container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        homeViewModel = getActivityViewModel(BillsHomeViewModel.class);
        binding = FragmentBillsHomeBinding.bind(view);
        initBillsAdapter();
    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_bills_home;
    }

    @Override
    public void onResume() {
        getMainActivity().getToolbar().setVisibility(View.VISIBLE);
        getMainActivity().getToolbar().getMenu().setGroupVisible(R.id.menu_save, false);
        getMainActivity().getToolbar().getMenu().setGroupVisible(R.id.menu_settings, false);
        getMainActivity().getToolbar().getMenu().setGroupVisible(R.id.menu_settings, false);
        getMainActivity().setYearMonthItemVisible(true);
        super.onResume();
        int thisYear = homeViewModel.getYear();
        int thisMonth = homeViewModel.getMonth();
        notifyData(thisYear, thisMonth);
    }

    /**
     * 汇总收支
     *
     * @param year  年
     * @param month 月
     */
    private void totalExpenseAndIncome(int year, int month) {
        homeViewModel.getIncomesOrExpenses(year, month, BillType.INCOME.type()).observe(getMainActivity(), data -> {
            String incomes = "0";
            if (null != data) {
                incomes = String.valueOf(data / 100);
            }
            binding.homeHeadView.tvTotalIncomeValue.setText(incomes);
            refreshHeadView();

        });
        homeViewModel.getIncomesOrExpenses(year, month, BillType.EXPENDITURE.type()).observe(getMainActivity(), data -> {
            String expenses = "0";
            if (null != data) {
                expenses = String.valueOf(data / 100);
            }
            binding.homeHeadView.tvTotalExpensesValue.setText(expenses);
            refreshHeadView();
        });
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMainActivity().setYearMonthItemVisible(true);
    }

    /**
     * 刷新头部收支
     */
    private void refreshHeadView() {
        BigDecimal income = new BigDecimal(binding.homeHeadView.tvTotalIncomeValue.getText().toString());
        BigDecimal expenses = new BigDecimal(binding.homeHeadView.tvTotalExpensesValue.getText().toString());
        BigDecimal totalRevenue = income.subtract(expenses);
        if (totalRevenue.longValue() > 0) {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(Color.GREEN);
        } else {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(Color.RED);
        }
        binding.homeHeadView.tvTotalRevenueValue.setText(totalRevenue.toString());
    }

    /**
     * 初始化账单列表适配器
     */
    private void initBillsAdapter() {
        adapter = new BillInfoAdapter();
        binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity()));
        binding.homeRecycler.setAdapter(adapter);
        binding.homeRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 8;
                outRect.right = 8;
                outRect.top = 8;
                outRect.bottom = 8;
            }
        });
        binding.materialupAppBar.getBackground().setAlpha(BACKGROUND_ALPHA);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                Bill billTab = (Bill) adapter.getItem(position);
                showBillItemPop(billTab);
                lastClickTime = System.currentTimeMillis();
            }

        });
        //差异化比对更新
        adapter.setDiffCallback(new DiffUtil.ItemCallback<Bill>() {
            @Override
            public boolean areItemsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
                return oldItem.id.equals(newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
                return oldItem.id.equals(newItem.id);
            }
        });
    }


    /**
     * 显示单条账单
     *
     * @param billTab
     */
    private void showBillItemPop(Bill billTab) {
        BillInfoPop popupView = new BillInfoPop(getContext());
        new XPopup.Builder(getContext())
                //.maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)//默认wrap更具实际布局
                //.isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                //.hasBlurBg(true)//模糊默认false
                //.hasShadowBg(true)//默认true
                .asCustom(popupView)/*.enableDrag(false)*/
                .show();
        popupView.post(() -> {
            popupView.setBill(billTab);//账单信息
            popupView.setBillImages(new ArrayList<>());//首先把图片重置
            homeViewModel.getBillImages(billTab.getId()).observe(getViewLifecycleOwner(), images -> popupView.setBillImages(images));

        });
        popupView.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (homeUUID.equals(getMainActivity().getMainViewModel().getHomeUUID())) {
            getMainActivity().setYearMonthItemVisible(false);
        }

    }


    /**
     * 刷新列表数据、收支情况数据
     *
     * @param year  年
     * @param month 月
     */
    public void notifyData(int year, int month) {
        homeViewModel.setYear(year);
        homeViewModel.setMonth(month);
        homeViewModel.getBills().observe(getMainActivity(), bills -> {
            adapter.setDiffNewData(bills);
            LogUtils.d("notify: ", bills.size());
        });
        totalExpenseAndIncome(year, month);
    }

}