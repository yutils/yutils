package com.yujing.adapter;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

/**
 * 开启RecyclerView上下左右拖拽
 *
 * @author 余静 2018年11月30日12:14:38
 */
@SuppressWarnings("unused")
public class YSimpleItemTouchCallback extends ItemTouchHelper.Callback {
    private YBaseYRecyclerViewAdapter mAdapter;

    public YSimpleItemTouchCallback(YBaseYRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //s上下拖拽
        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; //左->右和右->左滑动
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int position = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        Collections.swap(mAdapter.getList(), position, to);
        mAdapter.notifyItemMoved(position, to);
        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount() - position);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (mAdapter.getList().size() == 0) {
            return;
        }
        mAdapter.getList().remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount() - position);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof YBaseYRecyclerViewAdapter.BaseViewHolder) {
                YBaseYRecyclerViewAdapter.BaseViewHolder holder = (YBaseYRecyclerViewAdapter.BaseViewHolder) viewHolder;
                holder.itemView.setBackgroundColor(0x80bcbcbc); //设置拖拽和侧滑时的背景色
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof YBaseYRecyclerViewAdapter.BaseViewHolder) {
            YBaseYRecyclerViewAdapter.BaseViewHolder holder = (YBaseYRecyclerViewAdapter.BaseViewHolder) viewHolder;
            holder.itemView.setBackgroundColor(0x80eeeeee); //背景色还原
        }
    }
}
