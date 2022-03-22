package com.yujing.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.view.View

/**
 * View的一些基本操作
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

}