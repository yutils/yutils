package com.yujing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/*
   安装apk
   如果是安卓8.0以上先请求打开位置来源
   权限：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

   1.首先创建res/xml/file_paths.xml
   内容：
    <?xml version="1.0" encoding="UTF-8"?>
    <resources>
        <paths>
            <external-path path="" name="download"/>
        </paths>
    </resources>

   2.再在AndroidManifest.xml  中的application加入
    <!--安装app-->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.provider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>

   举例:
   var yInstallApk:YInstallApk?=null
   private fun install() {
        yInstallApk= YInstallApk(this)
        yInstallApk?.install(YPath.getSDCard()+"/app.apk")
   }
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        yInstallApk?.onActivityResult(requestCode,resultCode,data)
   }
 */
public class YInstallApk {
    private Activity activity;
    private Uri apkUri;
    private int INSTALL_CODE = 8899;

    public YInstallApk(Activity activity) {
        this.activity = activity;
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= 26) {
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            activity.startActivityForResult(intent, INSTALL_CODE);
        }
    }

    public void install(String apkPath) {
        File file = new File(apkPath);
        install(file);
    }

    public void install(File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
            install(apkUri);
        } else {
            Uri apkUri = Uri.fromFile(file);
            installApk(activity, apkUri);
        }
    }

    public void install(Uri apkUri) {
        if (Build.VERSION.SDK_INT >= 26) {
            //先判断是否有安装未知来源应用的权限
            boolean haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {
                installApk(activity, apkUri);
            } else {
                this.apkUri=apkUri;
                getPermission();
            }
        } else {
            installApk(activity, apkUri);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == INSTALL_CODE) {
            if (apkUri != null)
                installApk(activity, apkUri);
        }
    }

    /**
     * 安装app
     *
     * @param context context
     * @param apkPath app存放路径
     */
    public static void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
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
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            installApk(context, apkUri);
        } else {
            Uri apkUri = Uri.fromFile(file);
            installApk(context, apkUri);
        }
    }

    /**
     * 安装app
     *
     * @param context context
     * @param apkUri  app的uri
     */
    public static void installApk(Context context, Uri apkUri) {
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
            YToast.show(context, "安装失败，请手动安装");
        }
    }
}
