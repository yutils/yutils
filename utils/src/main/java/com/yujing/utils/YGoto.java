package com.yujing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * 跳转到常用页面
 */
/*
com.android.settings.AccessibilitySettings 辅助功能设置
com.android.settings.ActivityPicker 选择活动
com.android.settings.ApnSettings APN设置
com.android.settings.ApplicationSettings 应用程序设置
com.android.settings.BandMode 设置GSM/UMTS波段
com.android.settings.BatteryInfo 电池信息
com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
com.android.settings.DeviceAdminSettings 设备管理器
com.android.settings.DeviceInfoSettings 关于手机
com.android.settings.Display 显示——设置显示字体大小及预览
com.android.settings.DisplaySettings 显示设置
com.android.settings.DockSettings 底座设置
com.android.settings.IccLockSettings SIM卡锁定设置
com.android.settings.InstalledAppDetails 语言和键盘设置
com.android.settings.LanguageSettings 语言和键盘设置
com.android.settings.LocalePicker 选择手机语言
com.android.settings.LocalePickerInSetupWizard 选择手机语言
com.android.settings.ManageApplications 已下载（安装）软件列表
com.android.settings.MasterClear 恢复出厂设置
com.android.settings.MediaFormat 格式化手机闪存
com.android.settings.PhysicalKeyboardSettings 设置键盘
com.android.settings.PrivacySettings 隐私设置
com.android.settings.ProxySelector 代理设置
com.android.settings.RadioInfo 手机信息
com.android.settings.RunningServices 正在运行的程序（服务）
com.android.settings.SecuritySettings 位置和安全设置
com.android.settings.Settings 系统设置
com.android.settings.SettingsSafetyLegalActivity 安全信息
com.android.settings.SoundSettings 声音设置
com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
com.android.settings.TetherSettings 绑定与便携式热点
com.android.settings.TextToSpeechSettings 文字转语音设置
com.android.settings.UsageStats 使用情况统计
com.android.settings.UserDictionarySettings 用户词典
com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
com.android.settings.WirelessSettings 无线和网络设置
 */
public class YGoto {
    /**
     * 跳转到设置界面
     *
     * @param context context
     */
    public static void toSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到本APP详细信息
     *
     * @param context context
     */
    public static void toDetails(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        // 根据包名打开对应的设置界面
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到蓝牙
     *
     * @param context context
     */
    public static void toBluetooth(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转到移动网络设置界面
     *
     * @param context context
     */
    public static void toRoaming(Context context) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转日期设置
     *
     * @param context context
     */
    public static void toDate(Context context) {
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转手机显示设置界面（亮度，休眠时间）
     *
     * @param context context
     */
    public static void toDisplay(Context context) {
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转语言和输入设备
     *
     * @param context context
     */
    public static void toInput(Context context) {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转位置服务界面
     *
     * @param context context
     */
    public static void toLocation(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转选择网络运营商
     *
     * @param context context
     */
    public static void toNetwork(Context context) {
        Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转Wifi列表设置
     *
     * @param context context
     */
    public static void toWifi(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转IP设定界面
     *
     * @param context context
     */
    public static void toWifiIp(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_IP_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转开发人员选项界面
     *
     * @param context context
     */
    public static void toDevelopment(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到activity
     *
     * @param context context
     * @param cls     页面
     */
    public static <T extends Activity> void startActivity(Context context, Class<T> cls) {
        Intent intent = new Intent(context, cls);
        if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到activity
     *
     * @param cls 页面
     */
    public static <T extends Activity> void startActivity(Class<T> cls) {
        startActivity(YApp.get(), cls);
    }
}
