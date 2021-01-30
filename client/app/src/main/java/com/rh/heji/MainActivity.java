package com.rh.heji;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.rh.heji.databinding.ActivityMainBinding;
import com.rh.heji.databinding.NavHeaderMainBinding;
import com.rh.heji.service.DataSyncService;
import com.rh.heji.ui.home.BillsHomeFragment;
import com.rh.heji.ui.bill.YearSelectPop;
import com.rh.heji.ui.user.JWTParse;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;//导航控制
    private FloatingActionButton fab;//加号按钮
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private MainViewModel mainViewModel;
    private NavHeaderMainBinding navHeaderMainBinding;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions((allGranted, grantedList, deniedList) -> {
            Toast.makeText(this, "已同意权限", Toast.LENGTH_SHORT).show();
        });
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        initDrawerLayout();
        initFab();
        String token = AppCache.Companion.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            navController.navigate(R.id.nav_login);
        } else {
            JWTParse.User user = JWTParse.INSTANCE.getUser(token);
            setDrawerLayout(user);
            ToastUtils.showLong(token);
            startSyncDataService();
        }
    }

    /**
     * 侧滑菜单相关
     */
    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //Logout Menu
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.menu_logout).setOnMenuItemClickListener(item -> {
            new XPopup.Builder(MainActivity.this).asConfirm("退出确认", "确认退出当前用户吗?", () -> {
                AppCache.Companion.getInstance().deleteToken();
                finish();
            }).show();
            return false;
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_setting)
                .setDrawerLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        NavController.OnDestinationChangedListener listener = destinationChangedListener();
        navController.addOnDestinationChangedListener(listener);
        View navHeaderView = navigationView.getHeaderView(0);
        navHeaderMainBinding = NavHeaderMainBinding.bind(navHeaderView);
        navHeaderView.setOnClickListener(v -> {
            ToastUtils.showLong("");
            getNavController().navigate(R.id.nav_user_info);
            drawerLayout.closeDrawers();
        });
    }

    private void setDrawerLayout(JWTParse.User user) {
        navHeaderMainBinding.tvTitle.setText(user.getUsername());
        navHeaderMainBinding.tvNice.setText(user.getAuth().toString());
    }

    /**
     * ➕ 浮动按钮
     */
    private void initFab() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            getNavController().navigate(R.id.nav_income);
        });
    }

    @NotNull
    private NavController.OnDestinationChangedListener destinationChangedListener() {
        final String home = getResources().getString(R.string.menu_home);
        final String report = getResources().getString(R.string.menu_report);
        final String setting = getResources().getString(R.string.menu_setting);

        return (controller, destination, arguments) -> {
            String itemLabel = destination.getLabel().toString();

            if (itemLabel.equals(home)) {
            } else if (itemLabel.equals(report)) {
            } else if (itemLabel.equals(setting)) {
            }
            LogUtils.i("onDestinationChanged:", destination.getLabel());
        };
    }

    public void setYearMonthItemVisible(boolean visible) {
        MenuItem item = getToolbar().getMenu().findItem(R.id.action_year_month);
        if (null != item)
            item.setVisible(visible);
    }

    public void setSaveItemVisible(boolean visible) {
        getToolbar().getMenu().setGroupVisible(R.id.menu_save, visible);
    }

    public void setSettingItemVisible(boolean visible) {
        getToolbar().getMenu().setGroupVisible(R.id.menu_settings, visible);

    }

    public void startSyncDataService() {
        Intent intent = new Intent(this, DataSyncService.class);
        intent.setAction(DataSyncService.ACTION_START_FOREGROUND_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);//启动前台服务
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, DataSyncService.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.setGroupVisible(R.id.menu_save, false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void checkPermissions(RequestCallback requestCallback) {
        PermissionX.init(this).permissions(
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        ).explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(
                            deniedList,
                            "为了正常使用你必须同意以下权限:",
                            "我已明白");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    requestCallback.onResult(allGranted, grantedList, deniedList);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    public NavController getNavController() {
        return navController;
    }

    public AppBarConfiguration getAppBarConfiguration() {
        return mAppBarConfiguration;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public DrawerLayout getDrawer() {
        return drawerLayout;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Fragment> fragments = getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        List<Fragment> fragments = getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        List<Fragment> fragments = getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).onPrepareOptionsMenu(menu);
        }
        addYearMonthView(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @NotNull
    private List<Fragment> getFragments() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        return navHostFragment.getChildFragmentManager().getFragments();
    }


    /**
     * 隐藏键盘
     */
    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public MainViewModel getMainViewModel() {
        return mainViewModel;
    }

    /**
     * 该Menu属于全局所以在这里控制
     *
     * @param menu
     */
    private void addYearMonthView(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_year_month);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.menu_item_month, null);
        TextView tvYearMonth = view.findViewById(R.id.tvYearMonth);
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);//当前年份
        int thisMonth = calendar.get(Calendar.MONTH) + 1;
        final String yearMonth = thisYear + "年" + thisMonth + "月";
        tvYearMonth.setText(yearMonth);
        view.setOnClickListener(v -> {
            new XPopup.Builder(v.getContext())
                    //.hasBlurBg(true)//模糊
                    .hasShadowBg(true)
                    .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(new YearSelectPop(v.getContext(), thisYear, thisMonth, (year, month) -> {
                        tvYearMonth.setText(year + "年" + month + "月");
                        List<Fragment> fragments = getFragments();
                        fragments.forEach(fragment -> {
                            if (fragment instanceof BillsHomeFragment) {
                                BillsHomeFragment homeFragment = (BillsHomeFragment) fragment;
                                homeFragment.notifyData(year, month);
                            }
                        });
                    }))/*.enableDrag(false)*/
                    .show();

        });
        menuItem.setActionView(view);
        setYearMonthItemVisible(false);
    }


}