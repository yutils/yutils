package com.yujing.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 通知栏下载文件
 *
 * @author yujing 2020年8月30日11:52:28
 */
/*使用方法举例
    private var yNoticeDownload: YNoticeDownload?=null
    private fun download() {
        val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
        if (yNoticeDownload==null)
            yNoticeDownload = YNoticeDownload(this, url)
        yNoticeDownload?.setDownLoadFail { show("下载失败") }
        yNoticeDownload?.setDownLoadComplete { uri, file ->show("下载完成")}
        yNoticeDownload?.setDownLoadProgress { downloadSize, fileSize ->
            val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
            //"进度：$progress%"
        }
        yNoticeDownload?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        yNoticeDownload?.onDestroy()
    }
 */
@SuppressWarnings("unused")
public class YNoticeDownload {
    private DownloadManager mDownloadManager;//系统下载管理器
    private long id;//DownloadManager的下载id
    private DownloadReceiver mReceiver;//广播
    private Activity activity;
    private String url;
    private String title;//下载时候的自定义标题
    private Timer myTimer;
    private DownLoadComplete downLoadComplete;//下载完成回调
    private DownLoadProgress downLoadProgress;//下载过程回调
    private DownLoadFail downLoadFail;//下载失败
    private boolean isAPK;//是否是apk文件，初始化时候将会根据扩展名自动判断
    private File file;//下载的文件

    public YNoticeDownload(Activity activity) {
        this(activity, null);
    }

    public YNoticeDownload(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
        //注册广播
        mReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        activity.registerReceiver(mReceiver, intentFilter); //注册自定义广播
        mDownloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //申请权限
        String[] Permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String ps : Permissions) {
            if (ContextCompat.checkSelfPermission(activity, ps) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, Permissions, 1);// 申请权限
            }
        }
        if (url.length() > 4 && url.substring(url.length() - 4).toLowerCase().equals(".apk")) {
            isAPK = true;
        }
    }

    public void start() {
        if (url == null) return;
        //设置URL地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置wifi才能下载
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //只是下载状态才显示。当处于下载中状态和下载完成时状态均在通知栏中显示VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置下载路径
        String newString = url.substring(url.lastIndexOf("/"));//返回一个新的字符串，它是此字符串的一个子字符串。
        file = new File(YPath.getFilePath(activity, "download") + newString);//外部存储卡目录
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        Uri uri = Uri.fromFile(file);
        request.setDestinationUri(uri);
        //设置通知栏显示文字
        request.setTitle(title == null ? "文件下载中" : title);
        request.setDescription("请稍后...");
        request.setVisibleInDownloadsUi(true);//可以被媒体设备扫描到
        if (isAPK)
            request.setMimeType("application/vnd.android.package-archive");
        id = mDownloadManager.enqueue(request);
        updateViews();
        finish = false;
    }

    private void updateViews() {
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                getProgress();
            }
        }, 0, 100);
    }

    private boolean finish = false;

    private void getProgress() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            final int downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            final int fileSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            cursor.close();
            if (fileSize <= 0) return;
            if (!activity.isFinishing() && !finish && downLoadProgress != null)
                activity.runOnUiThread(() -> downLoadProgress.progress(downloaded, fileSize));
            if (downloaded == fileSize) {
                finish = true;
                myTimer.cancel();
            }
        } else {
            //失败
            myTimer.cancel();
            if (!activity.isFinishing() && downLoadFail != null)
                activity.runOnUiThread(() -> downLoadFail.fail());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAPK() {
        return isAPK;
    }

    public void setAPK(boolean APK) {
        isAPK = APK;
    }

    public void setDownLoadComplete(DownLoadComplete downLoadComplete) {
        this.downLoadComplete = downLoadComplete;
    }

    public void setDownLoadProgress(DownLoadProgress downLoadProgress) {
        this.downLoadProgress = downLoadProgress;
    }

    public void setDownLoadFail(DownLoadFail downLoadFail) {
        this.downLoadFail = downLoadFail;
    }

    /**
     * 广播接收器，接受ACTION_DOWNLOAD_COMPLETE和ACTION_NOTIFICATION_CLICKED
     */
    class DownloadReceiver extends BroadcastReceiver {
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                //final long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
                Cursor cursor = mDownloadManager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            getProgress();
                            if (!activity.isFinishing() && downLoadComplete != null)
                                activity.runOnUiThread(() -> downLoadComplete.complete(mDownloadManager.getUriForDownloadedFile(id), file));
                            break;
                        case DownloadManager.STATUS_FAILED:
                            myTimer.cancel();
                            //错误原因
                            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            if (downLoadFail != null)
                                activity.runOnUiThread(() -> downLoadFail.fail());
                            break;
                        case DownloadManager.STATUS_PAUSED:
                            break;
                        case DownloadManager.STATUS_PENDING:
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            break;
                    }
                    cursor.close();
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
                //下载过程中的点击事件
                if (!activity.isFinishing())
                    YToast.show(activity, "正在下载，请稍后。");
            }
        }
    }

    public interface DownLoadComplete {
        void complete(Uri uri, File file);
    }

    public interface DownLoadFail {
        void fail();
    }

    public interface DownLoadProgress {
        void progress(long downloadSize, long fileSize);
    }

    public void onResume() {
        if (mReceiver != null)
            activity.registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void onDestroy() {
        //注销广播
        if (myTimer != null)
            myTimer.cancel();
        if (mReceiver != null)
            activity.unregisterReceiver(mReceiver);
    }
}