package com.rh.heji.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Bill;
import com.rh.heji.databinding.FragmentBillsHomeBinding;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.ui.bill.Iteminfo.BillInfoPop;
import com.rh.heji.ui.bill.YearSelectPop;
import com.rh.heji.ui.bill.adapter.DayBillsNode;
import com.rh.heji.ui.bill.adapter.DayIncome;
import com.rh.heji.ui.bill.adapter.DayIncomeNode;
import com.rh.heji.ui.bill.adapter.NodeBillsAdapter;
import com.rh.heji.ui.bill.add.AddBillFragmentArgs;
import com.rh.heji.widget.CardDecoration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.rh.heji.Constants.BACKGROUND_ALPHA;

public class BillsHomeFragment extends BaseFragment {
    FragmentBillsHomeBinding binding;
    private BillsHomeViewModel homeViewModel;
    NodeBillsAdapter adapter;
    private String homeUUID;
    //最后点击时间
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 500;
    private TextView toolBarCenterTitle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeUUID = UUID.randomUUID().toString();
        getMainActivity().getMainViewModel().setHomeUUID(homeUUID);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
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
        binding.fab.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();//当前日期
            Navigation.findNavController(view).navigate(R.id.nav_income, new AddBillFragmentArgs.Builder(calendar).build().toBundle());
        });
    }

    @Override
    protected void setUpToolBar() {
        super.setUpToolBar();
        addYearMonthView();
        Toolbar toolbar = getToolBar();
        toolbar.inflateMenu(R.menu.home);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_dehaze_24, getMainActivity().getTheme()));
        toolbar.setNavigationOnClickListener(v -> {
            //展开侧滑菜单
            getMainActivity().openDrawer();
        });
        toolbar.getMenu().findItem(R.id.item1).setOnMenuItemClickListener(item -> {
            ToastUtils.showLong("aa");
            Navigation.findNavController(view).navigate(R.id.nav_calendar_note);
            return false;
        });
        toolbar.getMenu().findItem(R.id.item2).setOnMenuItemClickListener(item -> {
            ToastUtils.showLong("aa");
            Navigation.findNavController(view).navigate(R.id.nav_gallery);
            return false;
        });
    }


    @Override
    protected int layoutId() {
        return R.layout.fragment_bills_home;
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


    /**
     * 刷新头部收支
     */
    private void refreshHeadView() {
        BigDecimal income = new BigDecimal(binding.homeHeadView.tvTotalIncomeValue.getText().toString());
        BigDecimal expenses = new BigDecimal(binding.homeHeadView.tvTotalExpensesValue.getText().toString());
        BigDecimal totalRevenue = income.subtract(expenses);
        if (totalRevenue.longValue() > 0) {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(getMainActivity().getColor(R.color.income));
        } else {
            binding.homeHeadView.tvTotalRevenueValue.setTextColor(getMainActivity().getColor(R.color.expenditure));
        }
        binding.homeHeadView.tvTotalRevenueValue.setText(totalRevenue.toString());
    }

    /**
     * 初始化账单列表适配器
     */
    private void initBillsAdapter() {
        adapter = new NodeBillsAdapter();
        //binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.homeRecycler.setLayoutManager(new LinearLayoutManager(getMainActivity()));
        binding.homeRecycler.setAdapter(adapter);
        //binding.homeRecycler.addItemDecoration(new CardViewDecoration(getResources(), 5));
        binding.homeRecycler.addItemDecoration(new CardDecoration());

        binding.materialupAppBar.getBackground().setAlpha(BACKGROUND_ALPHA);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME) {
                lastClickTime = System.currentTimeMillis();
                if (adapter.getItem(position) instanceof DayIncomeNode) {//日视图
                    DayIncomeNode dayIncomeNode = (DayIncomeNode) adapter.getItem(position);
                    DayIncome dayIncome = dayIncomeNode.getDayIncome();
                    Calendar calendar = java.util.Calendar.getInstance();
                    calendar.set(dayIncome.getYear(), dayIncome.getMonth()-1, dayIncome.getMonthDay());
                    AddBillFragmentArgs args = new AddBillFragmentArgs.Builder(calendar).build();//选择的日期
                    Navigation.findNavController(view).navigate(R.id.nav_income, args.toBundle());
                } else {//日详细列表ITEM
                    DayBillsNode dayBills = (DayBillsNode) adapter.getItem(position);
                    Bill bill = dayBills.getBill();
                    showBillItemPop(bill);
                }
            }

        });
        binding.nestedSccrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {//向下滑动隐藏
                binding.fab.hide();
            } else {
                binding.fab.show();
            }
        });
        //差异化比对更新
//        adapter.setDiffCallback(new DiffUtil.ItemCallback<Bill>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
//                return oldItem.id.equals(newItem.id);
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
//                return oldItem.id.equals(newItem.id);
//            }
//        });
//        adapter.setDiffCallback(new DiffUtil.ItemCallback<BaseNode>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
//                if (oldItem instanceof DayIncomeNode) {
//                    DayIncomeNode oldNode = ((DayIncomeNode) oldItem);
//                    DayIncomeNode newNode = ((DayIncomeNode) oldItem);
//                    if (oldNode.getDayIncome().getMonthDay() == newNode.getDayIncome().getMonthDay()) {
//                        if (oldNode.getDayIncome().getExpected() == newNode.getDayIncome().getExpected()
//                                && oldNode.getDayIncome().getIncome() == newNode.getDayIncome().getIncome()) {
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
//                if (oldItem instanceof DayIncomeNode) {
//                    DayIncomeNode oldNode = ((DayIncomeNode) oldItem);
//                    DayIncomeNode newNode = ((DayIncomeNode) oldItem);
//                    if (oldNode.getDayIncome().getMonthDay() == newNode.getDayIncome().getMonthDay()) {
//                        if (oldNode.getDayIncome().getExpected() == newNode.getDayIncome().getExpected()
//                                && oldNode.getDayIncome().getIncome() == newNode.getDayIncome().getIncome()) {
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        });
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


    /**
     * 刷新列表数据、收支情况数据
     *
     * @param year  年
     * @param month 月
     */
    public void notifyData(int year, int month) {
        homeViewModel.setYear(year);
        homeViewModel.setMonth(month);
        //homeViewModel.getBills().observe(getMainActivity(), listObserver);

        homeViewModel.getBillsNodLiveData().observe(this, baseNodes -> {
            adapter.setNewInstance(baseNodes);
            //adapter.setDiffNewData(baseNodes);
        });
        homeViewModel.getBillsData();
        totalExpenseAndIncome(year, month);
    }

//    Observer<List<Bill>> listObserver = bills -> {
//        adapter.setDiffNewData(bills);
//        LogUtils.d("notify: ", bills.size());
//    };

    /**
     * 该Menu属于全局所以在这里控制
     */
    public void addYearMonthView() {
        toolBarCenterTitle = getToolBar().findViewById(R.id.toolbar_center_title);
        toolBarCenterTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_baseline_arrow_down_white_32), null);
        toolBarCenterTitle.setCompoundDrawablePadding(8);

        int thisYear = homeViewModel.getYear();
        int thisMonth = homeViewModel.getMonth();
        final String yearMonth = thisYear + "." + thisMonth;
        toolBarCenterTitle.setText(yearMonth);
        toolBarCenterTitle.setOnClickListener(v -> {
            new XPopup.Builder(getMainActivity())
                    //.hasBlurBg(true)//模糊
                    .hasShadowBg(true)
                    .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(new YearSelectPop(getMainActivity(), (year, month) -> {
                        toolBarCenterTitle.setText(year + "." + month + "");
                        List<Fragment> fragments = getMainActivity().getFragments();
                        fragments.forEach(fragment -> {
                            if (fragment instanceof BillsHomeFragment) {
                                BillsHomeFragment homeFragment = (BillsHomeFragment) fragment;
                                homeFragment.notifyData(year, month);
                            }
                        });
                    }))/*.enableDrag(false)*/
                    .show();

        });
    }

}