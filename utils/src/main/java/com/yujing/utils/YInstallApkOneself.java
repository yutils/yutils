package com.yujing.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;

/*
 * 自更新：Android 7+ 统一 ACTION_VIEW + FileProvider（不走 PackageInstaller.Session，避免 INSTALL_FAILED_ABORTED / User rejected permissions 等会话安装兼容问题）。
 *
 * 1.FileProvider 路径（res/xml/provider_paths.xml）
 *    <?xml version="1.0" encoding="utf-8"?>
 *    <paths xmlns:android="http://schemas.android.com/apk/res/android">
 *        <external-files-path name="private_files" path="."/>
 *    </paths>
 *
 * 2.Manifest 权限
 *    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
 *    （按需：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />）
 *
 * 3.AndroidManifest.xml application 内 FileProvider
 *    <provider
 *        android:name="androidx.core.content.FileProvider"
 *        android:authorities="${applicationId}.fileProvider"
 *        android:exported="false"
 *        android:grantUriPermissions="true">
 *        <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/provider_paths"/>
 *    </provider>
 *
 * 4.APK 建议放在私有目录，例如 …/Android/data/包名/files/download/xxx.apk
 *
 * 5.调用：new YInstallApkOneself(activity).install(file);
 */
public class YInstallApkOneself {
    private static final String TAG = "YInstallApk";
    @Nullable
    private ComponentActivity activity;

    public YInstallApkOneself() {
        activity = resolveComponentActivity(YActivityUtil.getCurrentActivity());
    }

    public YInstallApkOneself(@Nullable ComponentActivity activity) {
        this.activity = activity;
    }

    @Nullable
    private static ComponentActivity resolveComponentActivity(@Nullable android.app.Activity raw) {
        if (raw instanceof ComponentActivity) return (ComponentActivity) raw;
        return null;
    }

    @Nullable
    private Context installContext() {
        if (activity != null) return activity;
        ComponentActivity refreshed = resolveComponentActivity(YActivityUtil.getCurrentActivity());
        if (refreshed != null) return refreshed;
        return YApp.get();
    }

    public void install(File file) {
        Context ctx = installContext();
        if (ctx == null) {
            YToast.show("无法安装：找不到可用上下文");
            return;
        }
        if (!file.exists()) {
            YToast.show("APK文件不存在：" + file.getAbsolutePath());
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installWithFileProvider(ctx, getPrivateDirUri(ctx, file));
        } else {
            installApk(ctx, Uri.fromFile(file));
        }
    }

    private void installWithFileProvider(Context ctx, Uri apkUri) {
        YThread.runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                for (android.content.pm.ResolveInfo resolveInfo :
                        ctx.getPackageManager().queryIntentActivities(intent, 0)) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    ctx.grantUriPermission(
                            packageName,
                            apkUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                }

                ctx.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "FileProvider安装失败", e);
                YToast.show("安装失败，请手动安装");
            }
        });
    }

    private Uri getPrivateDirUri(Context ctx, File file) {
        return FileProvider.getUriForFile(
                ctx,
                ctx.getPackageName() + ".fileProvider",
                file
        );
    }

    public static void installApk(Context context, Uri apkUri) {
        YThread.runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "传统安装失败", e);
                YToast.show("安装失败，请手动安装");
            }
        });
    }
}
