package com.rh.heji.ui.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.lxj.xpopup.XPopup;
import com.matisse.Matisse;
import com.matisse.entity.ConstValue;
import com.rh.heji.BaseFragment;
import com.rh.heji.BuildConfig;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.mongo.ObjectId;
import com.rh.heji.databinding.IncomeFragmentBinding;
import com.rh.heji.ui.add.adapter.TicketEntity;
import com.rh.heji.ui.category.CategoryTabFragment;
import com.rh.heji.ui.category.CategoryViewModule;
import com.rh.heji.widget.KeyBoardView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * 支出 收入
 */
public class AddBillFragment extends BaseFragment {
    private final String BILL_UUID = UUID.randomUUID().toString();
    private AddBillViewModel incomeViewModel;
    private CategoryViewModule categoryViewModule;
    IncomeFragmentBinding binding;
    CategoryTabFragment categoryTabFragment;
    SelectImagePop selectImagePou;//图片弹窗


    @Override
    protected int layoutId() {
        return R.layout.income_fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        incomeViewModel = getViewModel(AddBillViewModel.class);
        categoryViewModule = getViewModel(CategoryViewModule.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getMainActivity().getToolbar().setVisibility(View.GONE);
        getMainActivity().getFab().setVisibility(View.GONE);

        /**
         * 不复用View，否则xml fragment不触发生命周期
         */
        view = inflater.inflate(layoutId(), container, false);
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        binding = IncomeFragmentBinding.bind(view);
        selectImage();
        selectPerson();
        selectYearAndDay();
        remark();
        category();
        keyboardListener();

    }

    private void category() {
        categoryTabFragment = (CategoryTabFragment) getChildFragmentManager().findFragmentById(R.id.categoryFragment);

        categoryViewModule.getSelectCategory().observe(getViewLifecycleOwner(), category -> {
            if (null != category) {
                BillType billType = BillType.transform(category.getType());
                binding.keyboard.setType(billType);
                changeMoneyTextColor(billType);
                String categoryName =category.getCategory();
                if (category.getCategory().equals("管理")) {
                    categoryName =billType.text();
                }
                incomeViewModel.getBill().setCategory(categoryName);
                incomeViewModel.getBill().setType(category.getType());
            }

        });
    }


    private void remark() {
        binding.eidtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                incomeViewModel.getBill().setRemark(s.toString().trim());
            }
        });
    }

    private void selectYearAndDay() {
        String nowTime = incomeViewModel.getTime();
        binding.tvBillTime.setText(nowTime);
        binding.tvBillTime.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getMainActivity());
            dialog.setOnDateSetListener((datePicker, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                String yearTime = TimeUtils.date2String(calendar.getTime(), "yyyy-MM-dd") + " 00:00";//未选时自动补全
                setNoteTime(yearTime);
                selectHourAndMinute(yearTime);
            });
            dialog.show();
        });
    }

    /**
     * 选择小时和分钟
     *
     * @param yearTime 年份-月份
     */
    private void selectHourAndMinute(String yearTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getMainActivity(), (timePicker, hourOfDay, minute) -> {
            if (hourOfDay == 0 && minute == 0) return;
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(TimeUtils.string2Date(yearTime, "yyyy-MM-dd HH:mm"));
            calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar2.set(Calendar.MINUTE, minute);
            String dayTime = TimeUtils.date2String(calendar2.getTime(), "yyyy-MM-dd HH:mm");
            setNoteTime(dayTime);
        }, 0, 0, true);
        timePickerDialog.show();
    }

    /**
     * 赋值
     *
     * @param selectTime 选中的日期或更精确的
     */
    private void setNoteTime(String selectTime) {
        binding.tvBillTime.setText(selectTime);
        incomeViewModel.setTime(selectTime);
        if (BuildConfig.DEBUG) {
            LogUtils.d(selectTime);
            long billTime = TimeUtils.string2Millis(incomeViewModel.getTime(), "yyyy-MM-dd HH:mm");
            LogUtils.d(TimeUtils.millis2String(billTime, "yyyy-MM-dd HH:mm"));
        }

    }


    /**
     * 选择经手人
     */
    private void selectPerson() {
        List<String> names = incomeViewModel.getDealers();//经手人名单
        if (names.size() > 0) {
            binding.tvUserLabel.setText("经手人:" + names.get(0));//默认经手人
            incomeViewModel.getBill().setDealer(names.get(0));//设置默经手人
        }
        binding.tvUserLabel.setOnClickListener(v -> {
            new XPopup.Builder(getContext())
                    .maxHeight(binding.keyboard.getHeight())
                    .asBottomList("请选择经手人", names.toArray(new String[names.size()]),
                            (position, text) -> {
                                binding.tvUserLabel.setText("经手人:" + text);
                                incomeViewModel.getBill().setDealer(text);
                            })
                    .show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void keyboardListener() {
        binding.keyboard.setKeyboardListener(new KeyBoardView.OnKeyboardListener() {
            @Override
            public void save(String result) {
                ToastUtils.showLong(result);
                BillType billType = binding.keyboard.getBillType();
                saveBill(result,billType);
            }

            @Override
            public void calculation(String result) {
                binding.tvMoney.setText(result);
            }

            @Override
            public void switchModel(BillType billType) {
                changeMoneyTextColor(billType);

                if (null != categoryTabFragment)
                    categoryTabFragment.setType(billType);

            }
        });
    }

    private void changeMoneyTextColor(BillType billType) {
        int textColor = billType.equals(BillType.EXPENDITURE) ? android.R.color.holo_red_dark : android.R.color.holo_green_dark;
        binding.tvMoney.setTextColor(getResources().getColor(textColor));
    }

    private void saveBill(String money, BillType billType) {
        if (TextUtils.isEmpty(money) || money.equals("0")) {
            ToastUtils.showShort("未填写金额");
            return;
        }
        incomeViewModel.save(new ObjectId().toString(), money,billType).observe(getViewLifecycleOwner(), str -> {
            //BillEntity entity = new BillEntity(bill);
//            HeJiServer billService = (HeJiServer) ServiceCreator.getInstance().createService(HeJiServer.class);
//                billService.saveBill(entity).enqueue(new BaseCallback<BaseResponse>() {
//                    @Override
//                    protected void onSuccess(BaseResponse data) throws IOException {
//                        LogUtils.e(data.toString());
//                        AppDatabase.getInstance().billDao().update(bill);
//                    }
//
//                    @Override
//                    protected void onError(BaseResponse response) {
//                        super.onError(response);
//                    }
//                });
            //AppDatabase.getInstance().imageDao().install(images);
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    /**
     * 票据图片
     */
    private void selectImage() {
        selectImagePou = new SelectImagePop(getContext(), getMainActivity());
        binding.imgTicket.setOnClickListener(v -> {
            if (selectImagePou == null)
                selectImagePou = new SelectImagePop(getContext(), getMainActivity());
            new XPopup.Builder(getContext())
                    .asCustom(selectImagePou)
                    .show();
            //selectImagePou.getLayoutParams().height = binding.keyboard.getRoot().getHeight();
            selectImagePou.setDeleteClickListener(data -> {
                incomeViewModel.setImgUrls(data);
            });
            selectImagePou.setData(new ArrayList<>());
        });
        final Observer<List<String>> imgObserver = data -> {
            binding.imgTicket.setText("图片(x" + data.size() + ")");
            selectImagePou.setData(data);
        };
        incomeViewModel.getImgUrlsLive().observe(getViewLifecycleOwner(), imgObserver);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        getMainActivity().getFab().setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ConstValue.REQUEST_CODE_CHOOSE) {//选中的照片
                List<String> mSelected = new ArrayList<>();
                Matisse.Companion.obtainResult(data).forEach(uri -> {
                    String imgUrl = UriUtils.uri2File(uri).getAbsolutePath();
                    mSelected.add(imgUrl);
                });

                setImages(mSelected);
                if (incomeViewModel.getImgUrls().size() > 0) {
                    mSelected.stream().forEach(s -> {
                        /**
                         * 包含的话就删除重新加
                         */
                        if (incomeViewModel.getImgUrls().contains(s)) {
                            incomeViewModel.getImgUrls().remove(s);
                        }
                        incomeViewModel.addImgUrl(s);
                    });
                } else {
                    incomeViewModel.setImgUrls(mSelected);
                }

            }
        }
    }


    private void setImages(List<String> selected) {
        List<TicketEntity> photos = new ArrayList<>();
        for (String item : selected) {
            TicketEntity info = new TicketEntity();
            Long fileTime = new File(item).lastModified();
            String time = TimeUtils.millis2String(fileTime);
            info.setCreateTime(time);
            info.setPath(item);
            photos.add(info);
        }
    }

    public CategoryViewModule getCategoryViewModule() {
        return categoryViewModule;
    }
}