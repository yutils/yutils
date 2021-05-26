package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Objects;

/**
 * 屏幕工具类，涉及到屏幕宽度、高度、密度比、(像素、dp、sp)之间的转换等。
 *
 * @author 余静 2018年11月30日12:11:39
 */
@SuppressWarnings("unused")
public class YScreenUtil {
    /**
     * 获取屏幕宽度，单位为px
     *
     * @param context 应用程序上下文
     * @return 屏幕宽度，单位px
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度，单位为px
     *
     * @param context 应用程序上下文
     * @return 屏幕高度，单位px
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取DPI
     * @param context context
     * @return Dpi
     */
    public static float getDensityDpi(Context context) {
        return getDisplayMetrics(context).densityDpi;
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

    /**
     * 获取系统字体sp密度值
     *
     * @param context context
     * @return float密度
     */
    public static float getScaledDensity(Context context) {
        return getDisplayMetrics(context).scaledDensity;
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

    /**
     * 获取屏幕宽度
     *
     * @param activity 页面
     * @return 宽度
     */
    public static int getWidthPixels(Activity activity) {
        DisplayMetrics dm = (new DisplayMetrics());
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity 页面
     * @return 宽度
     */
    public static int getHeightPixels(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context context
     * @return 高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(Objects.requireNonNull(clazz.getField("status_bar_height").get(object)).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
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
}