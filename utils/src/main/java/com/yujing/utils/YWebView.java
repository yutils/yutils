package com.yujing.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView常用设置
 * @author yujing 2018年11月30日12:07:59
 */
@SuppressWarnings("unused")
public class YWebView {
    /**
     * 初始化WebView
     * @param  webView webView
     * @param url url地址
     */
    public static void init(WebView webView, String url) {
        setSettings(webView);
        setBackgroundAlpha(webView);
        setClient(webView);
        webView.loadUrl(url);
    }

    /**
     * 设置WebView背景透明
     * @param webView webView
     */
    public static void setBackgroundAlpha(WebView webView) {
        try {
            webView.setBackgroundColor(0); // 设置背景色
            webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        } catch (Exception e) {
            Log.e("webView", "WebView背景设置透明错误，请在布局文件中添加android:background=\"#00000000\"", e);
        }
    }

    /**
     * 设置WebView跳转拦截
     * @param webView webView
     */
    public static void setClient(WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    String url = request.getUrl().toString();
                    if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:") || url.startsWith("smsto:")) {
                        //Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        //startActivity(intent);
                        return true;
                    }
                    view.loadUrl(url);
                }
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }
        });
    }

    /**
     * 设置WebView
     * @param webView webView
     */
    @SuppressLint({"SetJavaScriptEnabled"})
    public static void setSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);// 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);// 设置可以访问文件
        webSettings.setBuiltInZoomControls(true);// 设置支持缩放
        webSettings.setDisplayZoomControls(false);// 设定缩放控件隐藏
        webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportZoom(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//允许其加载混合网络协议内容即可,https和http混合页面
        }
    }
}
