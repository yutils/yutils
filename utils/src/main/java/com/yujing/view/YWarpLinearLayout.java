package com.yujing.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 瀑布流布局
 *
 * @author yujing 2021年1月28日13:43:33
 */
/*用法
//xml
<com.yujing.view.YWarpLinearLayout
    android:id="@+id/wll"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center" />

//动态改变
binding.wll.vertical_Space = 20F //垂直间距
binding.wll.horizontal_Space = 20F //水平间距
binding.wll.gravity = 2 //对齐方式 right 0，left 1，center 2
//binding.wll.isFull = true //横屏拉伸
binding.wll.requestLayout() //刷新view内容
binding.wll.invalidate()    //刷新view大小
 */
public class YWarpLinearLayout extends ViewGroup {
    private Type mType;
    private List<WarpLine> mWarpLineGroup;

    public YWarpLinearLayout(Context context) {
        this(context, null);
        mType = new Type(context, null);
    }

    public YWarpLinearLayout(Context context, AttributeSet attrs) {
        //this(context, attrs, R.style.WarpLinearLayoutDefault);
        super(context, attrs);
        mType = new Type(context, attrs);
    }

    public YWarpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mType = new Type(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int with = 0;
        int height = 0;
        int childCount = getChildCount();
        /**
         * 在调用childView。getMeasre之前必须先调用该行代码，用于对子View大小的测量
         */
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
         * 计算宽度
         */
        switch (withMode) {
            case MeasureSpec.EXACTLY:
                //noinspection DuplicateBranchesInSwitch
                with = withSize;
                break;
            case MeasureSpec.AT_MOST:
                for (int i = 0; i < childCount; i++) {
                    if (i != 0) {
                        with += mType.horizontal_Space;
                    }
                    with += getChildAt(i).getMeasuredWidth();
                }
                with += getPaddingLeft() + getPaddingRight();
                with = Math.min(with, withSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                for (int i = 0; i < childCount; i++) {
                    if (i != 0) {
                        with += mType.horizontal_Space;
                    }
                    with += getChildAt(i).getMeasuredWidth();
                }
                with += getPaddingLeft() + getPaddingRight();
                break;
            default:
                with = withSize;
                break;

        }
        /**
         * 根据计算出的宽度，计算出所需要的行数
         */
        WarpLine warpLine = new WarpLine();
        /**
         * 不能够在定义属性时初始化，因为onMeasure方法会多次调用
         */
        mWarpLineGroup = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            if (warpLine.lineWidth + getChildAt(i).getMeasuredWidth() + mType.horizontal_Space > with) {
                if (warpLine.lineView.size() == 0) {
                    warpLine.addView(getChildAt(i));
                    mWarpLineGroup.add(warpLine);
                    warpLine = new WarpLine();
                } else {
                    mWarpLineGroup.add(warpLine);
                    warpLine = new WarpLine();
                    warpLine.addView(getChildAt(i));
                }
            } else {
                warpLine.addView(getChildAt(i));
            }
        }
        /**
         * 添加最后一行
         */
        if (warpLine.lineView.size() > 0 && !mWarpLineGroup.contains(warpLine)) {
            mWarpLineGroup.add(warpLine);
        }
        /**
         * 计算宽度
         */
        height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mWarpLineGroup.size(); i++) {
            if (i != 0) {
                height += mType.vertical_Space;
            }
            height += mWarpLineGroup.get(i).height;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(height, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }
        setMeasuredDimension(with, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        t = getPaddingTop();
        for (int i = 0; i < mWarpLineGroup.size(); i++) {
            int left = getPaddingLeft();
            WarpLine warpLine = mWarpLineGroup.get(i);
            int lastWidth = getMeasuredWidth() - warpLine.lineWidth;
            for (int j = 0; j < warpLine.lineView.size(); j++) {
                View view = warpLine.lineView.get(j);
                if (isFull()) {//需要充满当前行时
                    view.layout(left, t, left + view.getMeasuredWidth() + lastWidth / warpLine.lineView.size(), t + view.getMeasuredHeight());
                    left += view.getMeasuredWidth() + mType.horizontal_Space + lastWidth / warpLine.lineView.size();
                } else {
                    switch (getGravity()) {
                        case 0://右对齐
                            view.layout(left + lastWidth, t, left + lastWidth + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                        case 2://居中对齐
                            view.layout(left + lastWidth / 2, t, left + lastWidth / 2 + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                        default://左对齐
                            view.layout(left, t, left + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                    }
                    left += view.getMeasuredWidth() + mType.horizontal_Space;
                }
            }
            t += warpLine.height + mType.vertical_Space;
        }
    }

    /**
     * 用于存放一行子View
     */
    private final class WarpLine {
        private List<View> lineView = new ArrayList<View>();
        /**
         * 当前行中所需要占用的宽度
         */
        private int lineWidth = getPaddingLeft() + getPaddingRight();
        /**
         * 该行View中所需要占用的最大高度
         */
        private int height = 0;

        private void addView(View view) {
            if (lineView.size() != 0) {
                lineWidth += mType.horizontal_Space;
            }
            height = Math.max(height, view.getMeasuredHeight());
            lineWidth += view.getMeasuredWidth();
            lineView.add(view);
        }
    }

    /**
     * 对样式的初始化
     */
    private final static class Type {
        /*
         *对齐方式 right 0，left 1，center 2
         */
        private int gravity;
        /**
         * 水平间距,单位px
         */
        private float horizontal_Space;
        /**
         * 垂直间距,单位px
         */
        private float vertical_Space;
        /**
         * 是否自动填满
         */
        private boolean isFull;

        Type(Context context, AttributeSet attrs) {
            gravity = 1;
            horizontal_Space = 0;
            vertical_Space = 0;
            isFull = false;
            //if (attrs == null) {
            //    return;
            //}
            //TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WarpLinearLayout);
            //grivate = typedArray.getInt(R.styleable.WarpLinearLayout_gravity, grivate);
            //horizontal_Space = typedArray.getDimension(R.styleable.WarpLinearLayout_horizontal_Space, horizontal_Space);
            //vertical_Space = typedArray.getDimension(R.styleable.WarpLinearLayout_vertical_Space, vertical_Space);
            //isFull = typedArray.getBoolean(R.styleable.WarpLinearLayout_isFull, isFull);
        }
    }

    public int getGravity() {
        return mType.gravity;
    }

    public float getHorizontal_Space() {
        return mType.horizontal_Space;
    }

    public float getVertical_Space() {
        return mType.vertical_Space;
    }

    public boolean isFull() {
        return mType.isFull;
    }

    public void setFull(boolean isFull) {
        mType.isFull = isFull;
    }

    public void setGravity(int gravity) {
        mType.gravity = gravity;
    }

    public void setHorizontal_Space(float horizontal_Space) {
        mType.horizontal_Space = horizontal_Space;
    }

    public void setVertical_Space(float vertical_Space) {
        mType.vertical_Space = vertical_Space;
    }
}