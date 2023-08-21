package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;

import com.yujing.contract.YListener1;

/**
 * 半透明等待对话框，单例模式
 *
 * @author 余静 2020年3月20日10:44:41
 */
/*
用法：

//弹出 正在加载 转圈圈
YShow.show("正在加载")
//弹出 正在加载 转圈圈 下面显示请稍后
YShow.show("正在加载","请稍后...")
//弹出 正在加载 转圈圈 下面显示请稍后，并且不允许关闭
YShow.show("正在加载","请稍后...",false)
//更新文字
YShow.setMessage("正在加载") //YShow.getDialog().setMessage1("正在加载")
YShow.setMessageOther("加载进度 50%") //YShow.getDialog().setMessage2("加载进度 50%")

//关闭 对话框
YShow.finish()


val ys = YShow.show("AAAA", "BBBB")
ys.setMessage1("CCCC") //修改文本内容

//onCreate创建完成监听
ys.setCreatedListener {
    it.setMessage2("DDDD")
}

//自定义view
ys.rootView.apply {
    removeAllViews()
    setBackgroundColor(Color.GREEN)//修改背景
    addView(TextView(context).apply {
        text = "666"
        setTextColor(Color.BLUE)
    })
}

//点击事件
ys.setOnClickListener {
    YToast.show("被点击了")
}
 */
@SuppressWarnings({"unused"})
public class YShow extends Dialog {
    public static volatile boolean defaultFullScreen = false;
    public Activity activity;
    public ProgressBar mProgressBar;//进度
    public CharSequence message1;
    public CharSequence message2;
    public LinearLayout rootView;
    public TextView textView1;
    public TextView textView2;
    public boolean canCancel = true;//可以按返回键
    public Boolean fullScreen;//全屏
    //onCreate执行完毕后回调
    private YListener1<YShow> createdListener = null;
    //view点击监听
    public View.OnClickListener onClickListener = null;

    @SuppressLint("StaticFieldLeak")
    private static volatile YShow yDialog = null;

    private volatile double tag = 0.0; //给当前对象一个编号


    private YShow(Activity activity) {
        super(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        this.activity = activity;
    }

    // 构造函数，主题android.R.style.Theme_Holo_Light_Dialog_NoActionBar,Theme_DeviceDefault_Dialog_NoActionBar,Theme_Material_Dialog_NoActionBar
    private YShow(Activity activity, CharSequence text, CharSequence text2, boolean canCancel) {
        super(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        this.activity = activity;
        this.message1 = text;
        this.message2 = text2;
        this.canCancel = canCancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getView();
        rootView.setOnClickListener(onClickListener);
        setContentView(rootView);// 设置布局view
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);// 设置Gravity居中
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.9f;// 透明度
            lp.dimAmount = 0f;// 模糊度
            //lp.width=dip2px(getContext(), 90);
            window.setAttributes(lp);//应用设置
            //设置 window的Background为圆角
            GradientDrawable gradientDrawable = new GradientDrawable();
            int strokeWidth = 1; // 1dp 边框宽度
            int roundRadius = 5; // 6dp 圆角半径
            int strokeColor = Color.parseColor("#303A89FF");//边框颜色
            int fillColor = Color.parseColor("#A0000000");//内部填充颜色
            gradientDrawable.setColor(fillColor);
            gradientDrawable.setCornerRadius(dip2px(getContext(), roundRadius));
            gradientDrawable.setStroke(dip2px(getContext(), strokeWidth), strokeColor);
            window.setBackgroundDrawable(gradientDrawable);
        }
        setCancelable(canCancel);// 是否允许按返回键
        //找到mProgressBar
        mProgressBar = (ProgressBar) rootView.getChildAt(0);
        //找到textView1
        textView1 = (TextView) rootView.getChildAt(1);
        //找到textView2
        textView2 = (TextView) rootView.getChildAt(2);
        setMessage1(message1);
        setMessage2(message2);
        setCanceledOnTouchOutside(false);// 触摸屏幕其他区域不关闭对话框
        if (createdListener != null) createdListener.value(this);
    }


    @IdRes
    public static int rootId;
    @IdRes
    public static int id1;
    @IdRes
    public static int id2;
    @IdRes
    public static int id3;

    //获取布局
    private LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.removeAllViews();
        linearLayout.setId(rootId);
        linearLayout.setOrientation(LinearLayout.VERTICAL);//设置纵向布局
        linearLayout.setPadding(dip2px(getContext(), 10), dip2px(getContext(), 15), dip2px(getContext(), 10), dip2px(getContext(), 10));
        linearLayout.setMinimumHeight(dip2px(getContext(), 90));//最小高度
        linearLayout.setMinimumWidth(dip2px(getContext(), 90));//最小宽度

        LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageViewLayoutParams.gravity = Gravity.CENTER;//设置中心对其
        //实例化一个ProgressBar
        ProgressBar mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
        mProgressBar.setId(id1);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setLayoutParams(imageViewLayoutParams);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.setMargins(0, 0, 0, 0);
        tvParams.gravity = Gravity.CENTER;//设置中心
        //实例化一个TextView
        TextView tv = new TextView(getContext());
        mProgressBar.setId(id2);
        tv.setLayoutParams(tvParams);
        tv.setTextSize(12);
        tv.setTextColor(Color.parseColor("#EEEEEE"));
        tv.setText(message1);

        LinearLayout.LayoutParams tvParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams2.setMargins(0, dip2px(getContext(), 2), 0, 0);
        tvParams2.gravity = Gravity.CENTER;//设置中心
        //实例化第二个TextView
        TextView tv2 = new TextView(getContext());
        mProgressBar.setId(id3);
        tv2.setLayoutParams(tvParams2);
        tv2.setTextSize(10);
        tv2.setTextColor(Color.parseColor("#EEEEEE"));
        tv2.setText(message2);

        linearLayout.addView(mProgressBar);
        linearLayout.addView(tv);
        linearLayout.addView(tv2);
        return linearLayout;
    }

    public YShow setMessage1(CharSequence message) {
        this.message1 = message;
        YThread.runOnUiThread(() -> {
            if (textView1 != null) {
                textView1.setText(this.message1);
                textView1.setVisibility(message1 == null || message1.length() == 0 ? View.GONE : View.VISIBLE);// 没有值就隐藏textView
            }
        });
        return this;
    }

    public YShow setMessage2(CharSequence message) {
        this.message2 = message;
        YThread.runOnUiThread(() -> {
            if (textView2 != null) {
                textView2.setText(this.message2);
                textView2.setVisibility(message2 == null || message2.length() == 0 ? View.GONE : View.VISIBLE);
            }
        });
        return this;
    }

    public void setCreatedListener(YListener1<YShow> createdListener) {
        this.createdListener = createdListener;
        if (rootView != null) {
            YThread.runOnUiThread(() -> {
                createdListener.value(this);
            });
        }
    }

    public YShow setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        this.setCancelable(canCancel);
        return this;
    }

    public YShow setFullScreen(Boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public YShow setProgressBarColor(@ColorInt int color) {
        if (mProgressBar != null) {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
            YThread.runOnUiThread(() -> {
                mProgressBar.getIndeterminateDrawable().setColorFilter(colorFilter);
            });
        }
        return this;
    }

    public CharSequence getMessage1() {
        return message1;
    }

    public CharSequence getMessage2() {
        return message2;
    }

    //点击事件
    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (yDialog != null) {
            if (rootView == null) {
                this.onClickListener = onClickListener;
            } else {
                rootView.setOnClickListener(onClickListener);
            }
        }
    }

    @Override
    public void show() {
        if (activity == null || activity.isFinishing()) return;
        finish();
        YThread.runOnUiThread(() -> {
            if (fullScreen == null) fullScreen = defaultFullScreen;
            if (fullScreen) {
                //主要作用是焦点失能和焦点恢复，保证在弹出dialog时不会弹出虚拟按键且事件不会穿透。
                if (this.getWindow() != null) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    super.show();
                    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                } else {
                    super.show();
                }
            } else {
                super.show();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    // ------------------------------------------------------- static -------------------------------------------------------
    @Deprecated
    public static YShow showUpdate(Activity activity) {
        return showUpdate(activity, null, null, true, null);
    }

    @Deprecated
    public static YShow showUpdate(Activity activity, CharSequence message) {
        return showUpdate(activity, message, null, true, null);
    }

    @Deprecated
    public static YShow showUpdate(Activity activity, CharSequence message1, CharSequence message2) {
        return showUpdate(activity, message1, message2, true, null);
    }

    @Deprecated
    public static YShow showUpdate(Activity activity, CharSequence message, boolean canCancel) {
        return showUpdate(activity, message, null, canCancel, null);
    }

    @Deprecated
    public static YShow showUpdate(Activity activity, CharSequence message1, CharSequence message2, boolean canCancel) {
        return showUpdate(activity, message1, message2, canCancel, null);
    }

    @Deprecated
    public static YShow showUpdate(Activity activity, CharSequence message1, CharSequence message2, boolean canCancel, Boolean fullScreen) {
        return show(activity, message1, message2, canCancel, fullScreen);
    }


    public static YShow show(CharSequence message) {
        return show(YActivityUtil.getCurrentActivity(), message, null, true);
    }

    public static YShow show(CharSequence message1, CharSequence message2) {
        return show(YActivityUtil.getCurrentActivity(), message1, message2, true);
    }

    public static YShow show(CharSequence message, boolean canCancel) {
        return show(YActivityUtil.getCurrentActivity(), message, null, canCancel);
    }

    public static YShow show(CharSequence message1, CharSequence message2, boolean canCancel) {
        return show(YActivityUtil.getCurrentActivity(), message1, message2, canCancel, null);
    }

    public static YShow show(CharSequence message1, CharSequence message2, boolean canCancel, Boolean fullScreen) {
        return show(YActivityUtil.getCurrentActivity(), message1, message2, canCancel, fullScreen);
    }

    public static YShow show(Activity activity) {
        return show(activity, null, null, true);
    }

    public static YShow show(Activity activity, CharSequence message) {
        return show(activity, message, null, true);
    }

    public static YShow show(Activity activity, CharSequence message1, CharSequence message2) {
        return show(activity, message1, message2, true);
    }

    public static YShow show(Activity activity, CharSequence message, boolean canCancel) {
        return show(activity, message, null, canCancel);
    }

    public static YShow show(Activity activity, CharSequence message1, CharSequence message2, boolean canCancel) {
        return show(activity, message1, message2, canCancel, null);
    }

    public synchronized static YShow show(Activity activity, CharSequence message1, CharSequence message2, boolean canCancel, Boolean fullScreen) {
        if (isShow()) {
            YThread.runOnUiThread(() -> {
                setMessage(message1);
                setMessageOther(message2);
                setCancel(canCancel);
            });
        } else {
            if (YThread.isMainThread()) {
                yDialog = new YShow(activity, message1, message2, canCancel);
                yDialog.setFullScreen(fullScreen);
                yDialog.show();
            } else {
                double newTag = Math.random();
                YThread.runOnUiThread(() -> {
                    //等待初始化完成后运行，确保yDialog!=null,转同步
                    yDialog = new YShow(activity, message1, message2, canCancel);
                    yDialog.tag = newTag;
                    yDialog.setFullScreen(fullScreen);
                    yDialog.show();
                });
                //确保yDialog new成功
                do {
                    YThread.delay(1);
                } while (yDialog == null || yDialog.tag != newTag);
            }
        }
        return yDialog;
    }

    //设置文本
    public static void setMessage(CharSequence message) {
        if (yDialog != null) yDialog.setMessage1(message);
    }

    public static void setMessageOther(CharSequence message) {
        if (yDialog != null) yDialog.setMessage2(message);
    }

    public static void setColor(@ColorInt int color) {
        if (yDialog != null) yDialog.setProgressBarColor(color);
    }

    public static void setCancel(boolean canCancel) {
        if (yDialog != null) yDialog.setCancelable(canCancel);// 是否允许按返回键
    }

    public static boolean isDefaultFullScreen() {
        return defaultFullScreen;
    }

    public static void setDefaultFullScreen(boolean defaultFullScreen) {
        YShow.defaultFullScreen = defaultFullScreen;
    }

    /**
     * 获取Dialog
     *
     * @return YShow
     */
    public static YShow getDialog() {
        return yDialog;
    }

    /**
     * 是否正在显示
     *
     * @return 是否正在显示
     */
    public static boolean isShow() {
        return yDialog != null && yDialog.isShowing();
    }

    /**
     * 关闭对话框
     */
    public static void finish() {
        if (yDialog != null) yDialog.dismiss();
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
