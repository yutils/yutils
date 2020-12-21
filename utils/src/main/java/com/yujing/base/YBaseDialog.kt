package com.yujing.base

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.utils.YScreenUtil
import com.yujing.utils.YToast

/**
 * 自定义dialog快速创建基类
 *
 * @param <B> dataBinding类
 * @author yujing 2020年12月21日16:22:07
</B> */
/*
用法：
//kotlin
class TestDialog(activity: Activity) : YBaseDialog<TestDialogBinding>(activity, R.layout.test_dialog) {
    init {
        fullscreen = true
        openAnimation = false
    }
    override fun init() {
    }
}
//java
public class TestDialog extends YBaseDialog<TestDialogBinding> {

    public TestDialog(@NotNull Activity activity, int layout) {
        super(activity, layout);
    }

    @Override
    protected void init() {

    }
}
 */
abstract class YBaseDialog<B : ViewDataBinding>(
    protected var activity: Activity,
    var layout: Int
) :
    Dialog(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar) {
    lateinit var view: View
    lateinit var binding: B
    var mCancelable = true

    //进场动画，出场地动画。因为用户可能调用setAnimationLocation，所以yAnimation必须优先实例化
    var yAnimation = YBaseDialogAnimation()
    var alpha = 1f //透明
    var dimAmount = 0.4f //模糊
    var widthPixels = 0.5f //宽
    var heightPixels = 0.5f //高

    //边框颜色
    var strokeColor = Color.parseColor("#FFFFFFFF")

    //填充颜色
    var fillColor = Color.parseColor("#A0FFFFFF")

    //全屏显示
    var fullscreen = false

    //禁用输入法
    var disableInput = false

    //打开动画
    var openAnimation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = LayoutInflater.from(activity).inflate(layout, null)
        setContentView(view) // 设置布局view
        binding = DataBindingUtil.bind(view)!!
        val window = window
        window?.let { initWindow(it) }
        setCancelable(mCancelable) // 是否允许按返回键
        setCanceledOnTouchOutside(mCancelable) // 触摸屏幕其他区域不关闭对话框
        //打开动画否
        if (openAnimation) {
            yAnimation = YBaseDialogAnimation()
            yAnimation.init(this, view)
            //入场动画
            view.startAnimation(yAnimation.startAnimation)
        }
        init()
    }

    /**
     * 初始化数据
     */
    protected abstract fun init()
    fun ismCancelable(): Boolean {
        return mCancelable
    }

    override fun setCancelable(cancelable: Boolean) {
        mCancelable = cancelable
    }

    /* *************************基本设置************************** */ //配置dialog基本属性
    private fun initWindow(window: Window) {
        if (disableInput) {
            //启动不弹出输入法
            window.setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
        }
        // 设置Gravity居中
        window.setGravity(Gravity.CENTER)
        //获取LayoutParams对象
        val lp = window.attributes
        //设置透明度
        lp.alpha = alpha
        //设置模糊度
        lp.dimAmount = dimAmount
        //设置宽高
        val dm = activity.resources.displayMetrics
        lp.width = (dm.widthPixels * widthPixels).toInt()
        lp.height = (dm.heightPixels * heightPixels).toInt()
        //应用设置
        window.attributes = lp
        //设置window的Background为圆角
        val gradientDrawable = GradientDrawable()
        //当前屏幕与开发屏幕的比例
        val ScaleScreenWidthDp =
            activity.resources.configuration.smallestScreenWidthDp / DevelopmentScreenWidthDp
        val strokeWidth = 2 * ScaleScreenWidthDp // 2dp 边框宽度，乘以屏幕比例
        val roundRadius = 20 * ScaleScreenWidthDp // 20dp 圆角半径，乘以屏幕比例
        gradientDrawable.setColor(fillColor)
        gradientDrawable.cornerRadius = YScreenUtil.dp2px(context, roundRadius).toFloat()
        gradientDrawable.setStroke(YScreenUtil.dp2px(context, strokeWidth), strokeColor)
        //应用背景颜色
        window.setBackgroundDrawable(gradientDrawable)
    }

    override fun show() {
        //主要作用是焦点失能和焦点恢复，保证在弹出dialog时不会弹出虚拟按键且事件不会穿透。
        if (fullscreen && this.window != null) {
            this.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            this.window!!.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            super.show()
            this.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            super.show()
        }
    }

    fun show(str: String?) {
        if (str == null || str.isEmpty()) return
        YToast.show(context, str)
    }

    /**
     * 缩放动画起点和结束点，在屏幕上的相对位置,x，y为占比。
     */
    fun setAnimationLocation(animationX: Float, animationY: Float) {
        if (openAnimation) {
            yAnimation.setAnimationLocation(animationX, animationY)
        }
    }

    override fun dismiss() {
        //退出动画
        if (openAnimation) {
            view.startAnimation(yAnimation.getExitAnimation { super.dismiss() })
        } else {
            super.dismiss()
        }
    }

    companion object {
        //开发屏幕最小宽度
        protected var DevelopmentScreenWidthDp = 720f
    }
}