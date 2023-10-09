package com.yujing.utils

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.Deprecated

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



原生用法：
//跳转
activityResultRegistry.register("456",ActivityResultContracts.StartActivityForResult()){result->
    if (result?.resultCode != Activity.RESULT_OK) return@register
}.run { launch(Intent(this@Activity, CaptureActivity::class.java)) }

//获取权限
activityResultRegistry.register("123", ActivityResultContracts.RequestPermission()){
  if (!it) return@register
}.run { launch(Manifest.permission.CAMERA) }

//拍照
val file = File(getExternalFilesDir("")!!.absolutePath + "/img/1.jpg")
if (!file.parentFile.exists()) file.parentFile.mkdirs()
val uri: Uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".fileProvider", file)
//拍照
activityResultRegistry.register("789", ActivityResultContracts.TakePicture()) {
    if (!it) return@register  //拍照失败
    val bitmap = YUri.getBitmap(this, uri)
    YImageDialog.show(bitmap)
    YLog.i("分辨率：${bitmap.width}x${bitmap.height} 路径：${file}   uri：${uri}")
}.run { launch(uri) }
 */

@Deprecated
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