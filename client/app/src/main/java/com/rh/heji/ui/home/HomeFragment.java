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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BusUtils;
import com.blankj.utilcode.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.rh.heji.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.databinding.FragmentHomeBinding;
import com.rh.heji.ui.home.adapter.BillInfoAdapter;
import com.rh.heji.ui.home.pop.BillInfoPop;

import java.math.BigDecimal;
import java.util.UUID;

import static com.rh.heji.Constants.BACKGROUND_ALPHA;

public class HomeFragment extends BaseFragment {
    FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    BillInfoAdapter adapter;
    private String homeUUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(layoutId(), container, false);
        initView(view);
        BusUtils.post("TAG","HELLO WORD");
        return view;
    }

    public void initView(View view) {
        homeViewModel = getActivityViewModel(HomeViewModel.class);
        binding = FragmentHomeBinding.bind(view);
        initBillsAdapter();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeUUID = UUID.randomUUID().toString();
        getMainActivity().getMainViewModel().setHomeUUID(homeUUID);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onStart() {
        super.onStart();
        getMainActivity().getToolbar().setVisibility(View.VISIBLE);
        getMainActivity().getToolbar().getMenu().setGroupVisible(R.id.menu_save, false);
        getMainActivity().getToolbar().getMenu().setGroupVisible(R.id.menu_settings, false);
        setYearMonthVisible(true);
        if (true){//TODO
            Navigation.findNavController(view).navigate(R.id.nav_login);
        }
    }

    @Override
    public void onResume() {
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
            Bill billTab = (Bill) adapter.getItem(position);
            showBillItemPop(billTab);
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
            popupView.setBill(billTab);
            popupView.setBillImages(homeViewModel.getBillImages(billTab.getId()));
        });
        popupView.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (homeUUID.equals(getMainActivity().getMainViewModel().getHomeUUID())) {
            setYearMonthVisible(false);
        }

    }

    /**
     * Toolbar显示控制
     *
     * @param b 显示否
     */
    private void setYearMonthVisible(boolean b) {
        MenuItem menuItem = getMainActivity().getToolbar().getMenu().findItem(R.id.action_year_month);
        if (null != menuItem)
            menuItem.setVisible(b);
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