package com.yujing.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
yPermissions.setSuccessListener {
    YLog.i("成功$it")
}.setFailListener {
    YLog.i("失败$it")
}.setAllSuccessListener {
    YLog.i("全部成功")
    YPermissions.requestAll(this)
}.request(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA
)
 */
class YPermissions(val activity: ComponentActivity) {
    companion object {
        /**
         * 获取Manifest中的全部权限
         */
        fun getManifestPermissions(context: Context): Array<String>? {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
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

    //即将请求的数组
    private var array: Array<String>? = null

    /**
     * 获取权限
     * @param permissions 权限列表
     */
    fun request(vararg permissions: String):
            YPermissions {
        if (Build.VERSION.SDK_INT < 23) {
            for (item in permissions)
                successListener?.value(item)
            allSuccessListener?.value()
            return this
        }
        //没有权限的列表
        val noPermissions = ArrayList<String>()
        for (item in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, item))
                noPermissions.add(item)
        }
        //列表如果空,不请求权限，否则请求
        if (noPermissions.isEmpty()) {
            for (item in permissions)
                successListener?.value(item)
            allSuccessListener?.value()
        } else {
            //旧的方法
            //ActivityCompat.requestPermissions(activity!!, toApplyList.toArray(tmpList), 888)
            array = noPermissions.toArray(arrayOfNulls<String>(noPermissions.size))
            //注册权限请求
            var register: ActivityResultLauncher<Array<String>>? = null
            //注册生命周期
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    register = activity.activityResultRegistry.register("YPermissions", ActivityResultContracts.RequestMultiplePermissions()) { map ->
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
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    register?.unregister()
                }
            })
            //注册
            if (register != null) register?.launch(array)
        }
        return this
    }
}