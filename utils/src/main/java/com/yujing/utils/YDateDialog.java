package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 日历对话框
 * @author 余静 2018年5月25日09:12:20
 */
@SuppressWarnings({"unused"})
public class YDateDialog {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Date initDate;
    private final Activity activity;
    private String format = "yyyy年MM月dd日 HH:mm";
    private boolean showDay = true;// 是否显示日
    private boolean showTime = true;// 是否显示时间
    private boolean showMonth = true;// 是否显示日
    private boolean cancelable = true;// 是否可以取消
    private OnClickListener onCancelClickListener;

    public YDateDialog(Activity activity) {
        this.activity = activity;
        //Theme_Holo_Light_Dialog_NoActionBar
        this.activity.setTheme(android.R.style.Theme_Holo_Light);
    }

    // 设置format
    public void setFormat(String format) {
        this.format = format;
    }

    // 初始化时间
    public void initTime(String initDateTime) {
        this.initDateTime = initDateTime;
    }

    // 初始化时间
    public void initTime(Date initDateTime) {
        this.initDate = initDateTime;
    }

    // 是否显示日
    public boolean isShowDay() {
        return showDay;
    }

    // 设置是否显示日
    public void setShowDay(boolean showDay) {
        this.showDay = showDay;
    }

    // 是否显示时间
    public boolean isShowTime() {
        return showTime;
    }

    // 设置是否显示时间
    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public void show(final DataListener dataListener) {
        //---------------------------------------------设置布局开始------------------------------------
        // 创建一个布局
        LinearLayout dateTimeLayout = new LinearLayout(activity);
        dateTimeLayout.setOrientation(LinearLayout.VERTICAL);
        // 实例化日期选择
        datePicker = new DatePicker(activity);
        datePicker.setCalendarViewShown(false);// 不显示周列表
        dateTimeLayout.addView(datePicker);// 添加到容器
        // 实例化时间选择
        timePicker = new TimePicker(activity);
        dateTimeLayout.addView(timePicker);
        //---------------------------------------------布局设置完毕------------------------------------
        //---------------------------------------------计算时间开始------------------------------------
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (initDateTime != null) {
            try {
                calendar.setTime(Objects.requireNonNull(sdf.parse(initDateTime)));
            } catch (ParseException e) {
                Log.e("YDateDialog", Objects.requireNonNull(e.getMessage()));
            }
        }
        if (initDate != null) {
            calendar.setTime(initDate);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> setTitle());
        //---------------------------------------------计算时间完毕------------------------------------
        // 判断是否显示时间
        timePicker.setVisibility(showTime ? View.VISIBLE : View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> setTitle());
        try {
            // 判断是否显示日
            if (showDay) {
                ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.VISIBLE);
            } else {
                ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
            if (showMonth) {
                ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
            } else {
                ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
                ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("YDateDialog", "日历不兼容");
        }

        //初始化弹窗时间
        ad = new AlertDialog.Builder(activity).setTitle(sdf.format(calendar.getTime())).setView(dateTimeLayout).setPositiveButton("设置", (dialog, whichButton) -> {
            if (dataListener != null) {
                String yyyy, MM, dd, HH, mm;
                yyyy = "" + datePicker.getYear();
                MM = "" + ((datePicker.getMonth() < 9) ? ("0" + (datePicker.getMonth() + 1)) : (datePicker.getMonth() + 1));
                dd = "" + ((datePicker.getDayOfMonth() < 10) ? ("0" + (datePicker.getDayOfMonth())) : (datePicker.getDayOfMonth()));
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    HH = "" + ((timePicker.getHour() < 10) ? ("0" + (timePicker.getHour())) : (timePicker.getHour()));
                    mm = "" + ((timePicker.getMinute() < 10) ? ("0" + (timePicker.getMinute())) : (timePicker.getMinute()));
                } else {
                    HH = "" + ((timePicker.getCurrentHour() < 10) ? ("0" + (timePicker.getCurrentHour())) : (timePicker.getCurrentHour()));
                    mm = "" + ((timePicker.getCurrentMinute() < 10) ? ("0" + (timePicker.getCurrentMinute())) : (timePicker.getCurrentMinute()));
                }
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                dataListener.getDataTime(dateTime, calendar1, calendar1.getTime(), yyyy, MM, dd, HH, mm);
            }
        }).setNegativeButton("取消", (dialog, which) -> {
            if (onCancelClickListener != null) {
                onCancelClickListener.onClick(dialog, which);
            }
            ad.dismiss();
        }).show();
        ad.setCancelable(cancelable);
        setTitle();
    }

    //更新title
    public void setTitle() {
        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
        } else {
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);
    }

    // 是否显示月份
    public boolean isShowMonth() {
        return showMonth;
    }

    // 设置是否显示月份
    public void setShowMonth(boolean showMonth) {
        this.showMonth = showMonth;
    }

    public interface DataListener {
        @SuppressWarnings("EmptyMethod")
        void getDataTime(String format, Calendar calendar, Date date, String yyyy, String MM, String dd, String HH, String mm);
    }

    public static void TEST(Activity activity) {
        YDateDialog yDateDialog = new YDateDialog(activity);
        yDateDialog.setFormat("yyyy年MM月dd日");// 设置日期格式（如："yyyy年MM月dd日HH:mm"）
        yDateDialog.initTime("2018年6月27日");//设置初始化日期，必须和设置格式相同（如："2016年07月01日15:19"）
        yDateDialog.setShowDay(false);// 设置是否显示日滚轮,默认显示
        yDateDialog.setShowTime(false);// 设置是否显示时间滚轮,默认显示
        yDateDialog.setShowMonth(true);// 设置是否显示时间滚轮,默认显示
        yDateDialog.show((format, calendar, date, yyyy, MM, dd, HH, mm) -> {

        });
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public OnClickListener getOnCancelClickListener() {
        return onCancelClickListener;
    }

    public void setOnCancelClickListener(OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }
}
