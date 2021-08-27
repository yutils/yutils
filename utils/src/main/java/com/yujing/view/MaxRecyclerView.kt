package com.yujing.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * 最大化的RecyclerView，嵌套于ScrollView之中使用,处理多个RecyclerView显示不全的问题
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