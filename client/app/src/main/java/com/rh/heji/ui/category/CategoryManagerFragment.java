package com.rh.heji.ui.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rh.heji.data.db.Constant;
import com.rh.heji.data.db.mongo.ObjectId;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.data.AppDatabase;
import com.rh.heji.data.BillType;
import com.rh.heji.data.db.Category;
import com.rh.heji.databinding.FragmentCategoryManagerBinding;
import com.rh.heji.ui.category.adapter.CategoryManagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Date: 2020/10/10
 * Author: 锅得铁
 * # 类别标签管理
 */
public class CategoryManagerFragment extends BaseFragment implements Observer<List<Category>> {
    FragmentCategoryManagerBinding binding;
    private CategoryManagerAdapter adapter;
    private CategoryViewModule categoryViewModule;
    CategoryManagerFragmentArgs args;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        categoryViewModule = getViewModel(CategoryViewModule.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getMainActivity().hideInput();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void initView(View view) {
        binding = FragmentCategoryManagerBinding.bind(view);
        args = CategoryManagerFragmentArgs.fromBundle(getArguments());

        binding.btnAdd.setOnClickListener(v -> {
            String name = binding.editCategoryValue.getText().toString().trim();
            categoryViewModule.saveCategory(name, args.getIeType());
            KeyboardUtils.hideSoftInput(view);//隐藏键盘
            binding.editCategoryValue.setText("");
            binding.editCategoryValue.clearFocus();//清除聚焦

        });

        binding.categoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryManagerAdapter() {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, Category label) {
                super.convert(holder, label);
                itemBinding.imgDelete.setVisibility(View.INVISIBLE);
                itemBinding.imgDelete.setOnClickListener(v -> {
                    alertDeleteTip(label);
                });
            }
        };
        if (args.getIeType() == BillType.INCOME.type())
            categoryViewModule.getIncomeCategory().observe(getViewLifecycleOwner(), this);
        else
            categoryViewModule.getExpenditureCategory().observe(getViewLifecycleOwner(), this);
        binding.categoryRecycler.setAdapter(adapter);

    }

    @Override
    protected void setUpToolBar() {
        super.setUpToolBar();
        getToolBar().setTitle("分类管理");
        getToolBar().setNavigationIcon(blackDrawable());
        getToolBar().setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_category_manager;
    }

    private void alertDeleteTip(Category label) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {

            Navigation.findNavController(view).popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onChanged(List<Category> categories) {
        adapter.setNewInstance(categories);
    }
}
