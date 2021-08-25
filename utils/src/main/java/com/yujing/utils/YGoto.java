package com.yujing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;

/**
 * 跳转到常用页面
 * @author 余静 2021年3月10日13:13:43
 */

/*用法
//初始化
YUtils.init(this)
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
    public static final int INSTALL_APP_CODE = 8899;
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
     * 跳转到打电话
     *
     * @param activity activity
     * @param phone    电话号码
     */
    public static void makeCall(Activity activity, String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        activity.startActivity(intent);
    }

    /**
     * 跳转到发短信
     *
     * @param activity activity
     * @param tel      电话号码
     * @param content  内容
     */
    public static void sendSMS(Activity activity, String tel, String content) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(tel)) {
            //noinspection SpellCheckingInspection
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel));
            intent.putExtra("sms_body", content);
            activity.startActivity(intent);
        }
    }

    /**
     * 跳转到悬浮窗设置
     *
     * @param context context
     */
    public static void toOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到运行安装第三方APP权限设置
     * @param context context
     */
    public static void toInstallApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0以上
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            if (context instanceof Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity) context).startActivityForResult(intent, INSTALL_APP_CODE);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
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
