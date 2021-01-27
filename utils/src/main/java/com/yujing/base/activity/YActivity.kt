package com.yujing.base.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.yujing.base.contract.YLifeEventInterface
import com.yujing.base.contract.YLifeEventListener

/**
 * 监听activity事件
 * @author yujing 2020年12月21日12:56:03
 */
/*
用法：
val base = activity as YActivity
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
abstract class YActivity : RxAppCompatActivity(), YLifeEventInterface {
    override var yEventListeners: MutableList<YLifeEventListener> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<RxAppCompatActivity>.onCreate(savedInstanceState)
        super<YLifeEventInterface>.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<RxAppCompatActivity>.onActivityResult(requestCode, resultCode, data)
        super<YLifeEventInterface>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super<RxAppCompatActivity>.onConfigurationChanged(newConfig)
        super<YLifeEventInterface>.onConfigurationChanged(newConfig)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super<RxAppCompatActivity>.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        super<YLifeEventInterface>.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super<RxAppCompatActivity>.onNewIntent(intent)
        super<YLifeEventInterface>.onNewIntent(intent)
    }

    override fun onStart() {
        super<RxAppCompatActivity>.onStart()
        super<YLifeEventInterface>.onStart()
    }

    override fun onResume() {
        super<RxAppCompatActivity>.onResume()
        super<YLifeEventInterface>.onResume()
    }

    override fun onPause() {
        super<RxAppCompatActivity>.onPause()
        super<YLifeEventInterface>.onPause()
    }

    override fun onRestart() {
        super<RxAppCompatActivity>.onRestart()
        super<YLifeEventInterface>.onRestart()
    }

    override fun onStop() {
        super<RxAppCompatActivity>.onStop()
        super<YLifeEventInterface>.onStop()
    }

    override fun onBackPressed() {
        super<RxAppCompatActivity>.onBackPressed()
        super<YLifeEventInterface>.onBackPressed()
    }

    override fun finish() {
        super<RxAppCompatActivity>.finish()
        super<YLifeEventInterface>.finish()
    }

    override fun onDestroy() {
        super<RxAppCompatActivity>.onDestroy()
        super<YLifeEventInterface>.onDestroy()
    }
}