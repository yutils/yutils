package com.yujing.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yujing.contract.YListener
import com.yujing.contract.YListener1
import java.util.*

/**
 * 权限请求
 *
 * @author 余静 2021年7月9日17:47:39
 */
/*用法
//请求全部Manifest中注册的权限，不判断成功
YPermissions.requestAll(this)


//实例化权限监听
val yPermissions = YPermissions(this)

//register需要在onAttach() 或 onCreate()调用
yPermissions.register()

//请求权限
yPermissions.request(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
).setSuccessListener {
    YLog.i("成功$it")
}.setFailListener{
    YLog.i("失败$it")
}.setAllSuccessListener{
    YLog.i("全部成功")
}

 */
class YPermissions(val activity: AppCompatActivity) {
    companion object {
        /**
         * 获取Manifest中的全部权限
         */
        fun getManifestPermissions(context: Context): Array<String>? {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PERMISSIONS
                )
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
            return packageInfo?.requestedPermissions
        }

        /**
         * 判断是否有某些权限
         *
         * @param context     context
         * @param permissions 权限。如：Manifest.permission.CAMERA
         * @return 是否有
         */
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT < 23) return true
            for (perm in permissions) {
                if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(context!!, perm!!)
                ) return false
            }
            return true
        }

        /**
         * 获取权限
         */
        fun requestAll(activity: AppCompatActivity) {
            YPermissions(activity).request(*getManifestPermissions(activity)!!)
        }
    }

    //请求成功权限
    private var successListener: YListener1<String>? = null

    //请求失败权限
    private var failListener: YListener1<String>? = null

    //全部权限请求成功回调
    private var allSuccessListener: YListener? = null

    fun setSuccessListener(successListener: YListener1<String>): YPermissions {
        this.successListener = successListener
        return this
    }

    fun setFailListener(failListener: YListener1<String>): YPermissions {
        this.failListener = failListener
        return this
    }

    fun setAllSuccessListener(allSuccessListener: YListener): YPermissions {
        this.allSuccessListener = allSuccessListener
        return this
    }

    //注册
    private var register: ActivityResultLauncher<Array<String>>? = null

    //即将请求的数组
    private var array: Array<String>? = null

    /**
     * 注册回调监听
     * 需要在onAttach() 或 onCreate()调用
     */
    fun register(): YPermissions {
        try {
            register =
                activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
                    array?.let {
                        var findFail = false//是否有不成功的权限
                        for (item in it) {
                            //再检查一遍
                            if (map[item]!!) {
                                //同意
                                successListener?.value(item)
                            } else {
                                findFail = true
                                //拒绝
                                failListener?.value(item)
                            }
                        }
                        //如果没有不成功的权限
                        if (!findFail) {
                            allSuccessListener?.value()
                        }
                        array = null
                    }
                }
        } catch (e: java.lang.IllegalStateException) {
            YLog.e("请在onAttach() 或 onCreate() 中注册调用 register()")
        }
        return this
    }

    /**
     * 获取权限
     * @param permissions 权限列表
     */
    fun request(vararg permissions: String):
            YPermissions {
        //没有权限的列表
        val noPermissions = ArrayList<String>()
        for (item in permissions) {
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(activity, item)
            ) {
                noPermissions.add(item)
            }
        }
        //列表如果不是空,请求权限
        if (noPermissions.isNotEmpty()) {
            //旧的方法
            //ActivityCompat.requestPermissions(activity!!, toApplyList.toArray(tmpList), 888)
            array = noPermissions.toArray(arrayOfNulls<String>(noPermissions.size))
            if (register != null) {
                register?.launch(array)
            } else {
                ActivityCompat.requestPermissions(activity, array!!, 888)
            }
        } else {
            allSuccessListener?.value()
        }
        return this
    }
}