package com.yujing.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.Date

/*
用法：
方法一：

//拍照
YTake.take(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//拍照并剪切
YTake.takeAndCorp(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//选择图片
YTake.chosePicture(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//选择图片并剪切
YTake.chosePictureAndCorp(this) {
    val bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

方法二：
//剪切返回，onCreate中注册
private val cropPicture = registerForActivityResult(YCropPicture(this)) {
    var bitmap = YConvert.uri2Bitmap(this, it)
    YImageDialog.show(bitmap)
}

//拍照返回，onCreate中注册
private val takePicture = registerForActivityResult(YTakePicture(this)) {
    val file = File(YPath.getPICTURES() + "/corp/${Date().time}.jpg")
    cropPicture.launch(Crop(it, file, 400, 400))
}

//拍照，onCreate中注册
val file = File(YPath.getPICTURES() + "/img/${Date().time}.jpg")
takeImage.launch(file)


方法三：（相对于 方法二 不用提前注册）
lifecycle.addObserver(object : DefaultLifecycleObserver {
    var cropPicture: ActivityResultLauncher<Crop>?
    var takePicture: ActivityResultLauncher<File>?
    var registerPermission: ActivityResultLauncher<String>? = null
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        //剪切返回
        cropPicture = activityResultRegistry.register("key2", YCropPicture(applicationContext)) { it: Uri? ->
            if (it == null) return@register
            val bitmap = YConvert.uri2Bitmap(applicationContext, it)
            YImageDialog.show(bitmap)
        }
        //拍照返回
        takePicture = activityResultRegistry.register("key1", YTakePicture(applicationContext)) { it: Uri? ->
            if (it == null) return@register
            val file = File(YPath.getPICTURES() + "/corp/${Date().time}.jpg")
            cropPicture?.launch(Crop(it, file, 400, 400))
        }
        //相机权限返回
        registerPermission = activityResultRegistry.register("CAMERA", ActivityResultContracts.RequestPermission()) { it: Boolean ->
            //同意权限
            if (it) {
                val file = File(YPath.getPICTURES() + "/img/${Date().time}.jpg")
                takePicture?.launch(file)
            }
        }
        //调用相机权限
        registerPermission?.launch(Manifest.permission.CAMERA)
    }
})
 */

class YTake {
    companion object {
        /**
         * 请求权限并拍照，返回URI
         */
        @JvmStatic
        fun take(activity: ComponentActivity, onResult: (Uri?) -> Unit) {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                var takePicture: ActivityResultLauncher<File>? = null
                var registerPermission: ActivityResultLauncher<String>? = null
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    //拍照返回
                    takePicture = activity.activityResultRegistry.register("key1", YTakePicture(activity), onResult)
                    //相机权限返回
                    registerPermission = activity.activityResultRegistry.register("CAMERA", ActivityResultContracts.RequestPermission()) { it: Boolean ->
                        //同意权限
                        if (it) {
                            val file = File(YPath.getPICTURES() + "/img/${Date().time}.jpg")
                            takePicture?.launch(file)
                        }
                    }
                    //调用相机权限
                    registerPermission?.launch(Manifest.permission.CAMERA)
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    takePicture?.unregister()
                    registerPermission?.unregister()
                }
            })
        }

        /**
         * 请求权限并拍照并剪切，返回URI
         */
        @JvmStatic
        fun takeAndCorp(activity: ComponentActivity, outputX: Int = 400, outputY: Int = 400, onResult: (Uri?) -> Unit) {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                var cropPicture: ActivityResultLauncher<Crop>? = null
                var takePicture: ActivityResultLauncher<File>? = null
                var registerPermission: ActivityResultLauncher<String>? = null
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    //剪切返回
                    //cropPicture = activity.activityResultRegistry.register("key2", YCropPicture(activity)) { it: Uri? -> }
                    cropPicture = activity.activityResultRegistry.register("key2", YCropPicture(activity), onResult)
                    //拍照返回
                    takePicture = activity.activityResultRegistry.register("key1", YTakePicture(activity)) {
                        if (it == null) return@register
                        val file = File(YPath.getPICTURES() + "/corp/${Date().time}.jpg")
                        cropPicture?.launch(Crop(it, file, outputX, outputY))
                    }
                    //相机权限返回
                    registerPermission = activity.activityResultRegistry.register("CAMERA", ActivityResultContracts.RequestPermission()) { it: Boolean ->
                        //同意权限
                        if (it) {
                            val file = File(YPath.getPICTURES() + "/img/${Date().time}.jpg")
                            takePicture?.launch(file)
                        }
                    }
                    //调用相机权限
                    registerPermission?.launch(Manifest.permission.CAMERA)
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    cropPicture?.unregister()
                    takePicture?.unregister()
                    registerPermission?.unregister()
                }
            })
        }

        /**
         * 选择照片
         */
        @JvmStatic
        fun chosePicture(activity: ComponentActivity, onResult: (Uri?) -> Unit) {
            val activityResultObserver = YActivityResultObserver(activity.activityResultRegistry, "chosePicture") {
                if (it!!.resultCode == Activity.RESULT_OK) {
                    var uri = it.data?.data
                    onResult.invoke(uri)
                }
            }
            activity.lifecycle.addObserver(activityResultObserver)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultObserver.launch(intent)
        }

        /**
         * 选择照片和剪切
         */
        @JvmStatic
        fun chosePictureAndCorp(activity: ComponentActivity, outputX: Int = 400, outputY: Int = 400, onResult: (Uri?) -> Unit) {
            var cropPicture: ActivityResultLauncher<Crop>? = null
            //剪切
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    cropPicture = activity.activityResultRegistry.register("key2", YCropPicture(activity), onResult)
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    cropPicture?.unregister()
                }
            })
            //选择
            val activityResultObserver = YActivityResultObserver(activity.activityResultRegistry, "chosePicture") {
                if (it!!.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = it.data?.data ?: return@YActivityResultObserver
                    val file = File(YPath.getPICTURES() + "/corp/${Date().time}.jpg")
                    cropPicture?.launch(Crop(uri!!, file, outputX, outputY))
                }
            }
            activity.lifecycle.addObserver(activityResultObserver)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultObserver.launch(intent)
        }
    }
}

/**
 * 拍照，传入File，拍照成功返回Uri
 */
class YTakePicture(val context: Context) : ActivityResultContract<File, Uri?>() {
    var uri: Uri? = null
    override fun createIntent(context: Context, input: File): Intent {
        uri = YPicture.createImageUri(context, input)
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) return null
        return uri
    }
}

/**
 * 照片剪切，传入Crop，拍照成功返回Uri
 */
class Crop(val cropUri: Uri, val outFile: File, val outputX: Int = 0, val outputY: Int = 0)

/**
 * 照片剪切，传入Crop，拍照成功返回Uri
 */
class YCropPicture(val context: Context) : ActivityResultContract<Crop, Uri?>() {
    var uri: Uri? = null
    override fun createIntent(context: Context, input: Crop): Intent {
        uri = YPicture.createImageUri(context, input.outFile)
        return Intent("com.android.camera.action.CROP").apply {
            setDataAndType(input.cropUri, "image/*")
            putExtra("crop", "true")
            // 宽高和比例都不设置时,裁剪框比例和大小都可以随意调整
            if (input.outputX > 0 && input.outputY > 0) {
                // aspectX aspectY 是裁剪框宽高的比例
                putExtra("aspectX", input.outputX)
                putExtra("aspectY", input.outputY)
                // outputX outputY 是裁剪后生成图片的宽高
                putExtra("outputX", input.outputX)
                putExtra("outputY", input.outputY)
            }
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            putExtra("return-data", false)
        }
    }

    //强行弹出null，后续需要判断。
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) return null
        return uri
    }
}