package com.yujing.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import com.yujing.contract.YListener
import com.yujing.utils.YLog
import com.yujing.utils.YScreenUtil
import java.lang.Deprecated

/**
 * YBaseDialog的启动关闭动画
 */
@Deprecated
class YBaseDialogAnimation {
    companion object {
        private const val TAG = "YAnimation"
    }

    private var animationX = 0.5f
    private var animationY = 0.5f //动画缩放开始点
    var strokeWidth = 0f // 边框宽度，乘以屏幕比例
    var roundRadius = 0f // 圆角半径，乘以屏幕比例
    private var dialog: Dialog? = null
    private var view: View? = null

    fun init(dialog: Dialog?, view: View?) {
        this.dialog = dialog
        this.view = view
    }

    /**
     * 缩放动画起点和结束点，在屏幕上的相对位置
     *
     * @param animationX x占比
     * @param animationY y占比
     */
    fun setAnimationLocation(animationX: Float, animationY: Float) {
        this.animationX = animationX
        this.animationY = animationY
    }

    val startAnimation: Animation
        get() {
            if (view == null) {
                YLog.e(TAG, "请先初始化：init(Dialog dialog, View view)")
            }
            //缩放动画
            val scaleAnimation = ScaleAnimation( //起始X，结束X，起始Y，结束Y
                0F, 1F, 0F, 1F,
                Animation.RELATIVE_TO_SELF, animationX,  //中心X点
                Animation.RELATIVE_TO_SELF, animationY
            ) //中心Y点
            scaleAnimation.duration = 400 //时长

            //透明动画，透明到不透明
            val alphaAnimation = AlphaAnimation(0f, 1.0f)
            alphaAnimation.duration = 400 //时长
            val animationSet = AnimationSet(true)
            animationSet.addAnimation(scaleAnimation) //添加
            animationSet.addAnimation(alphaAnimation) //添加
            return animationSet
        }

    //出场动画
    fun getExitAnimation(endListener: YListener?): Animation? {
        if (view == null) {
            YLog.e(TAG, "请先初始化：init(Dialog dialog, View view)")
            return null
        }
        val window = dialog!!.window
        window?.let { clearWindowDrawable(it) }
        //缩放动画
        val scaleAnimation = ScaleAnimation( //起始X，结束X，起始Y，结束Y
            1F, 0F, 1F, 0F,
            Animation.RELATIVE_TO_SELF, animationX,  //中心X点
            Animation.RELATIVE_TO_SELF, animationY
        ) //中心Y点
        scaleAnimation.duration = 200 //时长

        //透明动画，透明到不透明
        val alphaAnimation = AlphaAnimation(1f, 0.0f)
        alphaAnimation.duration = 200 //时长
        alphaAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view!!.post { endListener?.value() }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation) //添加
        animationSet.addAnimation(alphaAnimation) //添加
        return animationSet
    }

    //清除dialog的边框
    private fun clearWindowDrawable(window: Window) {
        //设置window的Background为圆角
        val gradientDrawable = GradientDrawable()
        val strokeColor = Color.parseColor("#00000000") //边框颜色
        val fillColor = Color.parseColor("#00000000") //内部填充颜色
        gradientDrawable.setColor(fillColor)
        gradientDrawable.cornerRadius = YScreenUtil.dp2px(dialog!!.context, roundRadius).toFloat()
        gradientDrawable.setStroke(YScreenUtil.dp2px(dialog!!.context, strokeWidth), strokeColor)
        //应用背景颜色
        window.setBackgroundDrawable(gradientDrawable)
    }
}