package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 图片显示对话框
 * @author yujing 2020年3月21日18:23:58
 */
@SuppressWarnings({"unused"})
public class YImageDialog extends Dialog {
    private static boolean defaultFullScreen = false;
    private Boolean fullScreen;//全屏

    private Activity activity;
    private ImageView imageView;
    private boolean cancel = true;
    @SuppressLint("StaticFieldLeak")
    private static YImageDialog yDialog = null;
    private DisplayMetrics displayMetrics;
    private Bitmap bitmap;//图片
    private Integer resource;//图片
    private Drawable drawable;//图片

    // 构造函数
    public YImageDialog(Activity activity) {
        super(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        this.activity = activity;

        displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) manager.getDefaultDisplay().getRealMetrics(displayMetrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = getView();

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (resource != null) {
            imageView.setImageResource(resource);
        } else if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }

        setContentView(linearLayout);// 设置布局view
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);// 设置Gravity居中
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;// 透明度
            lp.dimAmount = 0f;// 模糊度
            if (fullScreen != null && fullScreen) {
                lp.height = displayMetrics.heightPixels;
                lp.width = displayMetrics.widthPixels;
            }
            window.setAttributes(lp);
            //设置 window的Background为圆角
            GradientDrawable gradientDrawable = new GradientDrawable();
            int strokeWidth = 1; // 1dp 边框宽度
            int roundRadius = 0; // 6dp 圆角半径
            int strokeColor = Color.parseColor("#80000000");//内部填充颜色
            int fillColor = Color.parseColor("#A0000000");//边框颜色
            gradientDrawable.setColor(fillColor);
            gradientDrawable.setCornerRadius(dip2px(getContext(), roundRadius));
            gradientDrawable.setStroke(dip2px(getContext(), strokeWidth), strokeColor);
            window.setBackgroundDrawable(gradientDrawable);
        }
        setCancelable(cancel);// 是否允许按返回键
        //imgView = (ImageView) linearLayout.getChildAt(0);
        setCanceledOnTouchOutside(cancel);// 触摸屏幕其他区域不关闭对话框
        linearLayout.setOnClickListener(v -> {
            if (cancel) {
                dismiss();
                finish();
            }
        });
    }

    //获取布局
    private LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);//设置纵向布局
        //linearLayout.setPadding(dip2px(getContext(), 10), dip2px(getContext(), 10), dip2px(getContext(), 10), dip2px(getContext(), 10));
        linearLayout.setMinimumHeight(displayMetrics.heightPixels);//最小高度
        linearLayout.setMinimumWidth(displayMetrics.widthPixels);//最小宽度
        //创建imageView
        imageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageViewLayoutParams.gravity = Gravity.CENTER;//设置中心对其
        imageViewLayoutParams.weight = 1;
        imageViewLayoutParams.setMargins((int) (displayMetrics.widthPixels * 0.05), (int) (displayMetrics.heightPixels * 0.05), (int) (displayMetrics.widthPixels * 0.05), (int) (displayMetrics.heightPixels * 0.05));
        imageView.setLayoutParams(imageViewLayoutParams);
        linearLayout.addView(imageView);
        return linearLayout;
    }

    public synchronized static void show(Activity activity, Bitmap bitmap) {
        show(activity, bitmap, true);
    }

    public synchronized static void show(Activity activity, int resource) {
        show(activity, resource, true);
    }

    public synchronized static void show(Activity activity, Drawable drawable) {
        show(activity, drawable, true);
    }

    public synchronized static void show(Activity activity, Bitmap bitmap, boolean cancelable) {
        finish();
        if (activity == null || activity.isFinishing() || bitmap == null) {
            return;
        }
        yDialog = new YImageDialog(activity);
        yDialog.setBitmap(bitmap);
        yDialog.setCancelable(cancelable);
        yDialog.show();
    }

    public synchronized static void show(Activity activity, int resource, boolean cancelable) {
        finish();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        yDialog = new YImageDialog(activity);
        yDialog.setResource(resource);
        yDialog.setCancelable(cancelable);
        yDialog.show();
    }

    public synchronized static void show(Activity activity, Drawable drawable, boolean cancelable) {
        finish();
        if (activity == null || activity.isFinishing() || drawable == null) {
            return;
        }
        yDialog = new YImageDialog(activity);
        yDialog.setDrawable(drawable);
        yDialog.setCancelable(cancelable);
        yDialog.show();
    }

    /**
     * 关闭对话框
     */
    public static void finish() {
        if (yDialog != null) {
            yDialog.dismiss();
        }
    }

    @Override
    public void show() {
        if (activity == null || activity.isFinishing())
            return;
        finish();
        if (fullScreen == null) fullScreen = defaultFullScreen;
        if (fullScreen) {
            //主要作用是焦点失能和焦点恢复，保证在弹出dialog时不会弹出虚拟按键且事件不会穿透。
            if (this.getWindow() != null) {
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                super.show();
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }else {
                super.show();
            }
        } else {
            super.show();
        }
    }

    public boolean isCancel() {
        return cancel;
    }

    public YImageDialog setCancel(boolean cancel) {
        this.cancel = cancel;
        return this;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public YImageDialog setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public Integer getResource() {
        return resource;
    }

    public YImageDialog setResource(Integer resource) {
        this.resource = resource;
        return this;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public YImageDialog setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public Boolean getFullScreen() {
        return fullScreen;
    }

    public YImageDialog setFullScreen(Boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public static boolean isDefaultFullScreen() {
        return defaultFullScreen;
    }

    public static void setDefaultFullScreen(boolean defaultFullScreen) {
        YImageDialog.defaultFullScreen = defaultFullScreen;
    }

    /**
     * 顾名思义
     */
    private static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}