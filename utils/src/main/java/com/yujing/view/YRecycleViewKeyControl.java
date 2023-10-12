package com.yujing.view;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView滚动上下按键映射物理按键 2019年1月9日09:40:25
 */
/*用法
private static final int UP_KEY_CODE = 1;
private static final int DOWN_KEY_CODE = 2;
private static final int ENTER_KEY_CODE = 3;
//private static final int UP_KEY_CODE = KeyEvent.KEYCODE_J;
//private static final int DOWN_KEY_CODE = KeyEvent.KEYCODE_M;
//private static final int ENTER_KEY_CODE = KeyEvent.KEYCODE_PERIOD;
//private static final int DELETE_KEY_CODE = KeyEvent.KEYCODE_9;
//private static final int CANCEL_KEY_CODE = KeyEvent.KEYCODE_8;

//自定义按键映射
ZRecycleViewKeyControl.KeyDownListener keyDownListener = new ZRecycleViewKeyControl.KeyDownListener() {
    @Override
    public void itemObject(int keyCode, View view, int position) {
        //确定
        if (keyCode == ENTER_KEY_CODE) {}
        //删除
        if (keyCode == DELETE_KEY_CODE) { }
        //取消
        if (keyCode == CANCEL_KEY_CODE) {}
    }
};

//实例化
zRecycleViewKeyControl = new ZRecycleViewKeyControl(rlWeightList);
zRecycleViewKeyControl.addUpKeyCode(UP_KEY_CODE);
zRecycleViewKeyControl.addDownKeyCode(DOWN_KEY_CODE);
zRecycleViewKeyControl.addEnterKeyCode(ENTER_KEY_CODE);

//设置监听
zRecycleViewKeyControl.setKeyDownListener(keyDownListener);
//设置颜色
zRecycleViewKeyControl.setActiveBackground(Color.parseColor("#80FFFFFF"));
zRecycleViewKeyControl.setDefaultBackground(Color.TRANSPARENT);

//跳转到某行
zRecycleViewKeyControl.gotoLine(i);
//向上翻页
zRecycleViewKeyControl.doKey(UP_KEY_CODE);
//向下翻页
zRecycleViewKeyControl.doKey(DOWN_KEY_CODE);
//确定按键
zRecycleViewKeyControl.doKey(ENTER_KEY_CODE);

//绑定事件
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    zRecycleViewKeyControl.keyDown(keyCode, event);
}
 */
public class YRecycleViewKeyControl {
    //上键
    private final List<Integer> upKeyCodes = new ArrayList<>();
    //下键
    private final List<Integer> downKeyCodes = new ArrayList<>();
    //确定键
    private final List<Integer> enterKeyCodes = new ArrayList<>();
    //本次按键code
    private int keycode;
    //当前选中项
    private int selectedIndex = -1;
    //选中item颜色，-1代表不修改颜色
    private int activeBackground = -1;
    //未选中item颜色，-1代表不修改颜色
    private int defaultBackground = -1;
    //按键监听接口
    private KeyDownListener keyDownListener;
    //recyclerView
    private RecyclerView recyclerView;
    //ViewHolder
    private RecyclerView.ViewHolder toSelectHolder;
    //adapter
//    private RecyclerView.Adapter adapter;
    //滚动监听
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //如果是第一个行或者最后一行，滚动完毕后额外更新一次
            if (recyclerView.getAdapter() != null && selectedIndex == 0)
                doUpdateViewHolder(selectedIndex, recyclerView.getAdapter().getItemCount() - 1);
            if (recyclerView.getAdapter() != null && selectedIndex == recyclerView.getAdapter().getItemCount() - 1) {
                doUpdateViewHolder(selectedIndex, 0);
            }
        }
    };

    /**
     * 构造方法 必须传recyclerView
     *
     * @param recyclerView recyclerView
     */
    public YRecycleViewKeyControl(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(onScrollListener);
    }

    /**
     * 设置按键按下监听
     */
    public YRecycleViewKeyControl setKeyDownListener(KeyDownListener keyDownListener) {
        this.keyDownListener = keyDownListener;
        return this;
    }

    /**
     * 添加上键映射
     */
    public YRecycleViewKeyControl addUpKeyCode(int... upKeyCode) {
        for (int item : upKeyCode)
            if (!upKeyCodes.contains(item))
                upKeyCodes.add(item);
        return this;
    }

    /**
     * 添加下键映射
     */
    public YRecycleViewKeyControl addDownKeyCode(int... downKeyCode) {
        for (int item : downKeyCode)
            if (!downKeyCodes.contains(item))
                downKeyCodes.add(item);
        return this;
    }

    /**
     * 添加确定键映射
     */
    public YRecycleViewKeyControl addEnterKeyCode(int... enterKeyCode) {
        for (int item : enterKeyCode)
            if (!enterKeyCodes.contains(item))
                enterKeyCodes.add(item);
        return this;
    }

    /**
     * 清除上键映射
     */
    public YRecycleViewKeyControl clearDownKeyCode() {
        downKeyCodes.clear();
        return this;
    }

    /**
     * 清除下键映射
     */
    public YRecycleViewKeyControl clearUpKeyCode() {
        upKeyCodes.clear();
        return this;
    }

    /**
     * 清除下键映射
     */
    public YRecycleViewKeyControl clearEnterKeyCode() {
        enterKeyCodes.clear();
        return this;
    }

    /**
     * 在activity或者view中，keyDown方法调用此方法
     *
     * @param keyCode 按键Code
     * @param event   按键事件
     */
    public YRecycleViewKeyControl onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0)
            doKey(event.getKeyCode());
        return this;
    }

    /**
     * 触发keycode事件
     *
     * @param keyCode 当前key传入
     * @return myself
     */
    public YRecycleViewKeyControl doKey(int keyCode) {
        this.keycode = keyCode;
        if (upKeyCodes.contains(keycode))
            doKeyUp();
        else if (downKeyCodes.contains(keycode))
            doKeyDown();
        else if (enterKeyCodes.contains(keycode))
            doEnter();
        return this;
    }

    /**
     * 更新指定行（屏幕中可见行）的背景颜色，并且把上一次选中行的颜色设置为默认颜色
     *
     * @param toSelectIndex 行号
     */
    private boolean doUpdateViewHolder(int toSelectIndex, int oldSelectIndex) {
        toSelectHolder = recyclerView.findViewHolderForAdapterPosition(toSelectIndex);
        RecyclerView.ViewHolder selectHolder = recyclerView.findViewHolderForAdapterPosition(oldSelectIndex);
        if (toSelectHolder == null)
            return false;
        if (defaultBackground != -1 && selectHolder != null)
            selectHolder.itemView.setBackgroundColor(defaultBackground);
        if (defaultBackground != -1)
            toSelectHolder.itemView.setBackgroundColor(activeBackground);
        return true;
    }

    //获取当前位置
    public int getSelectedIndex() {
        return selectedIndex;
    }

    //跳转到位置
    public boolean gotoLine(int toSelectIndex) {
        toSelectHolder = recyclerView.findViewHolderForAdapterPosition(toSelectIndex);
        RecyclerView.ViewHolder selectHolder = recyclerView.findViewHolderForAdapterPosition(selectedIndex);
        if (toSelectHolder == null)
            return false;
        if (defaultBackground != -1 && selectHolder != null)
            selectHolder.itemView.setBackgroundColor(defaultBackground);
        if (defaultBackground != -1)
            toSelectHolder.itemView.setBackgroundColor(activeBackground);
        selectedIndex = toSelectIndex;
        return true;
    }


    /**
     * 上键触发事件，向上滚动一行更改背景颜色并且回调
     */
    private void doKeyUp() {
        int toSelectIndex = selectedIndex - 1;
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            //非顶部情况
            if (toSelectIndex >= 0) {
                recyclerView.smoothScrollToPosition(toSelectIndex);
                if (doUpdateViewHolder(toSelectIndex, selectedIndex)) {
                    selectedIndex = toSelectIndex;
                    if (keyDownListener != null)
                        keyDownListener.itemObject(keycode, toSelectHolder.itemView, selectedIndex);
                }
            } else {
                //顶部调到最后一行,selectedIndex需要先赋值再滚动
                int oldSelectedIndex = selectedIndex;
                selectedIndex = recyclerView.getAdapter().getItemCount() - 1;
                recyclerView.smoothScrollToPosition(selectedIndex);
                if (doUpdateViewHolder(selectedIndex, oldSelectedIndex)) {
                    if (keyDownListener != null)
                        keyDownListener.itemObject(keycode, toSelectHolder.itemView, selectedIndex);
                }
            }
        }
    }

    /**
     * 下键触发事件，向下滚动一行更改背景颜色并且回调
     */
    private void doKeyDown() {
        int toSelectIndex = selectedIndex + 1;
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            //非尾部情况
            if (toSelectIndex < recyclerView.getAdapter().getItemCount()) {
                recyclerView.smoothScrollToPosition(toSelectIndex);
                if (doUpdateViewHolder(toSelectIndex, selectedIndex)) {
                    selectedIndex = toSelectIndex;
                    if (keyDownListener != null)
                        keyDownListener.itemObject(keycode, toSelectHolder.itemView, selectedIndex);
                }
            } else {
                //尾部调到第一行,selectedIndex需要先赋值再滚动
                int oldSelectedIndex = selectedIndex;
                selectedIndex = 0;
                recyclerView.smoothScrollToPosition(selectedIndex);
                if (doUpdateViewHolder(selectedIndex, oldSelectedIndex)) {
                    if (keyDownListener != null)
                        keyDownListener.itemObject(keycode, toSelectHolder.itemView, selectedIndex);
                }
            }
        }
    }

    /**
     * 确定键触发事件，直接回调当前行号
     */
    private void doEnter() {
        if (toSelectHolder != null && keyDownListener != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > selectedIndex)
            keyDownListener.itemObject(keycode, toSelectHolder.itemView, selectedIndex);
    }

    /**
     * 获取选中行背景颜色
     */
    public int getActiveBackground() {
        return activeBackground;
    }

    /**
     * 设置选中行背景颜色
     */
    public YRecycleViewKeyControl setActiveBackground(@ColorInt int activeBackground) {
        this.activeBackground = activeBackground;
        return this;
    }

    /**
     * 获取默认背景颜色
     */
    public int getDefaultBackground() {
        return defaultBackground;
    }

    /**
     * 设置默认背景颜色
     *
     * @param defaultBackground 默认背景颜色
     */
    public YRecycleViewKeyControl setDefaultBackground(@ColorInt int defaultBackground) {
        this.defaultBackground = defaultBackground;
        return this;
    }


    /**
     * 按键接口
     */
    public interface KeyDownListener {
        /**
         * 按键项item
         *
         * @param keyCode  按键code，真实按键code
         * @param view     当前行view
         * @param position 行号
         */
        void itemObject(int keyCode, View view, int position);
    }
}