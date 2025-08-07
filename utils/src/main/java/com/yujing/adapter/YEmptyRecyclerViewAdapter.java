package com.yujing.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 空RecyclerViewAdapter显示“无数据”，主要给BaseYRecyclerViewAdapter类调用
 *
 * @author 余静 2018年11月30日12:13:46
 */
@Deprecated
public class YEmptyRecyclerViewAdapter extends RecyclerView.Adapter<YEmptyRecyclerViewAdapter.EmptyHolder> {
    private final Context context;
    private TextView tv;
    private final View emptyView;

    public YEmptyRecyclerViewAdapter(Context context) {
        super();
        this.context = context;
        emptyView = getEmptyView();
    }

    /**
     * 创建一个view 上面写着“无数据”
     *
     * @return View
     */
    private View getEmptyView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;//设置中心对其
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);//设置纵向布局
        //文字
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvParams.gravity = Gravity.CENTER;//TextView在父布局里面居中
        //实例化一个TextView
        tv = new TextView(context);
        tv.setLayoutParams(tvParams);
        tv.setGravity(Gravity.CENTER);//文字在TextView里面居中
        tv.setTextSize(25);
        tv.setTextColor(Color.parseColor("#999999"));
        tv.setText("无数据");
        linearLayout.addView(tv);
        return linearLayout;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyHolder holder, int position) {
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public YEmptyRecyclerViewAdapter.EmptyHolder onCreateViewHolder(ViewGroup viewGroup, int point) {
        return new EmptyHolder(emptyView);
    }

    //-----------------get----and-----set--------------------
    public CharSequence getItemEmptyText() {
        return tv.getText();
    }

    public void setItemEmptyText(CharSequence itemEmptyText) {
        tv.setText(itemEmptyText);
    }

    //-----------------class--------------------
    public static class EmptyHolder extends RecyclerView.ViewHolder {
        EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}