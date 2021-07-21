package com.yujing.view

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.utils.YScreenUtil

/**
 * 弹窗
 * @author yujing 2021年7月21日10:56:54
 */
/*
用法

//创建
lateinit var shoppingCar: YPopupWindow<ActivityGoodsPopupWindowBinding>
//初始化
shoppingCar = YPopupWindow(this,R.layout.activity_goods_popup_window)

//获取焦点，如果设置true，那么点击空白处可以退出，如果设置fasle,弹出PopupWindow的时候没有焦点，就不会影响沉浸式状态栏的显示了。但是空白不能退，就设置成全屏popupWindow，自己监听空白点击吧。
//shoppingCar.popupWindow.isFocusable=true

//显示，对其到binding.llRoot
shoppingCar.show( binding.llRoot)

//点击空白关闭,给空白的view写点击事件
shoppingCar.binding.llSpace.setOnClickListener {
    shoppingCar.popupWindow.dismiss()
}
 */

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
        popupWindow.height = YScreenUtil.getScreenHeight(activity)
        popupWindow.width = YScreenUtil.getScreenWidth(activity) //(YScreenUtil.getScreenWidth(activity) * (2F / 5F)).toInt()
        //获取焦点，如果设置true，那么点击空白处可以退出，如果设置fasle,弹出PopupWindow的时候没有焦点，就不会影响沉浸式状态栏的显示了。但是空白不能退，就设置成全屏popupWindow，自己监听空白点击吧。
        popupWindow.isFocusable = false
        popupWindow.isTouchable = true
        //解决 标题栏没有办法遮罩的问题
        popupWindow.isClippingEnabled = false
        //设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.animationStyle = android.R.style.Animation_InputMethod
        //背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.argb(0, 0, 0, 0)))
    }

    //显示窗口，显示在view下面或者上面
    fun show(parentView: View) {
        //获取View当前绝对位置，相对当前activity而言.getLocationOnScreen(position);相对整个屏幕而言.
        val position = IntArray(2)
        parentView.getLocationInWindow(position)
        //显示，偏移X，偏移Y，相对于父容器位置 popupWindow!!.showAtLocation(parentView, Gravity.CENTER,parentView.width,0)
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