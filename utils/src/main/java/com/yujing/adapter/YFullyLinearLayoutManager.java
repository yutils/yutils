package com.yujing.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 解决RecyclerView在ScrollView中的高度不正常问题
 * @author yujing 2018年11月30日12:14:26
 */
@SuppressWarnings("unused")
class YFullyLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = YFullyLinearLayoutManager.class.getSimpleName();

    public YFullyLinearLayoutManager(Context context) {
        super(context);
    }

    public YFullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        Log.i(TAG, "onMeasure called. \nwidthMode " + widthMode + " \nheightMode " + heightSpec + " \nwidthSize " + widthSize + " \nheightSize " + heightSize + " \ngetItemCount() " + getItemCount());

        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);

            if (getOrientation() == HORIZONTAL) {
                width = width + mMeasuredDimension[0];
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                height = height + mMeasuredDimension[1];
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        if (position < getItemCount()) {
            try {
                View view = recycler.getViewForPosition(0);// fix动态添加时报IndexOutOfBoundsException
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
                view.measure(childWidthSpec, childHeightSpec);
                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}