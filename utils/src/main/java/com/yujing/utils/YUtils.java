package com.yujing.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yujing.contract.YListener1;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * @author 余静 2021年3月10日10:46:57
 */
/*用法
//初始化
YUtils.init(this)
 */
public class YUtils {
    /**
     * 全局application
     * 使用时：YApp.get();
     *
     * @param application application
     */
    public static void init(Application application) {
        YApp.set(application);
        YActivityUtil.init(application);
    }

    /**
     * 全局application
     * 使用时：YApp.get();
     * 并且初始化YActivityUtil注册:registerActivityLifecycleCallbacks
     *
     * @param application application
     */
    public static void initAll(Application application) {
        init(application);
        YActivityUtil.init(application);
    }

    /**
     * 重新计算listView高度
     *
     * @param listView 需要计算的对象
     */
    public static void resetListViewHeight(ListView listView) {
        // 提示：最底层容器要用LinearLayout
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
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
        if (listAdapter == null) return;
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null) continue;
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
        if (maxHeight != null && params.height > maxHeight)
            params.height = maxHeight;
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
     * 当前是否是debug模式
     *
     * @return 是或否
     */
    public static boolean isDebug() {
        return isDebug(YApp.get());
    }


    /**
     * 获取设备的唯一驱动id
     *
     * @return id
     */
    public static String getAndroidId() {
        return getAndroidId(YApp.get());
    }

    /**
     * 获取设备的唯一驱动id
     *
     * @param context context
     * @return id
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备的imei
     *
     * @return id
     */
    public static String getImei() {
        return getImei(YApp.get(), 0);
    }

    /**
     * 获取设备的imei
     *
     * @param context context
     * @param index   第N个卡的imei，从0开始
     * @return imei
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getImei(Context context, int index) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            return null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return null;
        //如果取0号卡的IMEI
        if (index == 0) {
            if (Build.VERSION.SDK_INT >= 26) {
                String imei = tm.getImei();
                if (imei == null) try {
                    imei = tm.getDeviceId();
                } catch (Exception ignored) {
                }
                return imei;
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
                        YLog.e("获取IMEI", "失败", e);
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
        YScreenUtil.setFullScreen(activity, isFullScreen);
    }

    /**
     * 设置开启沉浸式
     *
     * @param activity     页面
     * @param isFullScreen 沉浸式否
     */
    public static void setImmersive(Activity activity, boolean isFullScreen) {
        YScreenUtil.setImmersive(activity, isFullScreen);
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
        } catch (PackageManager.NameNotFoundException e) {
            YLog.e("getVersionCode", "异常", e);
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

    public static int getVersionCode() {
        return getVersionCode(YApp.get());
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
        } catch (PackageManager.NameNotFoundException e) {
            YLog.e("getVersionName", "异常", e);
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

    public static String getVersionName() {
        return getVersionName(YApp.get());
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
        if (date == null) return null;
        if (date instanceof Serializable) {
            YLog.i("copyObject", "采用Serializable序列化");
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(date);
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                return (T) in.readObject();
            } catch (IOException e) {
                YLog.e("copyObject", "复制发生IO错误", e);
            } catch (ClassNotFoundException e) {
                YLog.e("copyObject", "复制找不到对象错误", e);
            } catch (Exception e) {
                YLog.e("copyObject", "复制错误", e);
            }
        }
        if (date instanceof Parcelable) {
            YLog.i("copyObject", "采用Parcelable序列化");
            Parcel parcel = null;
            try {
                parcel = Parcel.obtain();
                parcel.writeParcelable((Parcelable) date, 0);
                parcel.setDataPosition(0);
                return parcel.readParcelable(date.getClass().getClassLoader());
            } catch (Exception e) {
                YLog.e("copyObject", "复制错误", e);
            } finally {
                if (parcel != null)
                    parcel.recycle();
            }
        }
        YLog.i("copyObject", "警告，对象未继承Serializable或Parcelable");
        YLog.i("copyObject", "尝试Gson序列化");
        try {
            Gson gson = new Gson();
            return (T) gson.fromJson(gson.toJson(date), date.getClass());
        } catch (Exception e) {
            YLog.e("copyObject", "Gson序列化失败", e);
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
     * 获取SD卡大小
     *
     * @return 大小字节
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getSDCardSize() {
        if (isSDCardEnable()) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
            long blockSize = statFs.getBlockSizeLong();
            long blockCount = statFs.getBlockCountLong();
            return blockSize * blockCount;
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
            long blockSize = statFs.getBlockSizeLong();
            long blockCount = statFs.getAvailableBlocksLong();
            return blockSize * blockCount;
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
        long blockSize = statFs.getBlockSizeLong();
        long blockCount = statFs.getBlockCountLong();
        return blockSize * blockCount;
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
        long blockSize = statFs.getBlockSizeLong();
        long blockCount = statFs.getAvailableBlocksLong();
        return blockSize * blockCount;
    }


    /**
     * 后台实现发送短信
     * 如果要前台打印请参考YGoto.sendSMS()
     *
     * @param context     context
     * @param phoneNumber 手机号
     * @param text        短信内容
     * @param sendMessage 发送广播
     * @param receiver    接收广播
     */
    /*
         sendMessage = new BroadcastReceiver() {
         public void onReceive(Context context, Intent intent) {
                 // 判断短信是否发送成功
                 switch (getResultCode()) {
                     case Activity.RESULT_OK: break;
                     default: break;
                 }
             }
         };
         receiver = new BroadcastReceiver() {
             public void onReceive(Context context, Intent intent) {
                // 表示对方成功收到短信
             }
         };
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
            for (String msg : messages)
                smsManager.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
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

    public static void sendMessage(String phoneNumber, String text) {
        sendMessage(YApp.get(), phoneNumber, text, null, null);
    }

    /**
     * 判断APP版本是否是更新后第一次启动
     *
     * @param context context
     * @return 是否是第一次启动
     */
    public static boolean isUpdate(Context context) {
        return isUpdate(context, null);
    }

    public static boolean isUpdate() {
        return isUpdate(YApp.get());
    }

    /**
     * 更新接口
     */
    public static interface UpdateListener {
        void update(int oldCode, String oldName, int newCode, String newName);
    }

    /**
     * 判断更新并回调
     *
     * @param listener 回调新旧版本号
     * @return 是否是第一次启动
     */
    /*
      YUtils.isUpdate(this) { oldCode, oldName, newCode, newName ->
          YLog.i("$oldCode  $oldName  $newCode  $newName  ")
      }
      5  1.0.5  5  1.0.5
     */
    public static boolean isUpdate(Context context, UpdateListener listener) {
        SharedPreferences shared = context.getSharedPreferences("AppVersion", 0);
        int oldCode = shared.getInt("versionCode", -1);
        String oldName = shared.getString("versionName", "");

        int newCode = getVersionCode(context);
        String newName = getVersionName(context);

        if (listener != null) listener.update(oldCode, oldName, newCode, newName);

        if (newCode != oldCode || !newName.equals(oldName)) {
            // 如果版本号有变化
            SharedPreferences.Editor share = context.getSharedPreferences("AppVersion", 0).edit();
            share.putString("versionName", newName);// 写入数据
            share.putInt("versionCode", newCode);
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
        if (manager == null) return false;
        @SuppressLint("MissingPermission") NetworkInfo info = manager.getActiveNetworkInfo();
        return Objects.requireNonNull(info).getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean isNetConnected() {
        return isNetConnected(YApp.get());
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

    public static boolean isWifiConnected() {
        return isWifiConnected(YApp.get());
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

    public boolean isMobileConnected() {
        return isNetConnected(YApp.get());
    }

    /**
     * 获取网络连接类型
     *
     * @param context context
     * @return 类型
     */
    public static int getConnectedType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return -1;
        @SuppressLint("MissingPermission") NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null ? info.getType() : -1;
    }

    /**
     * ping 一个ip地址
     *
     * @param ip ip
     * @return 是否ping通
     */
    public static boolean ping(String ip) {
        return ping(ip, null);
    }

    /**
     * ping 一个ip地址
     *
     * @param ip           ip
     * @param dataListener ping中间数据
     * @return 是否ping通
     */
    public static boolean ping(String ip, YListener1<String> dataListener) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次，每次间隔100毫秒
            //读取ping的内容，可以不加
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            Thread thread1 = new Thread(() -> {
                try {
                    String content;
                    while ((content = br.readLine()) != null) {
                        if (dataListener != null) dataListener.value(content);
                    }
                } catch (Exception ignored) {
                } finally {
                    try {
                        br.close();
                        p.getInputStream().close();
                    } catch (Exception ignored) {
                    }
                }
            });
            thread1.setName("ping-br");
            thread1.start();

            Thread thread2 = new Thread(() -> {
                try {
                    String content;
                    while ((content = er.readLine()) != null) {
                        if (dataListener != null) dataListener.value(content);
                    }
                } catch (Exception ignored) {
                } finally {
                    try {
                        er.close();
                        p.getErrorStream().close();
                    } catch (Exception ignored) {
                    }
                }
            });
            thread2.setName("ping-er");
            thread2.start();
            // ping的状态
            int status = p.waitFor();
            if (status == 0) return true;
        } catch (Exception e) {
            YLog.e("-----ping-----", "ping出错：", e);
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
    public static List<String> getIPv6() {
        List<String> ips = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> item = ni.getInetAddresses(); item.hasMoreElements(); ) {
                    InetAddress inetAddress = item.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet6Address))
                        ips.add(inetAddress.getHostAddress());
                }
            }
        } catch (SocketException e) {
            YLog.e("获取IPv6失败", "异常", e);
        }
        return ips;
    }

    /**
     * 获取ipv4
     *
     * @return ipv4地址列表
     */
    public static List<String> getIPv4() {
        List<String> ips = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> item = ni.getInetAddresses(); item.hasMoreElements(); ) {
                    InetAddress inetAddress = item.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                        ips.add(inetAddress.getHostAddress());
                }
            }
        } catch (SocketException e) {
            YLog.e("获取IPv4失败", "异常", e);
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

    public static void copyToClipboard(String text) {
        copyToClipboard(YApp.get(), text);
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

    public static String getClipboardLast() {
        return getClipboardLast(YApp.get());
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
                for (int i = 0; i < clipData.getItemCount(); i++)
                    strings.add(clipData.getItemAt(0).getText().toString());
        }
        return strings;
    }

    public static List<String> getClipboardAll() {
        return getClipboardAll(YApp.get());
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

    /**
     * 打开TCP
     *
     * @param port 端口
     * @return 是否成功
     */
    public static boolean openTcp(int port) {
        try {
            shellRoot("setprop service.adb.tcp.port " + port, "start adbd");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 关闭TCP
     *
     * @param port 端口
     * @return 是否成功
     */
    public static boolean closeTcp(int port) {
        try {
            shellRoot("setprop service.adb.tcp.port " + port, "stop adbd");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 执行shell命令
     *
     * @return 是否成功
     */
    public static String shell(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        return new String(YConvert.inputStreamToBytes(process.getInputStream(), 500));
    }

    /**
     * 执行root命令
     *
     * @return 是否成功
     */
    public static String shellRoot(String... command) throws Exception {
        DataOutputStream os = null;
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String item : command) {
                os.writeBytes(item + "\n");
            }
            os.flush();
            return new String(YConvert.inputStreamToBytes(process.getInputStream(), 500));
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 获取打开APP的Intent
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 用法：
     * val intent = YUtils.getAppIntent("com.xx.xx","com.xx.xx.MainActivity")
     * intent.putExtra("数据", "8888888888888")
     * YUtils.openAPP(intent)
     *
     * @param packageName  包名
     * @param activityName activity类名
     * @return Intent
     */
    public static Intent getAppIntent(String packageName, String activityName) {
        ComponentName cn = new ComponentName(packageName, activityName);
        Intent intent = new Intent();
        intent.setComponent(cn);
        return intent;
    }

    /**
     * 获取打开APP的Intent
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 需要YUtils.init(this)
     * 用法：
     * val intent = YUtils.getAppIntent("微信")
     * intent.putExtra("数据", "8888888888888")
     * YUtils.openAPP(intent)
     *
     * @param appName app名称
     * @return Intent
     */
    public static Intent getAppIntent(String appName) {
        return getAppIntent(YApp.get(), appName);
    }

    /**
     * 获取打开APP的Intent
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 用法：
     * val intent = YUtils.getAppIntent(this,"微信")
     * intent.putExtra("数据", "8888888888888")
     * YUtils.openAPP(intent)
     *
     * @param context context
     * @param appName app名称
     * @return Intent
     */
    public static Intent getAppIntent(Context context, String appName) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = null;
        //获取所有安装的app
        @SuppressLint("QueryPermissionsNeeded")
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo info : installedPackages) {
            String pkg = info.packageName;//app包名
            ApplicationInfo ai = null;
            try {
                ai = packageManager.getApplicationInfo(pkg, 0);
            } catch (PackageManager.NameNotFoundException e) {
                YLog.e("读取APP异常", e);
            }
            String name = (String) packageManager.getApplicationLabel(ai);//获取应用名称
            if (appName.equals(name)) {
                packageName = pkg;
                break;
            }
        }
        //查询是否找到package
        if (packageName != null)
            return context.getPackageManager().getLaunchIntentForPackage(packageName);
        return null;
    }

    /**
     * 打开其他APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 需要YUtils.init(this)
     * 用法：
     * YUtils.openAPP("com.xx.xx","com.xx.xx.MainActivity")
     *
     * @param packageName  包名
     * @param activityName activity名称
     * @return 是否成功
     */
    public static boolean openAPP(String packageName, String activityName) {
        return openAPP(YApp.get(), packageName, activityName);
    }

    /**
     * 打开其他APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 用法：
     * YUtils.openAPP(this,"com.xx.xx","com.xx.xx.MainActivity")
     *
     * @param context      context
     * @param packageName  包名
     * @param activityName activity名称
     * @return 是否成功
     */
    public static boolean openAPP(Context context, String packageName, String activityName) {
        Intent intent = getAppIntent(packageName, activityName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        openAPP(context, intent);
        return false;
    }

    /**
     * 通过APP名称打开APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 需要YUtils.init(this)
     * 用法:
     * YUtils.openAPP("微信")
     *
     * @param appName APP名称
     * @return 是否成功
     */
    public static boolean openAPP(String appName) {
        return openAPP(YApp.get(), appName);
    }

    /**
     * 通过APP名称打开APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 用法:
     * YUtils.openAPP(this,"微信")
     *
     * @param context context
     * @param appName APP名称
     * @return 是否成功
     */
    public static boolean openAPP(Context context, String appName) {
        Intent intent = getAppIntent(context, appName);
        //查询是否找到package
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            openAPP(context, intent);
            return true;
        } else {
            YLog.e("打开APP失败", "没有安装：" + appName);
        }
        return false;
    }

    /**
     * 通过Intent打开APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 需要YUtils.init(this)
     * 用法：
     * val intent = YUtils.getAppIntent("微信")
     * intent.putExtra("数据", "8888888888888")
     * YUtils.openAPP(intent)
     *
     * @param intent intent
     * @return 是否成功
     */
    public static boolean openAPP(Intent intent) {
        return openAPP(YApp.get(), intent);
    }

    /**
     * 通过Intent打开APP
     * AndroidManifest.xml 的 activity 加入 android:exported="true"
     * 用法：
     * val intent = YUtils.getAppIntent("微信")
     * intent.putExtra("数据", "8888888888888")
     * YUtils.openAPP(this,intent)
     *
     * @param context context
     * @param intent  intent
     * @return 是否成功
     */
    public static boolean openAPP(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            YLog.e("打开APP异常", e);
        }
        return false;
    }

    /**
     * 重启APP
     *
     * @param context       context
     * @param activityClass activity
     */
    public static void reStartAPP(Context context, Class<? extends Activity> activityClass) {
        Intent intent = new Intent(YActivityUtil.getCurrentActivity(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        YActivityUtil.closeAllActivity();
        exit();
    }

    /**
     * 重启APP
     *
     * @param context context
     */
    public static void reStartAPP(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        exit();
    }

    /**
     * 重启APP
     */
    public static void reStartAPP() {
        reStartAPP(YApp.get());
    }

    /**
     * 退出APP
     */
    public static void exit() {
        YActivityUtil.closeAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}