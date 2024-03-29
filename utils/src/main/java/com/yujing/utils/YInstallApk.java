package com.yujing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.activity.ComponentActivity;

import java.io.File;

/*
安装apk
如果是安卓8.0以上先请求打开未知来源
权限：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

1.首先创建res/xml/provider_paths.xml
内容：
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
    <external-path name="beta_external_path" path="Download/"/>
    <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
    <external-path name="beta_external_files_path" path="Android/data/"/>
</paths>

2.再在AndroidManifest.xml  中的application加入
<!--安装app-->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>

举例:

//安装
YInstallApk().install(YPath.getSDCard() + "/app.apk")
 */
public class YInstallApk {
    private ComponentActivity activity;

    public YInstallApk() {
        this((ComponentActivity) YActivityUtil.getCurrentActivity());
    }


    public YInstallApk(ComponentActivity activity) {
        this.activity = activity;
    }

    public void install(String apkPath) {
        File file = new File(apkPath);
        install(file);
    }

    public void install(File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = YUri.getUri(YApp.get(), file);
            install(apkUri);
        } else {
            Uri apkUri = Uri.fromFile(file);
            installApk(activity, apkUri);
        }
    }

    public void install(Uri apkUri) {
        YThread.runOnUiThread(() -> {
            if (Build.VERSION.SDK_INT >= 26) {
                //先判断是否有安装未知来源应用的权限
                boolean haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
                if (haveInstallPermission) {
                    installApk(activity, apkUri);
                } else {
                    //请求权限之后回调
                    YActivityResultObserver activityResultObserver = new YActivityResultObserver(activity.getActivityResultRegistry(), "YInstallApk", result -> {
                        if (result.getResultCode() == Activity.RESULT_OK)
                            if (apkUri != null) installApk(activity, apkUri);
                        return null;
                    });
                    activity.getLifecycle().addObserver(activityResultObserver);

                    Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                    activityResultObserver.launch(intent);
                }
            } else {
                installApk(activity, apkUri);
            }
        });
    }

    /**
     * 安装app
     *
     * @param context context
     * @param apkPath app存放路径
     */
    public static void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) return;
        File file = new File(apkPath);
        installApk(context, file);
    }

    /**
     * 安装app
     *
     * @param context context
     * @param file    文件
     */
    public static void installApk(Context context, File file) {
        //判读版本是否在7.0以上
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= 24) {
            apkUri = YUri.getUri(YApp.get(), file);
        } else {
            apkUri = Uri.fromFile(file);
        }
        installApk(context, apkUri);
    }

    /**
     * 安装app
     *
     * @param context context
     * @param apkUri  app的uri
     */
    public static void installApk(Context context, Uri apkUri) {
        YThread.runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //判读版本是否在7.0以上
                if (Build.VERSION.SDK_INT >= 24) {
                    // 添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                }
                context.startActivity(intent);
            } catch (Exception e) {
                YToast.show("安装失败，请手动安装");
            }
        });
    }
}
