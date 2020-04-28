package com.yujing.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * 各种其他常用工具
 *
 * @author 余静 2018年5月18日14:52:58
 */
@SuppressWarnings("unused")
@SuppressLint("MissingPermission")
public class YUtils {

    /**
     * 重新计算listView高度
     *
     * @param listView 需要计算的对象
     */
    public static void resetListViewHeight(ListView listView) {
        // 提示：最底层容器要用LinearLayout
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
        // ((ScrollView)listView.getParent()).fullScroll(ScrollView.FOCUS_UP);
    }

    /**
     * 重新计算listView高度
     *
     * @param listView   需要计算的对象
     * @param maxHeight  最大高度
     * @param itemHeight 每个元素高度
     */
    public static void resetListViewHeight(ListView listView, Integer maxHeight, Integer itemHeight) {
        // 提示：最底层容器要用LinearLayout
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null)
                continue;
            if (listItem instanceof LinearLayout) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            } else {
                // 如果最底层不是LinearLayout就手动设置itemHeight
                try {
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                } catch (NullPointerException e) {
                    if (itemHeight != null)
                        totalHeight += itemHeight; // 这里写Item高度
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if (maxHeight != null && params.height > maxHeight) {
            params.height = maxHeight;
        }
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);
    }

    /**
     * 当前是否是debug模式
     *
     * @param context context
     * @return 是或否
     */
    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 获取设备的唯一驱动id
     *
     * @param context context
     * @return id
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备的imei
     *
     * @param context context
     * @return id
     */
    public static String getImei(Context context) {
        return getImei(context, 0);
    }

    /**
     * 获取设备的imei
     *
     * @param context context
     * @param index   第N个卡的imei，从0开始
     * @return imei
     */
    @SuppressLint("HardwareIds")
    @SuppressWarnings("deprecation")
    public static String getImei(Context context, int index) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        //如果取0号卡的IMEI
        if (index == 0) {
            if (Build.VERSION.SDK_INT >= 26) {
                String imei = tm.getImei();
                if (imei == null) try {
                    imei = tm.getDeviceId();
                } catch (Exception ignored) {
                }
                return imei;
            } else {
                return tm.getDeviceId();
            }
        }
        //否则取第N张卡的IMEI
        if (Build.VERSION.SDK_INT >= 26) {
            return tm.getImei(index);
        } else {
            Method[] methods = tm.getClass().getDeclaredMethods();
            for (Method m : methods) {
                //名字为getImei正好一个参数
                if ("getImei".equals(m.getName()) && m.getParameterTypes().length == 1) {
                    try {
                        Object obj = m.invoke(tm, index);
                        if (obj != null) {
                            return obj.toString();
                        }
                    } catch (Exception e) {
                        Log.e("获取IMEI", "失败", e);
                    }
                }
            }
        }
        return null;
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
     * 获取当前版本code
     *
     * @param context     context
     * @param packageName 包名
     * @return 版本code
     */
    public static int getVersionCode(Context context, String packageName) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            Log.d("getVersionCode:", String.valueOf(verCode));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getVersionCode", Objects.requireNonNull(e.getMessage()));
        }
        return verCode;
    }

    /**
     * 获取当前版本code
     *
     * @param context context
     * @return 版本code
     */
    public static int getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    /**
     * 获取当前版本名
     *
     * @param context     context
     * @param packageName 包名
     * @return 版本名
     */
    public static String getVersionName(Context context, String packageName) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            Log.d("getVersionName:", verName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getVersionName", Objects.requireNonNull(e.getMessage()));
        }
        return verName;
    }

    /**
     * 获取当前版本名
     *
     * @param context context
     * @return 版本名
     */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    /**
     * 对象复制,深度复制,被复制的对象必须序列化或是基本类型
     *
     * @param date 对象
     * @param <T>  对象 extends Serializable
     * @return 新的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T copyObject(T date) {
        if (date == null)
            return null;
        if (date instanceof Parcelable) {
            Log.i("copyObject", "采用Parcelable序列化");
            Parcel parcel = null;
            try {
                parcel = Parcel.obtain();
                parcel.writeParcelable((Parcelable) date, 0);
                parcel.setDataPosition(0);
                return (T) parcel.readParcelable(date.getClass().getClassLoader());
            } catch (Exception e) {
                Log.e("copyObject", "复制错误", e);
            } finally {
                parcel.recycle();
            }
        }
        if (date instanceof Serializable) {
            Log.i("copyObject", "采用Serializable序列化");
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(date);
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                return (T) in.readObject();
            } catch (IOException e) {
                Log.e("copyObject", "复制发生IO错误", e);
            } catch (ClassNotFoundException e) {
                Log.e("copyObject", "复制找不到对象错误", e);
            } catch (Exception e) {
                Log.e("copyObject", "复制错误", e);
            }
        }
        Log.i("copyObject", "警告，对象未继承Serializable或Parcelable");
        Log.i("copyObject", "尝试Gson序列化");
        try {
            Gson gson = new Gson();
            return (T) gson.fromJson(gson.toJson(date), date.getClass());
        } catch (Exception e) {
            Log.e("copyObject", "Gson序列化失败", e);
        }
        return null;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return 是否可用
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return SD卡路径
     */
    public static String getSDCardPath() {
        if (isSDCardEnable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 获取SD卡路径文件
     *
     * @return File
     */
    public static File getSDCardFile() {
        if (isSDCardEnable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取系统存储路径
     *
     * @return String路径
     */
    public static String getSDCardRootPath() {
        if (isSDCardEnable()) {
            return Environment.getRootDirectory().getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 获取系统存储路径文件
     *
     * @return File
     */
    public static File getSDCardRootFile() {
        if (isSDCardEnable()) {
            return Environment.getRootDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取应用程序的/data/data目录
     *
     * @param context context
     * @return /data/data目录
     */
    public static String getDataFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator;
    }

    /**
     * /data/data/PackageName/cache的路径
     *
     * @param context context
     * @return cache的路径
     */
    public static String getDataCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath() + File.separator;
    }

    /**
     * 获取SD卡大小
     *
     * @return 大小字节
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getSDCardSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//            if (Build.VERSION.SDK_INT < 18) {
//                int blockSize = statFs.getBlockSize();
//                int blockCount = statFs.getBlockCount();
//                return blockSize * blockCount;
//            } else {
            long blockSize = statFs.getBlockSizeLong();
            long blockCount = statFs.getBlockCountLong();
            return blockSize * blockCount;
//            }
        }
        return -1;
    }

    /**
     * 获取SD卡可用大小,SD卡存在返回大小；SD卡不存在返回-1
     *
     * @return 大小，字节
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getSDCardAvailableSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//            if (Build.VERSION.SDK_INT < 18) {
//                int blockSize = statFs.getBlockSize();
//                int blockCount = statFs.getAvailableBlocks();
//                return blockSize * blockCount;
//            } else {
            long blockSize = statFs.getBlockSizeLong();
            long blockCount = statFs.getAvailableBlocksLong();
            return blockSize * blockCount;
//            }
        }
        return -1;
    }

    /**
     * 获得手机内存总大小
     *
     * @return 大小，字节
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
//        if (Build.VERSION.SDK_INT < 19) {
//            int blockSize = statFs.getBlockSize();
//            int blockCount = statFs.getBlockCount();
//            return blockSize * blockCount;
//        } else {
        long blockSize = statFs.getBlockSizeLong();
        long blockCount = statFs.getBlockCountLong();
        return blockSize * blockCount;
//        }
    }

    /**
     * 获得手机可用内存
     *
     * @return 大小字节
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
//        if (Build.VERSION.SDK_INT < 19) {
//            int blockSize = statFs.getBlockSize();
//            int blockCount = statFs.getAvailableBlocks();
//            return blockSize * blockCount;
//        } else {
        long blockSize = statFs.getBlockSizeLong();
        long blockCount = statFs.getAvailableBlocksLong();
        return blockSize * blockCount;
//        }
    }

    /**
     * 打电话
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
     * 发短信
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
     * 后台实现发送短信
     *
     * @param context     context
     * @param phoneNumber 手机号
     * @param text        短信内容
     * @param sendMessage 发送广播
     * @param receiver    接收广播
     *                    <p>
     *                    sendMessage = new BroadcastReceiver() {
     *                    public void onReceive(Context context, Intent intent) {
     *                    // 判断短信是否发送成功
     *                    switch (getResultCode()) {
     *                    case Activity.RESULT_OK: break;
     *                    default: break;
     *                    }
     *                    }
     *                    };
     *                    receiver = new BroadcastReceiver() {
     *                    public void onReceive(Context context, Intent intent) {
     *                    // 表示对方成功收到短信
     *                    }
     *                    };
     */
    public static void sendMessage(Context context, String phoneNumber, String text, BroadcastReceiver sendMessage, BroadcastReceiver receiver) {
        //发送与接收的广播
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        //注册发送广播
        if (sendMessage != null)
            context.registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
        //注册接收关闭
        if (receiver != null)
            context.registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));

        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);

        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);

        SmsManager smsManager = SmsManager.getDefault();
        //如果字数超过70,需拆分成多条短信发送
        if (text.length() > 70) {
            ArrayList<String> messages = smsManager.divideMessage(text);
            for (String msg : messages) {
                smsManager.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
        }
    }

    /**
     * 后台实现发送短信
     *
     * @param context     context
     * @param phoneNumber 电话号码
     * @param text        短信内容
     */
    public static void sendMessage(Context context, String phoneNumber, String text) {
        sendMessage(context, phoneNumber, text, null, null);
    }

    /**
     * 判断APP版本是否是更新后第一次启动
     *
     * @param context context
     * @return 是否是第一次启动
     */
    public static boolean isUpdate(Context context) {
        int versionCode = getVersionCode(context);
        String versionName = getVersionName(context);
        SharedPreferences shared = context.getSharedPreferences("AppVersion", 0);
        if (versionCode != shared.getInt("versionCode", -1) || !versionName.equals(shared.getString("versionName", ""))) {
            // 如果版本号有变化
            SharedPreferences.Editor share = context.getSharedPreferences("AppVersion", 0).edit();
            share.putString("versionName", versionName);// 写入数据
            share.putInt("versionCode", versionCode);
            share.apply();
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否有网络连接,但是如果该连接的网络无法上网，也会返回true
     * 需要权限android.permission.ACCESS_NETWORK_STATE
     *
     * @param context context
     * @return 是否有网络连接
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        return Objects.requireNonNull(info).getState() == NetworkInfo.State.CONNECTED;
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context context
     * @return WIFI网络是否可用
     */
    public static boolean isWifiConnected(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context context
     * @return MOBILE网络是否可用
     */
    public boolean isMobileConnected(Context context) {
        return getConnectedType(context) == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 获取连接类型
     *
     * @param context context
     * @return 类型
     */
    public static int getConnectedType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return -1;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.getType();
        }
        return -1;
    }

    /**
     * ping 一个ip地址
     *
     * @param ip ip
     * @return 是ping通
     */
    public static boolean ping(String ip) {
        String result = null;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次，每次间隔100毫秒
            // 读取ping的内容，可以不加
            //InputStream input = p.getInputStream();
            //BufferedReader in = new BufferedReader(new InputStreamReader(input));
            //String content;
            //while ((content = in.readLine()) != null) {
            //System.out.println(content);
            //}
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                return true;
            }
        } catch (Exception e) {
            Log.e("-----ping-----", "ping出错：", e);
        }
        return false;
    }

    /**
     * ping 百度
     *
     * @return 是否通
     */
    public static boolean pingBaidu() {
        String ip = "www.baidu.com";
        return ping(ip);
    }

    /**
     * 获取ipv6
     *
     * @return ipv6地址列表
     */
    public static List<String> getIpv6() {
        List<String> ips = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> item = ni.getInetAddresses(); item.hasMoreElements(); ) {
                    InetAddress inetAddress = item.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet6Address)) {
                        ips.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("获取IPv6失败", ex.toString());
        }
        return ips;
    }

    /**
     * 获取ipv4
     *
     * @return ipv4地址列表
     */
    public static List<String> getIpv4() {
        List<String> ips = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> item = ni.getInetAddresses(); item.hasMoreElements(); ) {
                    InetAddress inetAddress = item.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        ips.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("获取IPv4失败", ex.toString());
        }
        return ips;
    }

    /**
     * 复制文本到粘贴板
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        if (clipboard != null)
            clipboard.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), text));
    }

    /**
     * 获取粘贴板最后一条数据
     */
    public static String getClipboardLast(Context context) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 获取剪贴板的剪贴数据集
        if (clipboard != null) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0)
                return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }

    /**
     * 获取粘贴板全部数据
     */
    public static List<String> getClipboardAll(Context context) {
        List<String> strings = new ArrayList<>();
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 获取剪贴板的剪贴数据集
        if (clipboard != null) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null)
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    strings.add(clipData.getItemAt(0).getText().toString());
                }
        }
        return strings;
    }

    /**
     * 字体高亮
     */
    public static View foreground(View view, int color, int start, int end) {
        if (view instanceof Button) {
            Button btn = (Button) view;
            // 获取文字
            Spannable span = new SpannableString(btn.getText().toString());
            //设置颜色和起始位置
            span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            btn.setText(span);
            return btn;
        } else if (view instanceof TextView) {//EditText extends TextView
            TextView text = (TextView) view;
            Spannable span = new SpannableString(text.getText().toString());
            span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            text.setText(span);
            return text;
        }
        return null;
    }

    /**
     * 关闭软键盘
     */
    public static void closeSoftKeyboard(Activity activity) {
        InputMethodManager inputManger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManger != null)
            inputManger.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * json格式化
     *
     * @param str 目标字符串
     * @return 被格式化的字符串
     */
    public static String jsonFormat(String str) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonElement element = JsonParser.parseString(str);
            return gson.toJson(element);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断字符串是否是json
     *
     * @param str 目标字符串
     * @return 结果
     */
    public static boolean stringIsJson(String str) {
        try {
            JsonElement element = JsonParser.parseString(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
