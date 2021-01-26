package com.rh.heji.ui.category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Category;
import com.rh.heji.databinding.FragmentCategoryContentBinding;
import com.rh.heji.ui.bill.add.AddBillFragment;
import com.rh.heji.ui.category.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Date: 2020/10/11
 * Author: 锅得铁
 * # 收入支出标签
 */
public class CategoryFragment extends BaseFragment {
    private FragmentCategoryContentBinding binding;
    private CategoryAdapter labelAdapter;
    public static final String KEY_TYPE = "TYPE";
    private int thisType;
    private BillType tabType;
    private CategoryViewModule categoryViewModule;

    /**
     * 收\支
     *
     * @param ieType Income : Expenditure
     * @return
     */
    public static CategoryFragment newInstance(int ieType) {
        CategoryFragment ieCategoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, ieType);
        ieCategoryFragment.setArguments(bundle);
        return ieCategoryFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    Observer<List<Category>> labelObserver = categories -> {
        Category selectCategory = categoryViewModule.getSelectCategory().getValue();
        if (null != selectCategory) {
            categories.stream().forEach(category -> {
                boolean isSelected = category.getCategory().equals(selectCategory.getCategory())
                        && category.getType() == selectCategory.getType();
                if (isSelected) {
                    category.selected = true;
                }
            });
        }
        labelAdapter.setNewInstance(categories);
        addCategoryFooterView(labelAdapter);
        defSelected();
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        AddBillFragment addBillFragment = (AddBillFragment) getParentFragment().getParentFragment();
        CategoryTabFragment tabFragment = (CategoryTabFragment) getParentFragment();
        categoryViewModule = addBillFragment.getCategoryViewModule();
        tabType = tabFragment.getType();
        thisType = getArguments().getInt(KEY_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getCategoryData();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        binding = FragmentCategoryContentBinding.bind(view);
        initCategory(new ArrayList<>());
        getCategoryData();
    }

    private void getCategoryData() {
        if (thisType == BillType.INCOME.type()) {
            categoryViewModule.getIncomeCategory().observe(getViewLifecycleOwner(), labelObserver);
        } else {
            categoryViewModule.getExpenditureCategory().observe(getViewLifecycleOwner(), labelObserver);
        }

    }

    //点击事件
    OnItemClickListener onItemClickListener = (adapter, view, position) -> {
        Category category = labelAdapter.getItem(position);//当前点击的
        if (category.getLabel().equals(CategoryAdapter.SETTING)) {//设置
            CategoryManagerFragmentArgs args = new CategoryManagerFragmentArgs
                    .Builder()
                    .setIeType(thisType).build();
            Navigation.findNavController(view).navigate(R.id.nav_category_manager, args.toBundle());
            return;
        }
        category.selected = !category.selected;//反选
        //使其他置为为选中状态
        labelAdapter.getData().forEach(i -> {
            if (!i.getCategory().equals(category.getCategory())) {
                i.selected = false;
            }
        });
        labelAdapter.notifyDataSetChanged();
        categoryViewModule.setSelectCategory(category);
    };

    @Override
    protected int layoutId() {
        return R.layout.fragment_category_content;
    }

    private void initCategory(List<Category> categories) {
        labelAdapter = new CategoryAdapter(categories);
        binding.categoryRecycler.setLayoutManager(new GridLayoutManager(getMainActivity(), 6));
        binding.categoryRecycler.setAdapter(labelAdapter);

        labelAdapter.setOnItemClickListener(onItemClickListener);
        addCategoryFooterView(labelAdapter);//尾部添加设置按钮
    }

    /**
     * 默认第一个为选中
     */
    private void defSelected() {
        if (!labelAdapter.getData().isEmpty() && labelAdapter.getData().size() > 0) {
            long count = labelAdapter.getData().stream().filter(category -> category.selected).count();
            if (count <= 0) {
                Category firstItem = labelAdapter.getData().stream().findFirst().get();
                if (!firstItem.getCategory().equals("管理")) {
                    firstItem.selected = true;
                    labelAdapter.notifyDataSetChanged();
                    if (!isHidden()&&thisType ==tabType.type()) {
                        categoryViewModule.setSelectCategory(firstItem);
                    }

                }
            }
        }
    }

    private void addCategoryFooterView(CategoryAdapter labelAdapter) {
        if (labelAdapter.getData() != null && labelAdapter.getData().size() > 0) {
            Category lastItem = labelAdapter.getData().get(labelAdapter.getItemCount() - 1);
            if (!lastItem.getCategory().equals(CategoryAdapter.SETTING)) {
                addSettingItem(labelAdapter);
            }
        } else {
            addSettingItem(labelAdapter);
        }
        labelAdapter.notifyDataSetChanged();
    }

    private void addSettingItem(CategoryAdapter labelAdapter) {
        Category category = new Category(CategoryAdapter.SETTING, 0, thisType);
        labelAdapter.addData(labelAdapter.getItemCount(), category);
    }

    public void setCategory() {
        Category selectCategory = null;
        List<Category> selects = labelAdapter.getData().stream().filter(new Predicate<Category>() {
            @Override
            public boolean test(Category category) {
                return category.selected;
            }
        }).collect(Collectors.toList());
        if (selects.size() <= 0) {
            selectCategory = labelAdapter.getData().stream().findFirst().get();
        } else {
            selectCategory = selects.stream().findFirst().get();
        }
        categoryViewModule.setSelectCategory(selectCategory);
    }
}
