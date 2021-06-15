package com.rh.heji

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.navigation.NavigationView
import com.lxj.xpopup.XPopup
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.request.ExplainScope
import com.permissionx.guolindev.request.ForwardScope
import com.rh.heji.AppCache.Companion.instance
import com.rh.heji.databinding.ActivityMainBinding
import com.rh.heji.databinding.NavHeaderMainBinding
import com.rh.heji.ui.home.DrawerSlideListener
import com.rh.heji.ui.home.HomeDrawerListener
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.ui.user.JWTParse.getUser
import kotlinx.coroutines.*
import java.lang.NullPointerException
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController private set
    private lateinit var drawerLayout: DrawerLayout
    val mainViewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding//侧拉头像
    private lateinit var navigationView: NavigationView
    private fun permitDiskReads(func: () -> Any): Any {
        if (BuildConfig.DEBUG) {
            val oldThreadPolicy = StrictMode.getThreadPolicy()
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder(oldThreadPolicy)
                    .permitDiskReads().build()
            )
            val anyValue = func()
            StrictMode.setThreadPolicy(oldThreadPolicy)

            return anyValue
        } else {
            return func()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        permitDiskReads { super.onCreate(savedInstanceState) }//StrictMode policy violation; ~duration=127 ms: android.os.strictmode.DiskReadViolation by XiaoMi
        checkPermissions { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
            //初始化一些需要权限的功能
            AppCache.instance.appViewModule.initCarshTool()
            Toast.makeText(this, "已同意权限", Toast.LENGTH_SHORT).show()
        }
        setContentView(R.layout.activity_main)
        initDrawerLayout()
        lifecycleScope.launch(Dispatchers.IO) {
            val token = instance.token.tokenString
            withContext(Dispatchers.Main) {
                if (TextUtils.isEmpty(token)) {
                    navController.navigate(R.id.nav_login)
                } else {
                    val user = getUser(token)
                    setDrawerLayout(user)
                    instance.appViewModule.asyncData()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            @SuppressLint("WrongConstant")
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.nav_home) { //主页
                    if (drawerLayout.isDrawerOpen(Gravity.START)) {
                        drawerLayout.closeDrawer(Gravity.START)
                    } else {
                        finish()
                    }
                } else {
                    if (drawerLayout.isDrawerOpen(Gravity.START)) {
                        drawerLayout.closeDrawer(Gravity.START)
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        })
    }

    /**
     * 侧滑菜单相关
     */
    private fun initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val navHostFragmentRootView: View = findViewById(R.id.nav_host_fragment)
        drawerLayout.addDrawerListener(HomeDrawerListener(this, object : DrawerSlideListener {
            override fun offset(left: Int, top: Int, right: Int, bottom: Int) {
                navHostFragmentRootView.layout(left, top, right, bottom)
            }
        }))
        //Logout Menu
        val navMenu = navigationView.menu
        navMenu.findItem(R.id.menu_logout).setOnMenuItemClickListener { item: MenuItem? ->
            XPopup.Builder(this@MainActivity).asConfirm("退出确认", "确认退出当前用户吗?") {
                runBlocking(Dispatchers.IO) { instance.token.delete() }
                finish()
            }.show()
            false
        }
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //系统默认侧滑控制
        //NavigationUI.setupWithNavController(navigationView, navController);
        //自定义控制
        navigationDrawerController()
        val navHeaderView = navigationView.getHeaderView(0)
        navHeaderMainBinding = NavHeaderMainBinding.bind(navHeaderView)
        navHeaderView.setOnClickListener { v: View? ->
            ToastUtils.showLong("")
            navController.navigate(R.id.nav_user_info)
            drawerLayout.closeDrawers()
        }
    }

    private fun navigationDrawerController() {
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            val handled = item.isChecked //已选中
            val parent = navigationView.parent
            if (parent is DrawerLayout) {
                parent.closeDrawer(navigationView)
            }
            if (!handled) {
                NavigationUI.onNavDestinationSelected(item, navController)
                if (navController.currentDestination?.id != R.id.nav_home) navController.popBackStack()
                try {
                    navController.navigate(item.itemId)
                    return@setNavigationItemSelectedListener true
                } catch (e: IllegalArgumentException) {
                    return@setNavigationItemSelectedListener false
                }
            }
            true
        }
        val weakReference = WeakReference(navigationView)
        navController.addOnDestinationChangedListener(
            object : OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination, arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    val menu = view.menu
                    var h = 0
                    val size = menu.size()
                    while (h < size) {
                        val item = menu.getItem(h)
                        var currentDestination: NavDestination = destination
                        while (currentDestination.id != item.itemId && currentDestination.parent != null) {
                            currentDestination = currentDestination.parent!!
                        }
                        val isChecked = currentDestination.id == item.itemId
                        item.isChecked = isChecked
                        h++
                    }
                    val home = resources.getString(R.string.menu_home)
                    val report = resources.getString(R.string.menu_report)
                    val setting = resources.getString(R.string.menu_setting)
                    val itemLabel = destination.label.toString()
                    if (itemLabel == home) {
                        title = "记账"
                    } else if (itemLabel == report) {
                    } else if (itemLabel == setting) {
                    }
                    LogUtils.i(destination.label)
                }
            })
    }

    private fun setDrawerLayout(user: JWTParse.User) {
        navHeaderMainBinding.tvTitle.text = user.username
        navHeaderMainBinding.tvNice.text = user.auth.toString()
    }

    fun checkPermissions(requestCallback: RequestCallback) {
        PermissionX.init(this).permissions(
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
        ).explainReasonBeforeRequest()
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String?>? ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "为了正常使用你必须同意以下权限:",
                    "我已明白"
                )
            }
            .onForwardToSettings { scope: ForwardScope, deniedList: List<String?>? ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "您需要去应用程序设置当中手动开启权限",
                    "我已明白"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                requestCallback.onResult(
                    allGranted,
                    grantedList,
                    deniedList
                )
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = fragments
        for (i in fragments.indices) {
            fragments[i].onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragments = fragments
        for (i in fragments.indices) {
            fragments[i].onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val fragments = fragments
        for (i in fragments.indices) {
            fragments[i].onContextItemSelected(item)
        }
        return super.onContextItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val fragments = fragments
        for (i in fragments.indices) {
            fragments[i].onPrepareOptionsMenu(menu)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    val fragments: List<Fragment>
        get() {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            return navHostFragment.childFragmentManager.fragments
        }

    /**
     * 隐藏键盘
     */
    fun hideInput() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun disableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun enableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}