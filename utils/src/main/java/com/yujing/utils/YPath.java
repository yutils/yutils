package com.yujing.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 获取安卓各种默认目录
 *
 * @author 余静  2019年3月28日09:48:19
 */
/*常用
    常用1
    val path = YPath.getFilePath(App.get(), "配置") + "/" + "name.txt"
    常用2
    val file = File(YPath.getFilePath(App.get()) + "/" + "name.txt")
 */
@SuppressWarnings({"unused"})
public class YPath {
    /**
     * 默认路径
     * /data/data/< package name >/files
     * 相当于：var path = YApp.get().let { (it.getExternalFilesDir("")?.absolutePath ?: (it.filesDir.toString()))}
     *
     * @return 路径
     */
    public static String get() {
        return getFilePath(YApp.get());
    }

    /**
     * 默认路径
     *
     * @return 路径
     */
    public static String get(String dir) {
        return getFilePath(YApp.get(), dir);
    }

    /**
     * 如果路径不是以 / 结尾，那么后面加 /
     *
     * @param path 路径
     * @return 路径
     */
    public static String toDir(String path) {
        if (path != null && path.length() > 0) {
            String last = path.substring(path.length() - 1);
            if (last.equals(File.separator) || last.equals("/") || last.equals("\\"))
                return path;
            path += File.separator;
        }
        return path;
    }

    /**
     * 获取存储路径，优先使用外部储存，不需要申请权限
     * /storage/emulated/0/Android/data/com.xx.xx/files/xxDir
     * /data/data/< package name >/files/xxDir
     * 忽略警告：1,返回值不处理：ResultOfMethodCallIgnored，2,空指针：ConstantConditions
     * var path = YApp.get().let { (it.getExternalFilesDir("")?.absolutePath ?: (it.filesDir.toString())) + "/"+dir}
     *
     * @param context 上下文
     * @param dir     自己的路径，dir前后加"/"都会被去掉
     * @return 路径
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public static String getFilePath(Context context, String dir) {
        String directoryPath;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            try {
                directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
            } catch (Exception e) {
                directoryPath = context.getFilesDir() + File.separator + dir;
            }
        } else {//没外部存储就使用内部存储
            directoryPath = context.getFilesDir() + File.separator + dir;
        }
        File file = new File(directoryPath);
        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }
        return directoryPath;
    }

    /**
     * 获取存储路径，优先使用外部储存，不需要申请权限
     * /storage/emulated/0/Android/data/com.xx.xx/files/
     * /data/data/< package name >/files
     * 忽略警告：1,空指针：ConstantConditions
     *
     * @param context 上下文
     * @return 路径
     */
    @SuppressWarnings({"ConstantConditions"})
    public static String getFilePath(Context context) {
        String directoryPath;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            try {
                directoryPath = context.getExternalFilesDir("").getAbsolutePath();
            } catch (Exception e) {
                directoryPath = context.getFilesDir().getPath();
            }
        } else {//没外部存储就使用内部存储
            directoryPath = context.getFilesDir().getPath();
        }
        return directoryPath;
    }


    /**
     * 获得缓存目录，不需要申请权限
     * 优先
     * <p>
     * 用于获取APP的在SD卡中的cache目录
     * /mnt/sdcard/Android/data/com.xx.xx/cache
     * /storage/emulated/0/Android/data/com.xx.xx/cache
     * <p>
     * 用于获取APP的cache目录
     * /data/data/com.xx.xx/cache
     * /data/user/0/com.xx.xx/cache
     *
     * @return 路径
     */
    @SuppressWarnings({"ConstantConditions"})
    public static String getCache(Context context) {
        String directoryPath;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            directoryPath = context.getExternalCacheDir().getAbsolutePath();
        } else {//没外部存储就使用内部存储
            directoryPath = context.getCacheDir().getPath();
        }
        return directoryPath;
    }

    /**
     * 获取该程序的安装包路径，不需要申请权限
     * /data/app/com.xx.xx/base.apk
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getPackageResourcePath(Context context) {
        return context.getPackageResourcePath();
    }

    /**
     * 获取该程序对应的apk文件的路径，不需要申请权限
     * /data/app/com.xx.xx/base.apk
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getPackageCodePath(Context context) {
        return context.getPackageCodePath();
    }

    /**
     * 返回通过Context.openOrCreateDatabase 创建的数据库文件，不需要申请权限
     * /data/data/com.xx.xx/databases/xxFileName
     * /data/user/0/com.xx.xx/databases/xxFileName
     *
     * @param context  context
     * @param fileName fileName
     * @return File
     */
    public static File getDatabasePath(Context context, String fileName) {
        return context.getDatabasePath(fileName);
    }

    /**
     * 用于获取APP的files目录，不需要申请权限
     * /data/data/com.xx.xx/files
     * /data/user/0/com.xx.xx/files
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getFilesDir(Context context) {
        return context.getFilesDir().getPath();
    }


    /**
     * 获得缓存目录
     * 需要申请权限
     * /cache
     *
     * @return 路径
     */
    public static String getCacheSdcard() {
        return Environment.getDownloadCacheDirectory().getPath();
    }

    /**
     * 用于获取APP SDK中的obb目录
     * /mnt/sdcard/Android/obb/com.xx.xx
     * /storage/emulated/0/Android/obb/com.xx.xx
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getObbDir(Context context) {
        return context.getObbDir().getPath();
    }

    /**
     * 获得SD卡目录（获取的是手机外置sd卡的路径）
     * 需要申请权限
     * /storage/emulated/0
     * /sdcard
     *
     * @return 路径
     */
    public static String getSDCard() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取照片文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getDCIM() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    }

    /**
     * 获取音乐文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getMUSIC() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
    }

    /**
     * 获取图片文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getPICTURES() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    }

    /**
     * 获取电影文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getMOVIES() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
    }

    /**
     * 获取下载文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getDOWNLOADS() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    /**
     * 获取文档文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getDOCUMENTS() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
    }

    /**
     * 获取播客文件夹
     * 需要申请权限
     *
     * @return 路径
     */

    public static String getPODCASTS() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getPath();
    }

    /**
     * 获取铃声文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getRINGTONES() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getPath();
    }

    /**
     * 获取警告文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getALARMS() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS).getPath();
    }

    /**
     * 获取通知文件夹
     * 需要申请权限
     *
     * @return 路径
     */
    public static String getNOTIFICATIONS() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getPath();
    }

    /**
     * 获得根目录(内部存储路径)
     * 需要申请权限
     * /data
     *
     * @return 路径
     */
    public static String getData() {
        return Environment.getDataDirectory().getPath();
    }


    /**
     * 获得系统目录
     * 需要申请权限
     * /system
     *
     * @return 路径
     */
    public static String getRoot() {
        return Environment.getRootDirectory().getPath();
    }
}
