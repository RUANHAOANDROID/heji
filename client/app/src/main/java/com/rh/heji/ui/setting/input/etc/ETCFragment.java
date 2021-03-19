package com.rh.heji.ui.setting.input.etc;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.blankj.utilcode.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.rh.heji.ui.base.BaseFragment;
import com.rh.heji.R;
import com.rh.heji.databinding.FragmentEtcBinding;

import java.util.Calendar;

/**
 * Date: 2020/10/27
 * Author: 锅得铁ø
 * #
 */
public class ETCFragment extends BaseFragment {
    FragmentEtcBinding binding;
    ETCViewModel etcViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        etcViewModel = getViewModel(ETCViewModel.class);
    }
    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (binding.etcWeb.canGoBack()) {
                binding.etcWeb.goBack();
            } else {
                getMainActivity().getNavController().popBackStack();
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        //拦截回退直接退出  object : Class 内部类
        getMainActivity().getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);//指定owner 仅在该Fragment生命周期下有效
    }

    @Override
    protected void initView(View view) {
        binding = FragmentEtcBinding.bind(view);

        WebSettings webSettings = binding.etcWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        binding.etcWeb.loadUrl(etcViewModel.ETC_URL);
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view1, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view1, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {//拦截请求
               //拦截的URL "http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/getCardMonthStatInfo"
                //关键字
                String key ="showetcacount";
                //获取参数
                String requestUrl =request.getRequestHeaders().get("Referer");
                if (!TextUtils.isEmpty(requestUrl)&&requestUrl.contains(key)){
                    getParameters(requestUrl);
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view1, String url) {//加载完成
                super.onPageFinished(view1, url);
                LogUtils.i(url);
                String showetcsearch = "showetcsearch";//查询页面
                String showetcacountdetail = "showetcacountdetail";//详情列表
                String showetcacount = "showetcacount";//月统计页

                if (url.contains(showetcsearch)) {
                    inputValue(view1);//自动填充Value
                } else if (url.contains(showetcacountdetail)) {
                    //http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacountdetail/42021909230571219224/2020-09/%E9%84%82FNA518
                    getParameters(url);
                } else if (url.contains(showetcacount)) {
                    //http://hubeiweixin.u-road.com/HuBeiCityAPIServer/index.php/huibeicityserver/showetcacount/42021909230571219224/202009/%E9%84%82FNA518
                    getParameters(url);
                }

            }

            private void getParameters(String url) {
                String[] ps = url.split("/");
                String carID = ps[ps.length - 1];
                String yearMonth = ps[ps.length - 2];
                String etcID = ps[ps.length - 3];
                if (!yearMonth.contains("-")) {
                    String year = yearMonth.substring(0, 4);
                    String month = yearMonth.substring(year.length());
                    yearMonth = year + "-" + month;
                }
                etcViewModel.setCarID(carID);
                etcViewModel.setEtcID(etcID);
                etcViewModel.setYearMonth(yearMonth);
            }

        };
        binding.etcWeb.setWebViewClient(client);
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_etc;
    }

    @Override
    protected void setUpToolBar() {
        super.setUpToolBar();
        showBlack();
        getToolBar().setTitle("ETC账单");
        getToolBar().inflateMenu(R.menu.input);
        getToolBar().getMenu().findItem(R.id.item1).setTitle("导入");
        getToolBar().getMenu().findItem(R.id.item1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                requestAlert();
                return false;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * JS输入ETC卡号、车牌号
     */
    private void inputValue(WebView webView) {
        String functionInput = "javascript:" +
                " function inputNumber() { " +
                "   document.getElementById(\"cardnum\").value = '" + etcViewModel.DEF_ETC_ID + "';" +
                "   document.getElementById(\"vehplate\").value = '" + etcViewModel.DEF_CAR_ID + "';" +
                "}";
        webView.loadUrl(functionInput);//写入ETC卡号、车牌号
        //执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ValueCallback<String> stringValueCallback = value -> LogUtils.i(value);
            webView.evaluateJavascript("javascript: inputNumber();", stringValueCallback);
        } else {
            webView.loadUrl("javascript: inputNumber();");
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_input) {
            requestAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestAlert() {
        if (TextUtils.isEmpty(etcViewModel.getEtcID()) || TextUtils.isEmpty(etcViewModel.getCarID()) || TextUtils.isEmpty(etcViewModel.getYearMonth())) {
            //selectMonth();
            new XPopup.Builder(getContext())
                    .asConfirm("导入提示", "请先按月份查询账单", new OnConfirmListener() {
                        @Override
                        public void onConfirm() {

                        }
                    })
                    .show();
        } else {
            new XPopup.Builder(getContext())
                    .asConfirm("确认导入" + etcViewModel.getYearMonth() + "的账单吗？", "账单可能会有延迟，月初导入上月账单最佳", () -> {
                        LoadingPopupView inputLoading = new XPopup.Builder(getContext()).asLoading().setTitle("正在导入");
                        inputLoading.show();
                        etcViewModel.requestHBGSETCList(etcViewModel.getEtcID(), etcViewModel.getYearMonth(), etcViewModel.getCarID())
                                .observe(getViewLifecycleOwner(), message -> {
                                    inputLoading.setTitle(message);
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        inputLoading.dismiss();
                                    }, 1000);
                                });
                    })
                    .show();

        }
    }

    private void selectMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String[] months = {
                year + "-01",
                year + "-02",
                year + "-03",
                year + "-04",
                year + "-05",
                year + "-06",
                year + "-07",
                year + "-08",
                year + "-09",
                year + "-10",
                year + "-11",
                year + "-12",
        };
        new XPopup.Builder(getContext()).asBottomList("选择月份", months, new OnSelectListener() {
            @Override
            public void onSelect(int position, String text) {
                etcViewModel.setYearMonth(text);
            }
        }).show();
    }

}
