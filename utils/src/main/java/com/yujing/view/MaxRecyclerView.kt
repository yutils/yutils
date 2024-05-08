package com.yujing.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * 最大化的RecyclerView，嵌套于ScrollView之中使用,处理多个RecyclerView显示不全的问题
 */
/*
1. （非必要）如果要求 ScrollView 内全部view高度都最大化 加入属性
android:fillViewport="true"

2.setAdapter之前加入，增加滑动惯性
binding.rv.setHasFixedSize(true)
binding.rv.isNestedScrollingEnabled = false


3.（非必要）如果ScrollView嵌套RecyclerView后，页面不会从页面顶部开始显示，会从RecyclerView第一个Item的位置开始显示
解决：在Xml页面顶部位置的布局控件中加入两句代码:
android:focusable="true"
android:focusableInTouchMode="true"
 */
class MaxRecyclerView : RecyclerView {
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?) : super(context!!)

    /**
     * 设置不滚动
     */
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}