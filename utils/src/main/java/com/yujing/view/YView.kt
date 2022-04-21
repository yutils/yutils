package com.yujing.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

/**
 * View的一些基本操作
 */
/*
view 缩放用法

var oldViewSize: MutableList<MutableMap<String, Any>> = mutableListOf()

@YBus("页面缩小")
fun scale() {
    oldViewSize = viewScale(binding.root, 0.6f)
}

@YBus("页面还原")
fun reduction() {
    viewReduction(binding.root, oldViewSize)
}

 */
object YView {
    /**
     * 获取在整个屏幕内的绝对坐标，含statusBar
     */
    fun getViewLocationOnScreen(view: View): Point {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return Point(location[0], location[1])
    }

    /**
     * 获取在当前窗口内的绝对坐标，含toolBar
     */
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
    fun viewScale(
        view: View, scale: Float, needWidth: Boolean = true, needHeight: Boolean = true,
        needViewGroup: Boolean = false, ignores: Array<View> = arrayOf(),
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
}