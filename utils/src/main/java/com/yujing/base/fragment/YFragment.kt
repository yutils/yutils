package com.yujing.base.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yujing.base.contract.YLifeEvent
import com.yujing.base.contract.YLifeEventListener
import com.yujing.contract.YReturn2
import com.yujing.contract.YReturn3
import java.lang.Deprecated

/**
 * 监听YFragment事件
 * @author 余静 2021年8月4日16:31:50
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
 */
@kotlin.Deprecated("作废，不再使用")
abstract class YFragment : Fragment() {
    var yEventListeners: MutableList<YLifeEventListener> = ArrayList()

    fun setEventListener(yEventListener: YLifeEventListener) {
        this.yEventListeners.add(yEventListener)
    }

    fun removeEventListener(yEventListener: YLifeEventListener) {
        yEventListeners.remove(yEventListener)
    }

    fun clearEventListener() {
        yEventListeners.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (item in yEventListeners) item.event(YLifeEvent.onCreate, null)
    }

    @Deprecated
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (item in yEventListeners) item.event(
            YLifeEvent.onActivityResult,
            YReturn3(requestCode, resultCode, data)
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        for (item in yEventListeners) item.event(YLifeEvent.onConfigurationChanged, newConfig)
    }

    @Deprecated
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (item in yEventListeners) item.event(
            YLifeEvent.onRequestPermissionsResult,
            YReturn3(requestCode, permissions, grantResults)
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        for (item in yEventListeners) item.event(
            YLifeEvent.onHiddenChanged,
            hidden
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (item in yEventListeners) item.event(
            YLifeEvent.onViewCreated,
            YReturn2(view, savedInstanceState)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (item in yEventListeners) item.event(YLifeEvent.onDestroyView, null)
    }

    override fun onDetach() {
        super.onDetach()
        for (item in yEventListeners) item.event(YLifeEvent.onDetach, null)
    }

    override fun onStart() {
        super.onStart()
        for (item in yEventListeners) item.event(YLifeEvent.onStart, null)
    }

    override fun onResume() {
        super.onResume()
        for (item in yEventListeners) item.event(YLifeEvent.onResume, null)
    }

    override fun onPause() {
        super.onPause()
        for (item in yEventListeners) item.event(YLifeEvent.onPause, null)

    }

    override fun onStop() {
        super.onStop()
        for (item in yEventListeners) item.event(YLifeEvent.onStop, null)

    }

    override fun onDestroy() {
        super.onDestroy()
        for (item in yEventListeners) item.event(YLifeEvent.onDestroy, null)
        clearEventListener()
    }
}