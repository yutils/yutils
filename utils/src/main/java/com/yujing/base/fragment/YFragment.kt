package com.yujing.base.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.trello.rxlifecycle3.components.support.RxFragment
import com.yujing.base.contract.YLifeEventInterface
import com.yujing.base.contract.YLifeEventListener

/**
 * 监听YFragment事件
 * @author yujing 2020年12月21日10:57:20
 */
/*用法
val base = fragment as YFragment
base.setEventListener { event, obj ->
    if (event == YLifeEvent.onDestroy) {
        YLog.d("关闭了")
    } else if (event == YLifeEvent.onActivityResult) {
        val result = obj as YReturn3<Int, Int, Intent>
    }
}

activity.lifecycle().subscribe { life ->
    YLog.d("当前生命周期状态：$life")
}
 */
abstract class YFragment : RxFragment(), YLifeEventInterface {
    override var yEventListener: YLifeEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<RxFragment>.onCreate(savedInstanceState)
        super<YLifeEventInterface>.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<RxFragment>.onActivityResult(requestCode, resultCode, data)
        super<YLifeEventInterface>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super<RxFragment>.onConfigurationChanged(newConfig)
        super<YLifeEventInterface>.onConfigurationChanged(newConfig)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super<RxFragment>.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super<YLifeEventInterface>.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super<RxFragment>.onHiddenChanged(hidden)
        super<YLifeEventInterface>.onHiddenChanged(hidden)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super<RxFragment>.onViewCreated(view, savedInstanceState)
        super<YLifeEventInterface>.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super<RxFragment>.onPause()
        super<YLifeEventInterface>.onPause()
    }

    override fun onDestroyView() {
        super<RxFragment>.onDestroyView()
        super<YLifeEventInterface>.onDestroyView()
    }

    override fun onDetach() {
        super<RxFragment>.onDetach()
        super<YLifeEventInterface>.onDetach()
    }

    override fun onStart() {
        super<RxFragment>.onStart()
        super<YLifeEventInterface>.onStart()
    }

    override fun onResume() {
        super<RxFragment>.onResume()
        super<YLifeEventInterface>.onResume()
    }


    override fun onStop() {
        super<RxFragment>.onStop()
        super<YLifeEventInterface>.onStop()
    }

    override fun onDestroy() {
        super<RxFragment>.onDestroy()
        super<YLifeEventInterface>.onDestroy()
    }
}