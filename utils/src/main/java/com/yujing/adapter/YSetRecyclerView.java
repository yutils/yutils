package com.yujing.adapter;

import android.content.Context;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yujing.utils.YApp;

/**
 * 快速设置RecyclerView
 *
 * @author 余静 2018年11月30日12:14:30
 */
/*
用法：

//设置recyclerView为垂直滚动布局
YSetRecyclerView.initVertical(binding.recyclerView)
//设置recyclerView为水平滚动布局
YSetRecyclerView.initHorizontal(binding.recyclerView)
//设置多行多列布局，如：垂直滚动，每行3个item
YSetRecyclerView.init(this,binding.recyclerView,RecyclerView.VERTICAL,3)
//设置多行多列布局，如：水平滚动，每行4个item
YSetRecyclerView.init(this,binding.recyclerView,RecyclerView.HORIZONTAL,3)
 */
@SuppressWarnings("unused")
@Deprecated
public class YSetRecyclerView {
    /**
     * 默认纵向布局
     *
     * @param recyclerView recyclerView
     */
    public static LinearLayoutManager init(RecyclerView recyclerView) {
        return initVertical(recyclerView);
    }

    /**
     * 纵向布局
     *
     * @param recyclerView recyclerView
     */
    public static LinearLayoutManager initVertical(RecyclerView recyclerView) {
        return init(YApp.get(), recyclerView, RecyclerView.VERTICAL);
    }

    /**
     * 横向布局
     *
     * @param recyclerView recyclerView
     */
    public static LinearLayoutManager initHorizontal(RecyclerView recyclerView) {
        return init(YApp.get(), recyclerView, RecyclerView.HORIZONTAL);
    }

    /**
     * 默认纵向布局
     *
     * @param context      context
     * @param recyclerView recyclerView
     */
    public static LinearLayoutManager init(Context context, RecyclerView recyclerView) {
        return init(context, recyclerView, RecyclerView.VERTICAL);
    }

    /**
     * 自定义横向或纵向布局
     *
     * @param context      context
     * @param recyclerView recyclerView
     * @param Orientation  Orientation，如：OrientationHelper.VERTICAL
     */
    public static LinearLayoutManager init(Context context, RecyclerView recyclerView, int Orientation) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setOrientation(Orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    // 自定义多行多列布局
    public static GridLayoutManager init(Context context, RecyclerView recyclerView, int Orientation, int items) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, items);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setOrientation(Orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    // 默认纵向布局
    public static YFullyLinearLayoutManager init(Context context, RecyclerView recyclerView, boolean isScrollView) {
        return init(context, recyclerView, RecyclerView.VERTICAL, isScrollView);
    }

    // 自定义横向或纵向布局
    public static YFullyLinearLayoutManager init(Context context, RecyclerView recyclerView, int Orientation, boolean isScrollView) {
        YFullyLinearLayoutManager layoutManager = new YFullyLinearLayoutManager(context);
        layoutManager.setSmoothScrollbarEnabled(isScrollView);
        layoutManager.setOrientation(Orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    // 自定义多行多列布局
    public static YFullyGridLayoutManager init(Context context, RecyclerView recyclerView, int Orientation, int items, boolean isScrollView) {
        YFullyGridLayoutManager layoutManager = new YFullyGridLayoutManager(context, items);
        layoutManager.setSmoothScrollbarEnabled(isScrollView);
        layoutManager.setOrientation(Orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static void setOther(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置增加或删除条目的动画
    }

    /**
     * 打开上下拖拽，左右删除
     *
     * @param recyclerView recyclerView
     * @param adapter      adapter
     */
    public static void openItemTouch(RecyclerView recyclerView, YBaseYRecyclerViewAdapter adapter) {
        ItemTouchHelper helper = new ItemTouchHelper(new YSimpleItemTouchCallback(adapter));
        helper.attachToRecyclerView(recyclerView);
    }
}