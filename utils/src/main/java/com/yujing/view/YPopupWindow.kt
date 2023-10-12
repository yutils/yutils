package com.yujing.view

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.utils.YScreenUtil
import java.lang.Deprecated

/*
用法

//创建
var popupWindow: YPopupWindow<PopupWindowBinding>
//初始化
shoppingCar = YPopupWindow(this,R.layout.popup_window)

//获取焦点，如果设置true，那么点击空白处可以退出，如果设置fasle,弹出PopupWindow的时候没有焦点，就不会影响沉浸式状态栏的显示了。但是空白不能退，就设置成全屏popupWindow，自己监听空白点击吧。
//popupWindow.popupWindow.isFocusable=true

//显示，对其到binding.llRoot
popupWindow.show( binding.llRoot)

//点击空白关闭,给空白的view写点击事件
popupWindow.binding.llSpace.setOnClickListener {
    popupWindow.popupWindow.dismiss()
}
※※※※※注意：退出activity时，一定要关闭popupWindow※※※※※


用法2,全屏弹窗
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:background="@color/trans_Black_8"
        tools:background="@color/gray_A">

        <LinearLayout
            android:id="@+id/space"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:orientation="vertical"
            android:layout_weight="40" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="60"
            android:orientation="vertical">
            //正文

        </LinearLayout>
    </LinearLayout>
</layout>

class ChoseWindow(var activity: Activity) {
    //创建
    var pw: YPopupWindow<PopupWindowChoseBinding>? = null

    fun show() {
        YBusUtil.init(this)
        //初始化
        pw = YPopupWindow(activity, R.layout.popup_window)
        pw?.setFullScreen(true)
        //屏幕绝对位置
        pw?.show()

        //点击空白
        //pw?.binding?.space?.setOnClickListener { dismiss() }
    }

    //退出activity时，一定要关闭popupWindow
    fun dismiss() {
        YBusUtil.onDestroy(this)
        pw?.popupWindow?.dismiss()
    }
}
 */

/*
//------------------------------------------------------全屏弹窗完整原生用法------------------------------------------------------
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <FrameLayout
        android:id="@+id/space"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#60000000">

        <!--这个view的位置由代码动态调整-->
        <LinearLayout
            android:id="@+id/ll_root"
            android:layout_width="@dimen/dp500"
            android:layout_height="@dimen/dp363"
            android:background="@drawable/ic_farmer_info"
            android:orientation="vertical"
            android:padding="1px">

        </LinearLayout>
    </FrameLayout>
</layout>


import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.yujing.bus.YBusUtil
import com.yujing.utils.YScreenUtil
import com.yujing.view.YView

@Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")
class PopupWindowFarmerInfo(var activity: Activity) {
    //窗口
    var popupWindow: PopupWindow
    var binding: PopupwindowFarmerInfoBinding

    init {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.popupwindow_farmer_info, null)
        binding = DataBindingUtil.bind(view)!!

        popupWindow = PopupWindow()
        popupWindow.contentView = view

        //获取焦点，如果设置true，那么点击空白处可以退出，如果设置fasle,弹出PopupWindow的时候没有焦点，就不会影响沉浸式状态栏的显示了。但是空白不能退，就设置成全屏popupWindow，自己监听空白点击吧。
        popupWindow.isFocusable = false
        popupWindow.isTouchable = true
        //背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))
        //设置是否全屏
        setFullScreen(true)

        //设置SelectPicPopupWindow弹出窗体动画效果
        //popupWindow.animationStyle = android.R.style.Animation_InputMethod
    }

    fun show(view: View) {
        YBusUtil.init(this)
        //显示窗口，显示在view下面或者上面
        popupWindow.showAtLocation(activity.window.decorView, Gravity.BOTTOM, 0, 0)

        //偏移位置
        val point = YView.getViewLocationOnScreen(view)
        binding.llRoot.x = (point.x + view.width / 2).toFloat()
        binding.llRoot.y = (point.y + view.height).toFloat()

        //点击空白
        binding.space.setOnClickListener { dismiss() }
    }

    private fun setFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            //(YScreenUtil.getScreenWidth(activity) * (2F / 5F)).toInt()
            popupWindow.height = YScreenUtil.getScreenHeight(activity)
            popupWindow.width = YScreenUtil.getScreenWidth(activity)
            popupWindow.isClippingEnabled = false
        } else {
            //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键
            popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
            popupWindow.height = WindowManager.LayoutParams.MATCH_PARENT
            popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            popupWindow.isClippingEnabled = true
        }
    }

    //退出activity时，一定要关闭popupWindow
    fun dismiss() {
        YBusUtil.onDestroy(this)
        popupWindow.dismiss()
    }
}
 */

/**
 * 弹窗
 * @author yujing 2021年7月21日10:56:54
 */
@Deprecated
class YPopupWindow<B : ViewDataBinding>(var activity: Activity, var layout: Int) {
    //窗口
    var popupWindow: PopupWindow
    var binding: B

    init {
        val view: View =
            LayoutInflater.from(activity).inflate(layout, null)
        binding = DataBindingUtil.bind(view)!!
        popupWindow = PopupWindow()
        popupWindow.contentView = view
        //获取焦点，如果设置true，那么点击空白处可以退出，如果设置fasle,弹出PopupWindow的时候没有焦点，就不会影响沉浸式状态栏的显示了。但是空白不能退，就设置成全屏popupWindow，自己监听空白点击吧。
        popupWindow.isFocusable = false
        popupWindow.isTouchable = true
        setFullScreen(false)
        //设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.animationStyle = android.R.style.Animation_InputMethod
        //背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))
    }

    fun setFullScreen(isFullScreen: Boolean) {
        if (isFullScreen) {
            //(YScreenUtil.getScreenWidth(activity) * (2F / 5F)).toInt()
            popupWindow.height = YScreenUtil.getScreenHeight(activity)
            popupWindow.width = YScreenUtil.getScreenWidth(activity)
            popupWindow.isClippingEnabled = false
        } else {
            //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键
            popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
            popupWindow.height = WindowManager.LayoutParams.MATCH_PARENT
            popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            popupWindow.isClippingEnabled = true
        }
    }


    fun show() {
        popupWindow.showAtLocation(activity.window.decorView, Gravity.BOTTOM, 0, 0)
    }

    //显示窗口，显示在view下面或者上面
    fun show(parentView: View) {
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0)
    }

    //显示窗口，显示在view下面或者上面
    fun showAsDropDown(parentView: View) {
        //获取View当前绝对位置，相对当前activity而言.getLocationOnScreen(position);相对整个屏幕而言.
        val position = IntArray(2)
        parentView.getLocationInWindow(position)
        //相对于父容器位置显示，偏移X，偏移Y.。相对于整个屏幕:popupWindow!!.showAtLocation(parentView, Gravity.CENTER,0,0)
        popupWindow.showAsDropDown(parentView, 0, 0)
        //变暗
        setAlpha(0.5F)
        //变亮
        popupWindow.setOnDismissListener { setAlpha(1F) }
    }

    //屏幕变暗
    fun setAlpha(alpha: Float) {
        val lp: WindowManager.LayoutParams = activity.window.attributes
        lp.alpha = alpha
        activity.window.attributes = lp
    }
}