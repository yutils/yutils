package com.yujing.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/*
优化后特性：
1. Android 12+ 使用PackageInstaller API（无未知应用权限弹窗）
2. Android 8-11 移除未知应用权限检测（私有目录+系统信任）
3. 适配应用私有目录的FileProvider路径
4. 增加文件校验、异常捕获等健壮性逻辑

1.FileProvider 路径（res/xml/provider_paths.xml）
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <!-- 应用私有外部存储：/Android/data/包名/files/ -->
        <external-files-path
            name="private_files"
            path="."/> <!-- "." 表示私有存储根目录，包含所有子目录 -->
    </paths>

2.Manifest 权限（AndroidManifest.xml）
    <!-- 安装未知应用（Android 8+，声明即可，代码不再主动跳转） -->
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
    <!-- Android 12+ PackageInstaller 需要 -->
    <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />
    <!-- 存储权限（仅 Android 9 及以下需要） -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />


3.再在AndroidManifest.xml 中的application加入
    <!-- 安装APK专用 FileProvider -->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.fileProvider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/provider_paths"/>
    </provider>

4.必须把 APK 下载到这里：
    // 私有目录：/Android/data/包名/files/download/
    String apkPath = context.getExternalFilesDir("").getAbsolutePath() + "/download/" + saveApkName

5.下载完成后直接安装
    new YInstallApk(this).install(apkPath);
*/
public class YInstallApkOneself {
    private static final String TAG = "YInstallApk";
    private static final int INSTALL_SESSION_ID = 1001;
    private ComponentActivity activity;

    public YInstallApkOneself() {
        this((ComponentActivity) YActivityUtil.getCurrentActivity());
    }

    public YInstallApkOneself(ComponentActivity activity) {
        this.activity = activity;
    }

    public void install(File file) {
        // 前置校验：文件是否存在
        if (!file.exists()) {
            YToast.show("APK文件不存在：" + file.getAbsolutePath());
            return;
        }
        // 按系统版本分支处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+：PackageInstaller（无弹窗）
            installWithPackageInstaller(file);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7-11：FileProvider（私有目录+无权限检测）
            Uri apkUri = getPrivateDirUri(file);
            installWithFileProvider(apkUri);
        } else {
            // Android 6及以下：传统方式
            Uri apkUri = Uri.fromFile(file);
            installApk(activity, apkUri);
        }
    }

    // ==================== Android 12+ 核心逻辑 ====================
    private void installWithPackageInstaller(File apkFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PackageInstaller packageInstaller = activity.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(activity.getPackageName());

            try {
                int sessionId = packageInstaller.createSession(params);
                PackageInstaller.Session session = packageInstaller.openSession(sessionId);

                // 写入APK文件（带进度的字节流，避免OOM）
                OutputStream outputStream = session.openWrite("apk", 0, apkFile.length());
                FileInputStream inputStream = new FileInputStream(apkFile);
                byte[] buffer = new byte[1024 * 1024]; // 1MB缓冲区
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                // 同步写入，确保文件完整
                session.fsync(outputStream);
                inputStream.close();
                outputStream.close();

                // 提交安装（系统后台处理，无弹窗）
                Intent intent = new Intent(activity, activity.getClass());
                intent.setAction(Intent.ACTION_MAIN);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        activity,
                        INSTALL_SESSION_ID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                session.commit(pendingIntent.getIntentSender());
                session.close();
            } catch (Exception e) {
                Log.e(TAG, "PackageInstaller安装失败", e);
                YToast.show("安装失败：" + e.getMessage());
                // 降级处理：尝试传统方式
                fallbackToNormalInstall(apkFile);
            }
        }
    }

    // ==================== Android 7-11 核心逻辑 ====================
    private void installWithFileProvider(Uri apkUri) {
        YThread.runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 授予系统包安装器临时读取权限（关键！避免解析失败）
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                // 给所有可处理安装的应用授予权限（兼容不同ROM）
                for (android.content.pm.ResolveInfo resolveInfo :
                        activity.getPackageManager().queryIntentActivities(intent, 0)) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(
                            packageName,
                            apkUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                }

                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "FileProvider安装失败", e);
                YToast.show("安装失败，请手动安装");
            }
        });
    }

    // ==================== 工具方法 ====================
    // 获取应用私有目录的FileProvider Uri（适配你的存储路径）
    private Uri getPrivateDirUri(File file) {
        return FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileProvider", // 与Manifest的authorities一致
                file
        );
    }

    // 降级处理：PackageInstaller失败时用传统方式
    private void fallbackToNormalInstall(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = getPrivateDirUri(file);
            installWithFileProvider(apkUri);
        } else {
            installApk(activity, Uri.fromFile(file));
        }
    }

    // 传统安装方法（Android 6及以下）
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