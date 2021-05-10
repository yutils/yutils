package com.yujing.base.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
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
abstract class YActivity : AppCompatActivity(), YLifeEventInterface {
    override var yEventListeners: MutableList<YLifeEventListener> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        super<YLifeEventInterface>.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)
        super<YLifeEventInterface>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super<AppCompatActivity>.onConfigurationChanged(newConfig)
        super<YLifeEventInterface>.onConfigurationChanged(newConfig)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super<AppCompatActivity>.onRequestPermissionsResult(
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
        super<AppCompatActivity>.onNewIntent(intent)
        super<YLifeEventInterface>.onNewIntent(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //这儿返回值，不作判断，无效，因为可能多个地方调用onKeyDown，无法判断应返回内容
        super<YLifeEventInterface>.onKeyDown(keyCode, event)
        return super<AppCompatActivity>.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()
        super<YLifeEventInterface>.onStart()
    }

    override fun onResume() {
        super<AppCompatActivity>.onResume()
        super<YLifeEventInterface>.onResume()
    }

    override fun onPause() {
        super<AppCompatActivity>.onPause()
        super<YLifeEventInterface>.onPause()
    }

    override fun onRestart() {
        super<AppCompatActivity>.onRestart()
        super<YLifeEventInterface>.onRestart()
    }

    override fun onStop() {
        super<AppCompatActivity>.onStop()
        super<YLifeEventInterface>.onStop()
    }

    override fun onBackPressed() {
        super<AppCompatActivity>.onBackPressed()
        super<YLifeEventInterface>.onBackPressed()
    }

    override fun finish() {
        super<AppCompatActivity>.finish()
        super<YLifeEventInterface>.finish()
    }

    override fun onDestroy() {
        super<AppCompatActivity>.onDestroy()
        super<YLifeEventInterface>.onDestroy()
        clearEventListener()
    }
}