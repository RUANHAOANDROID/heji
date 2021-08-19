package com.rh.heji

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
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
import androidx.navigation.*
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.navigation.NavigationView
import com.lxj.xpopup.XPopup
import com.rh.heji.databinding.HeaderMainNavBinding
import com.rh.heji.ui.home.DrawerSlideListener
import com.rh.heji.ui.home.HomeDrawerListener
import com.rh.heji.ui.user.JWTParse
import com.rh.heji.ui.user.JWTParse.getUser
import com.rh.heji.utlis.checkPermissions
import com.rh.heji.utlis.permitDiskReads
import kotlinx.coroutines.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController private set
    private lateinit var drawerLayout: DrawerLayout
    val mainViewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private lateinit var navHeaderMainBinding: HeaderMainNavBinding//侧拉头像
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        permitDiskReads { super.onCreate(savedInstanceState) }//StrictMode policy violation; ~duration=127 ms: android.os.strictmode.DiskReadViolation by XiaoMi
        checkPermissions(this) { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
            //初始化一些需要权限的功能
            App.getInstance().appViewModule.initCrashTool()
            Toast.makeText(this, "已同意权限", Toast.LENGTH_SHORT).show()
        }
        setContentView(R.layout.activity_main)
        initDrawerLayout()
        lifecycleScope.launch(Dispatchers.IO) {
            val token = App.getInstance().token.decodeToken()
            withContext(Dispatchers.Main) {
                if (TextUtils.isEmpty(token)) {
                    navController.navigate(R.id.nav_login)
                } else {
                    val user = getUser(token)
                    setDrawerLayout(user)
                    App.getInstance().appViewModule.asyncData()
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
                runBlocking(Dispatchers.IO) { App.getInstance().token.delete() }
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
        navHeaderMainBinding = HeaderMainNavBinding.bind(navHeaderView)
        navHeaderView.setOnClickListener { v: View? ->
            ToastUtils.showLong("")
            navController.navigate(R.id.nav_user_info)
            drawerLayout.closeDrawers()
        }
        setCurrentBook(App.getInstance().currentBook.name)
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
                    when (destination.label.toString()) {
                        home -> {
                            title = "记账"
                        }
                        report -> {

                        }
                        setting -> {

                        }
                    }
                    LogUtils.d(destination.label)
                }
            })
    }

    private fun setDrawerLayout(user: JWTParse.User) {
        navHeaderMainBinding.tvTitle.text = user.username
        navHeaderMainBinding.tvNice.text = user.auth.toString()
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

    fun setCurrentBook(bookName: String) {
        val bookItem = navigationView.menu.findItem(R.id.nav_book_list)
        val str1 = getString(R.string.menu_book)
        val str2 = "[$bookName]"
        val spannableString = SpannableString("$str1$str2")
        spannableString.setSpan(
            ForegroundColorSpan(getColor(R.color.textRemark)),
            str1.length,
            spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        bookItem.title = spannableString
    }

}