package com.rh.heji;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.rh.heji.databinding.NavHeaderMainBinding;
import com.rh.heji.service.DataSyncService;
import com.rh.heji.ui.user.JWTParse;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NavController navController;//导航控制
    private DrawerLayout drawerLayout;
    private MainViewModel mainViewModel;
    private NavHeaderMainBinding navHeaderMainBinding;
    private NavigationView navigationView;//导航视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions((allGranted, grantedList, deniedList) -> {
            Toast.makeText(this, "已同意权限", Toast.LENGTH_SHORT).show();
        });
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        initDrawerLayout();

        String token = AppCache.Companion.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            navController.navigate(R.id.nav_login);
        } else {
            JWTParse.User user = JWTParse.INSTANCE.getUser(token);
            setDrawerLayout(user);
            ToastUtils.showLong(token);
            startSyncDataService();
            AppCache.Companion.getInstance().appViewModule.asyncData();
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

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //系统默认侧滑控制
        //NavigationUI.setupWithNavController(navigationView, navController);
        //自定义控制
        navigationDrawerController();

        View navHeaderView = navigationView.getHeaderView(0);
        navHeaderMainBinding = NavHeaderMainBinding.bind(navHeaderView);
        navHeaderView.setOnClickListener(v -> {
            ToastUtils.showLong("");
            getNavController().navigate(R.id.nav_user_info);
            drawerLayout.closeDrawers();
        });
    }

    private void navigationDrawerController() {
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    boolean handled = item.isChecked();//已选中
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout) parent).closeDrawer(navigationView);
                    }

                    if (!handled) {
                        NavigationUI.onNavDestinationSelected(item, navController);
                        if (navController.getCurrentDestination().getId() != R.id.nav_home)
                            navController.popBackStack();
                        try {
                            navController.navigate(item.getItemId());
                            return true;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }

                    }

                    return true;
                });
        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                     @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        NavigationView view = weakReference.get();
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this);
                            return;
                        }
                        Menu menu = view.getMenu();
                        for (int h = 0, size = menu.size(); h < size; h++) {
                            MenuItem item = menu.getItem(h);
                            NavDestination currentDestination = destination;
                            while (currentDestination.getId() != item.getItemId() && currentDestination.getParent() != null) {
                                currentDestination = currentDestination.getParent();
                            }
                            boolean isChecked = currentDestination.getId() == item.getItemId();
                            item.setChecked(isChecked);
                        }
                        final String home = getResources().getString(R.string.menu_home);
                        final String report = getResources().getString(R.string.menu_report);
                        final String setting = getResources().getString(R.string.menu_setting);
                        String itemLabel = destination.getLabel().toString();

                        if (itemLabel.equals(home)) {
                            setTitle("记账");
                        } else if (itemLabel.equals(report)) {
                        } else if (itemLabel.equals(setting)) {
                        }
                        LogUtils.i("onDestinationChanged:", destination.getLabel());

                    }
                });
    }

    private void setDrawerLayout(JWTParse.User user) {
        navHeaderMainBinding.tvTitle.setText(user.getUsername());
        navHeaderMainBinding.tvNice.setText(user.getAuth().toString());
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
        return super.onPrepareOptionsMenu(menu);
    }

    @NotNull
    public List<Fragment> getFragments() {
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

    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.START);
    }

    public void disableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
    public void enableDrawer(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}