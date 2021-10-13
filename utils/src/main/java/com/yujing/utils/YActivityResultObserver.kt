package com.yujing.utils

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 避免必须在onCreate中注册registerForActivityResult事件
 * @author yujing 2021年10月13日14:58:27
 */
/*
使用方法：
kotlin:
var activityResultObserver = YActivityResultObserver(activityResultRegistry) {
    if (it!!.resultCode == Activity.RESULT_OK) {
        YLog.e("获取到安装权限")
    }
}
lifecycle.addObserver(activityResultObserver)
val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
activityResultObserver.launch(intent)


java:
//请求权限之后回调
YActivityResultObserver activityResultObserver = new YActivityResultObserver(activity.getActivityResultRegistry(), "YInstallApk", result -> {
    if (result.getResultCode() == Activity.RESULT_OK)
        if (apkUri != null) installApk(activity, apkUri);
    return null;
});
activity.getLifecycle().addObserver(activityResultObserver);
activityResultObserver.launch(intent);
 */
class YActivityResultObserver(val registry: ActivityResultRegistry, val key: String = "key", val onResult: (ActivityResult?) -> Unit) : DefaultLifecycleObserver {
    private var arl: ActivityResultLauncher<Intent>? = null
    override fun onCreate(owner: LifecycleOwner) {
        arl = registry.register(key, ActivityResultContracts.StartActivityForResult(), onResult)
    }

    fun launch(intent: Intent) {
        arl?.launch(intent)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        arl?.unregister()
    }
}