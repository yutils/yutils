package com.yujing.base.contract

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.yujing.contract.YReturn2
import com.yujing.contract.YReturn3

/**
 * activity基类
 * 实现监听activity事件
 */
/* 用法
abstract class YActivity : RxAppCompatActivity(), YActivityInterface {
override var yEventListener: YEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super<RxAppCompatActivity>.onCreate(savedInstanceState)
        super<YActivityInterface>.onCreate(savedInstanceState)
    }
    override fun onStop() {
        super<RxAppCompatActivity>.onStop()
        super<YActivityInterface>.onStop()
    }
    ...
}

//用的地方
val base = activity as YActivity
base.setEventListener { event, obj ->
    if (event == YLifeEvent.onDestroy) {
        YLog.d("关闭了")
    } else if (event == YLifeEvent.onActivityResult) {
        val result = obj as YReturn3<Int, Int, Intent>
    }
}
 */
interface YLifeEventInterface {
    //-------------------------------------public-------------------------------------
    var yEventListener: YLifeEventListener?

    fun setEventListener(yEventListener: YLifeEventListener?) {
        this.yEventListener = yEventListener
    }

    fun onCreate(savedInstanceState: Bundle?) {
        yEventListener?.event(YLifeEvent.onCreate, null)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        yEventListener?.event(
            YLifeEvent.onActivityResult,
            YReturn3(requestCode, resultCode, data)
        )
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        yEventListener?.event(YLifeEvent.onConfigurationChanged, newConfig)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        yEventListener?.event(
            YLifeEvent.onRequestPermissionsResult,
            YReturn3(requestCode, permissions, grantResults)
        )
    }


    fun onStart() {
        yEventListener?.event(YLifeEvent.onStart, null)
    }

    fun onResume() {
        yEventListener?.event(YLifeEvent.onResume, null)
    }

    fun onPause() {
        yEventListener?.event(YLifeEvent.onPause, null)
    }

    fun onRestart() {
        yEventListener?.event(YLifeEvent.onRestart, null)
    }

    fun onStop() {
        yEventListener?.event(YLifeEvent.onStop, null)
    }

    fun onDestroy() {
        yEventListener?.event(YLifeEvent.onDestroy, null)
    }

    //-------------------------------------activity-------------------------------------

    fun onNewIntent(intent: Intent?) {
        yEventListener?.event(YLifeEvent.onNewIntent, null)
    }

    fun onBackPressed() {
        yEventListener?.event(YLifeEvent.onBackPressed, null)
    }

    fun finish() {
        yEventListener?.event(YLifeEvent.finish, null)
    }
    //-------------------------------------fragment-------------------------------------

    fun onHiddenChanged(hidden: Boolean) {
        yEventListener?.event(YLifeEvent.onHiddenChanged, hidden)
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        yEventListener?.event(YLifeEvent.onViewCreated, YReturn2(view, savedInstanceState))
    }


    fun onDestroyView() {
        yEventListener?.event(YLifeEvent.onDestroyView, null)
    }

    fun onDetach() {
        yEventListener?.event(YLifeEvent.onDetach, null)
    }

}