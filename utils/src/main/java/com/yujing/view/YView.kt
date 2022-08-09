package com.yujing.view

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi

/**
 * View的一些基本操作
 */
/*
view 缩放用法:
//记录缩放比例
var oldViewSize: MutableList<MutableMap<String, Any>> = mutableListOf()
@YBus("页面缩小")
fun scale() {
    oldViewSize = YView.viewScale(binding.root, 0.6f)
    //如果有view没生效，刷新view
    YView.refreshAllView(binding.root)
}
@YBus("页面还原")
fun reduction() {
    YView.viewReduction(binding.root, oldViewSize)
    //如果有view没生效，刷新view
    YView.refreshAllView(binding.root)
}
 */
object YView {
    /**
     * 获取在整个屏幕内的绝对坐标，含statusBar
     */
    @JvmStatic
    fun getViewLocationOnScreen(view: View): Point {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Point(location[0], location[1])
    }

    /**
     * 获取在当前窗口内的绝对坐标，含toolBar
     */
    @JvmStatic
    fun getViewLocationInWindow(view: View): Point {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return Point(location[0], location[1])
    }

    /**
     * View转bitmap
     *
     * @param v View
     * @return Bitmap
     */
    @JvmStatic
    fun toBitmap(v: View): Bitmap? {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        val bgDrawable = v.background
        if (bgDrawable != null) bgDrawable.draw(c) else c.drawColor(Color.WHITE)
        v.draw(c)
        return b
    }

    /**
     * 缩放，先记录原始大小，然后设置缩放比例
     * @param view view
     * @param scale 缩放比例
     * @param needWidth 是否缩放宽度
     * @param needHeight 是否缩放高度
     * @param needViewGroup 是否缩放ViewGroup自身
     * @param ignores 忽略的view
     * @param allViewSize 全部view的大小
     */
    @JvmStatic
    fun viewScale(
        view: View, scale: Float, needWidth: Boolean = true, needHeight: Boolean = true,
        needViewGroup: Boolean = false, ignores: MutableList<View> = mutableListOf(),
        allViewSize: MutableList<MutableMap<String, Any>> = mutableListOf()
    ): MutableList<MutableMap<String, Any>> {
        for (i in ignores.indices) if (view == ignores[i]) return allViewSize
        val id = view.javaClass.name + "@" + Integer.toHexString(view.hashCode())
        val map: MutableMap<String, Any> = HashMap()
        //获取对象的唯一表示
        when (view) {
            is ViewGroup -> {
                if (needViewGroup) {
                    map["id"] = id
                    map["width"] = view.width
                    map["height"] = view.height
                    allViewSize.add(map)
                    if (needWidth) view.layoutParams.width = (view.width * scale).toInt()
                    if (needHeight) view.layoutParams.height = (view.height * scale).toInt()
                }
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    viewScale(child, scale, needWidth, needHeight, needViewGroup, ignores, allViewSize)
                }
            }
            is ImageView -> {
                map["id"] = id
                map["width"] = view.width
                map["height"] = view.height
                allViewSize.add(map)
                if (needWidth) view.layoutParams.width = (view.width * scale).toInt()
                if (needHeight) view.layoutParams.height = (view.height * scale).toInt()
            }
            is Button -> {
                map["id"] = id
                map["textSize"] = view.textSize
                map["width"] = view.width
                map["height"] = view.height
                allViewSize.add(map)
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.textSize * scale)
                if (needWidth) view.layoutParams.width = (view.width * scale).toInt()
                if (needHeight) view.layoutParams.height = (view.height * scale).toInt()
            }
            is EditText -> {
                map["id"] = id
                map["textSize"] = view.textSize
                allViewSize.add(map)
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.textSize * scale)
            }
            is TextView -> {
                map["id"] = id
                map["textSize"] = view.textSize
                allViewSize.add(map)
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.textSize * scale)
            }
            else -> {
                map["id"] = id
                map["width"] = view.width
                map["height"] = view.height
                allViewSize.add(map)
                if (needWidth) view.layoutParams.width = (view.width * scale).toInt()
                if (needHeight) view.layoutParams.height = (view.height * scale).toInt()
            }
        }
        return allViewSize
    }

    /**
     * 还原view缩放，通过allSize中记录的原始大小
     */
    @JvmStatic
    fun viewReduction(view: View, allViewSize: MutableList<MutableMap<String, Any>>?) {
        if (allViewSize == null) return
        if (allViewSize.isEmpty()) return
        val id = view.javaClass.name + "@" + Integer.toHexString(view.hashCode())
        var map: MutableMap<String, Any>? = null
        for (item in allViewSize) {
            if (item["id"] == id) {
                map = item
                break
            }
        }
        when (view) {
            is ViewGroup -> {
                map?.let {
                    val width = map["width"].toString().toInt()
                    val height = map["height"].toString().toInt()
                    view.layoutParams.width = width
                    view.layoutParams.height = height
                }
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    viewReduction(child, allViewSize)
                }
            }
            is ImageView -> {
                map?.let {
                    val width = map["width"].toString().toInt()
                    val height = map["height"].toString().toInt()
                    view.layoutParams.width = width
                    view.layoutParams.height = height
                }
            }
            is Button -> {
                map?.let {
                    val textSize = map["textSize"].toString().toFloat()
                    val width = map["width"].toString().toInt()
                    val height = map["height"].toString().toInt()
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    view.layoutParams.width = width
                    view.layoutParams.height = height
                }
            }
            is EditText -> {
                map?.let {
                    val textSize = map["textSize"].toString().toFloat()
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
            }
            is TextView -> {
                map?.let {
                    val textSize = map["textSize"].toString().toFloat()
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
            }
            else -> {
                map?.let {
                    val width = map["width"].toString().toInt()
                    val height = map["height"].toString().toInt()
                    view.layoutParams.width = width
                    view.layoutParams.height = height
                }
            }
        }
    }

    /**
     * 刷新全部view
     */
    @JvmStatic
    fun refreshAllView(root: View) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) refreshAllView(root.getChildAt(i))
        } else {
            root.requestLayout()
        }
    }


    /**
     * 设置View按下颜色，支持 ImageView ，TextView ，Button , ViewGroup
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setPressColor(view: View, @ColorInt colorPress: Int, @ColorInt colorUp: Int) {
        view.isClickable = true
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setColor(v, colorPress)
            } else if (event.action == MotionEvent.ACTION_UP) {
                setColor(v, colorUp)
            }
            return@setOnTouchListener false
        }
    }

    /**
     * 设置View颜色，支持 ImageView ，TextView ，Button , ViewGroup
     */
    fun setColor(v: View, @ColorInt color: Int) {
        when (v) {
            is ImageView -> v.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            is TextView -> v.setTextColor(color)
            is Button -> v.setTextColor(color)
            is ViewGroup -> {
                for (i in 0 until v.childCount) {
                    val child = v.getChildAt(i)
                    setColor(child, color)
                }
            }
        }
    }

    /**
     * 设置View按下背景颜色
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setPressBackgroundColor(view: View, @ColorInt colorPress: Int, @ColorInt colorUp: Int, @ColorInt colorFocused: Int = Color.TRANSPARENT) {
        view.isClickable = true
        val focusedDrawableOk = createGradientDrawable(colorFocused, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        val pressedDrawableOk = createGradientDrawable(colorPress, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        val normalDrawableOk = createGradientDrawable(colorUp, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        view.background = createStateListDrawable(focusedDrawableOk, pressedDrawableOk, normalDrawableOk)
    }

    /**
     * 设置按钮ButtonBackgroundTint
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setButtonBackgroundTint(view: Button, @ColorInt colorPress: Int = Color.parseColor("#51A691").xor(Color.parseColor("#60000000")), @ColorInt colorUp: Int = Color.parseColor("#51A691")) {
        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
            intArrayOf(colorPress, colorUp)
        )
        view.backgroundTintList = colorStateList
    }


    /**
     * 创建状态Drawable
     *
     * @param focusedDrawable 选中时Drawable
     * @param pressedDrawable 按下时Drawable
     * @param normalDrawable 抬起时Drawable
     * @return
     */
    /* 用法：
        val focusedDrawable = YView.createGradientDrawable(Color.RED, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        val pressedDrawable = YView.createGradientDrawable(Color.RED, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        val normalDrawable = YView.createGradientDrawable(Color.GREEN, 0, Color.WHITE, 0F, 0F, 0F, 0F)
        binding.btOk.background = YView.createStateListDrawable(focusedDrawable, pressedDrawable, normalDrawable)
     */
    @SuppressLint("ClickableViewAccessibility")
    fun createStateListDrawable(focusedDrawable: Drawable, pressedDrawable: Drawable, normalDrawable: Drawable): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_focused), focusedDrawable)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_pressed), normalDrawable)
        return stateListDrawable
    }

    /**
     * 创建渐变圆角 Drawable
     *
     * @param fillColor 填充颜色
     * @param width 边框宽度
     * @param strokeColor 边框颜色
     * @param topLeftRadius 圆角弧度
     * @param topRightRadius 圆角弧度
     * @param bottomRightRadius 圆角弧度
     * @param bottomLeftRadius 圆角弧度
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    fun createGradientDrawable(fillColor: Int = Color.WHITE, width: Int = 0, strokeColor: Int = Color.WHITE, topLeftRadius: Float = 0F, topRightRadius: Float = 0F, bottomRightRadius: Float = 0F, bottomLeftRadius: Float = 0F): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        //gradientDrawable.cornerRadius = radius.toFloat() //圆角
        gradientDrawable.setColor(fillColor)//填充颜色
        gradientDrawable.setStroke(width, strokeColor)//边框宽度和颜色
        //四角圆角
        gradientDrawable.cornerRadii = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)
        return gradientDrawable
    }
}