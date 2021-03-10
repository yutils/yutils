package com.yujing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * 可以调整到指定的纵横比的TextureView
 * YCamera用到
 */
public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置此视图的纵横比。视图的大小将根据比率进行计算
     * 根据参数计算。注意，参数的实际大小并不重要
     * 比如setAspectRatio（2，3）和setAspectRatio（4，6）会产生相同的结果。
     *
     * @param width  相对水平尺寸
     * @param height 相对垂直尺寸
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("大小不能为负数");
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
