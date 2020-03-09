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
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 图片显示对话框
 *
 * @author yujing 2019年8月1日11:47:34
 */
@SuppressWarnings({"unused"})
public class YImageDialog extends Dialog {
    private ImageView imageView;
    private boolean cancel = true;
    @SuppressLint("StaticFieldLeak")
    private static YImageDialog yDialog = null;
    private LinearLayout linearLayout;

    // 构造函数
    public YImageDialog(Activity activity) {
        super(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        linearLayout = getView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(linearLayout);// 设置布局view
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);// 设置Gravity居中
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;// 透明度
            lp.dimAmount = 0f;// 模糊度
            window.setAttributes(lp);
            //设置 window的Background为圆角
            GradientDrawable gradientDrawable = new GradientDrawable();
            int strokeWidth = 1; // 1dp 边框宽度
            int roundRadius = 0; // 6dp 圆角半径
            int strokeColor = Color.parseColor("#C0000000");//内部填充颜色
            int fillColor = Color.parseColor("#B0000000");//边框颜色
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
        linearLayout.setMinimumHeight(YScreenUtil.getScreenHeight(getContext()));//最小高度
        linearLayout.setMinimumWidth(YScreenUtil.getScreenWidth(getContext()));//最小宽度
        //创建imageView
        imageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageViewLayoutParams.gravity = Gravity.CENTER;//设置中心对其
        imageViewLayoutParams.weight = 1;
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
        yDialog.getImageView().setImageBitmap(bitmap);
        yDialog.setCancelable(cancelable);
        yDialog.show();
    }

    public synchronized static void show(Activity activity, int resource, boolean cancelable) {
        finish();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        yDialog = new YImageDialog(activity);
        yDialog.getImageView().setImageResource(resource);
        yDialog.setCancelable(cancelable);
        yDialog.show();
    }

    public synchronized static void show(Activity activity, Drawable drawable, boolean cancelable) {
        finish();
        if (activity == null || activity.isFinishing() || drawable == null) {
            return;
        }
        yDialog = new YImageDialog(activity);
        yDialog.getImageView().setImageDrawable(drawable);
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

    /**
     * 顾名思义
     */
    private static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
