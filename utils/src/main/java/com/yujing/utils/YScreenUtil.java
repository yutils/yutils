package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 屏幕工具类，涉及到屏幕宽度、高度、密度比、(像素、dp、sp)之间的转换等。
 *
 * @author 余静 2018年11月30日12:11:39
 */
@SuppressWarnings("unused")
public class YScreenUtil {

    /**
     * 设置成横屏
     *
     * @param activity activity
     */
    public static void toPortrait(Activity activity) {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 设置成竖屏
     *
     * @param activity activity
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public static void toLandscape(Activity activity) {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 设置成自动旋转,用户启用自动旋转才会生效
     *
     * @param activity activity
     */
    public static void toAuto(Activity activity) {
        if (activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * 设置全屏
     *
     * @param activity     页面
     * @param isFullScreen 全屏否
     */
    public static void setFullScreen(Activity activity, boolean isFullScreen) {
        if (isFullScreen) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏通知栏,通知栏透明:FLAG_FORCE_NOT_FULLSCREEN
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//显示通知栏
        }
    }

    /**
     * 设置开启沉浸式
     *
     * @param activity     页面
     * @param isFullScreen 沉浸式否
     */
    public static void setImmersive(Activity activity, boolean isFullScreen) {
        if (isFullScreen) {
            if (Build.VERSION.SDK_INT >= 19) {
                Window window = activity.getWindow();
                //沉浸式
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                //关闭输入法后，不显示虚拟按键
                WindowManager.LayoutParams params = window.getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
                window.setAttributes(params);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                View decorView = activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    /**
     * 设置屏幕变暗
     *
     * @param activity activity
     * @param alpha    0.0-1.0 ，0.0为黑 1.0为亮
     */
    public static void setAlpha(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取屏幕最小宽度
     *
     * @param context context
     * @return 应该是dp
     */
    public static int getSmallestScreenWidthDp(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp;
    }

    public static int getSmallestScreenWidthDp() {
        return getSmallestScreenWidthDp(YApp.get());
    }

    /**
     * 获取屏幕宽度（物理），单位为px
     *
     * @param context 应用程序上下文
     * @return 屏幕宽度，单位px
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenWidth() {
        return getScreenWidth(YApp.get());
    }

    /**
     * 获取屏幕高度（物理），单位为px
     *
     * @param context 应用程序上下文
     * @return 屏幕高度，单位px
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getScreenHeight() {
        return getScreenHeight(YApp.get());
    }

    /**
     * 获取当前应用屏幕宽度
     *
     * @param activity 页面
     * @return 宽度
     */
    public static int getScreenWidthCurrent(Activity activity) {
        DisplayMetrics dm = (new DisplayMetrics());
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取当前应用屏幕高度
     *
     * @param activity 页面
     * @return 宽度
     */
    public static int getScreenHeightCurrent(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取DPI
     *
     * @param context context
     * @return Dpi
     */
    public static float getDensityDpi(Context context) {
        return getDisplayMetrics(context).densityDpi;
    }

    public static float getDensityDpi() {
        return getDensityDpi(YApp.get());
    }

    /**
     * 获取系统dp尺寸密度值
     *
     * @param context context
     * @return float密度
     */
    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    public static float getDensity() {
        return getDensity(YApp.get());
    }

    /**
     * 获取系统字体sp密度值
     *
     * @param context context
     * @return float密度
     */
    public static float getScaledDensity(Context context) {
        return getDisplayMetrics(context).scaledDensity;
    }

    public static float getScaledDensity() {
        return getScaledDensity(YApp.get());
    }

    /**
     * dip转换为px大小
     *
     * @param context 应用程序上下文
     * @param dpValue dp值
     * @return 转换后的px值
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * getDensity(context) + 0.5f);
    }

    /**
     * dip转px
     *
     * @param dpValue 传入dip
     * @return 转换后的px
     */
    public static int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转换为dp值
     *
     * @param context 应用程序上下文
     * @param pxValue px值
     * @return 转换后的dp值
     */
    public static int px2dp(Context context, int pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * px转dip
     *
     * @param pxValue px
     * @return dp
     */
    public static int px2dp(int pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转换为px
     *
     * @param context 应用程序上下文
     * @param spValue sp值
     * @return 转换后的px值
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * getScaledDensity(context) + 0.5f);
    }

    public static int sp2px(float spValue) {
        return sp2px(YApp.get(), spValue);
    }

    /**
     * px转换为sp
     *
     * @param context 应用程序上下文
     * @param pxValue px值
     * @return 转换后的sp值
     */
    public static int px2sp(Context context, int pxValue) {
        return (int) (pxValue / getScaledDensity(context) + 0.5f);
    }

    public static int px2sp(int pxValue) {
        return px2sp(YApp.get(), pxValue);
    }

    /**
     * 获得状态栏（顶部）的高度
     *
     * @param context context
     * @return 高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusHeight = resources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static int getStatusHeight() {
        return getStatusHeight(YApp.get());
    }

    /**
     * 获得导航栏（底部）的高度
     *
     * @param context context
     * @return 高度
     */
    public static int getNavigationHeight(Context context) {
        int navigationBarHeight = -1;
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = resources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return navigationBarHeight;
    }

    public static int getNavigationHeight() {
        return getNavigationHeight(YApp.get());
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity activity
     * @return Bitmap
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(bmp, 0, 0, width, height);
        decorView.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity activity
     * @return Bitmap
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(bmp, 0, statusHeight, width, height - statusHeight);
        decorView.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 获取DisplayMetrics对象
     *
     * @param context context
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (manager != null)
            manager.getDefaultDisplay().getRealMetrics(metrics);//manager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static DisplayMetrics getDisplayMetrics() {
        return getDisplayMetrics(YApp.get());
    }
}