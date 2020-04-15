package com.yujing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yujing.contract.YListener;
import com.yujing.contract.YListener1;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求
 *
 * @author yujing 2020年2月12日16:01:27
 */
public class YPermissions {
    public static int requestCode = 888;
    private Activity activity;
    private YListener successListener;
    private YListener1<List<String>> failListener;

    public YPermissions(Activity activity) {
        this.activity = activity;
    }

    //获取到全部权限
    public void setSuccessListener(YListener successListener) {
        this.successListener = successListener;
    }

    //没有获取到的权限
    public void setFailListener(YListener1<List<String>> failListener) {
        this.failListener = failListener;
    }

    /**
     * 获取全部权限
     */
    public void requestAll() {
        request(getManifestPermissions(activity));
    }

    /**
     * 获取权限
     */
    public void request(String... permissions) {
        if (permissions == null) return;
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String item : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, item)) {
                toApplyList.add(item); // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), requestCode);
        } else {
            if (successListener != null) successListener.value();
        }
    }

    /**
     * 权限回调
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //判断是否是自己请求的权限||判断SDK版本号是否大于等于安卓6.0
        //判断permissions数组和grantResults数组是否有长度，permissions数组记录请求哪些权限，grantResults数组记录哪些权限获取到了，0(PackageManager.PERMISSION_GRANTED)代表获取到了，-1代表未获取到
        if (requestCode != 888 || Build.VERSION.SDK_INT < 23 || permissions.length == 0 || grantResults.length == 0) {
            if (successListener != null) successListener.value();
            return;
        }
        //拒绝列表
        List<String> IgnoreList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                IgnoreList.add(permissions[i]);
        }
        //拒绝列表如果为空，就表示获取到全部权限
        if (IgnoreList.isEmpty()) {
            if (successListener != null) successListener.value();
            return;
        }
        if (failListener != null) failListener.value(IgnoreList);
//        //不再提示列表，勾选了对话框中“不再提示”的选项, 返回false
//        List<String> noPromptList = new ArrayList<>();
//        for (String item : IgnoreList) {
//            if (!activity.shouldShowRequestPermissionRationale(item)) noPromptList.add(item);
//        }
//        //跳转到设置界面
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + activity.getPackageName())); // 根据包名打开对应的设置界面
//        activity.startActivity(intent);
    }

    /**
     * 获取Manifest中的全部权限
     */
    public static String[] getManifestPermissions(Activity activity) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        if (packageInfo == null) return null;
        return packageInfo.requestedPermissions;
    }

    /**
     * 获取权限
     */
    public static void requestAll(Activity activity) {
        request(activity, getManifestPermissions(activity));
    }

    /**
     * 获取权限
     */
    public static void request(Activity activity, String... permissions) {
        if (permissions == null) return;
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String item : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, item)) {
                toApplyList.add(item); // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), requestCode);
        }
    }

    /**
     * 判断是否有某些权限
     *
     * @param context     context
     * @param permissions 权限。如：Manifest.permission.CAMERA
     * @return 是否有
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < 23) return true;
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, perm)) {
                return false;
            }
        }
        return true;
    }
}
