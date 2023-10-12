package com.yujing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * 自动滚动 RecyclerView
 *
 * @author yujing 2021年8月26日23:10:35
 */

/*
用法：
xml
<com.yujing.view.AutoPollRecyclerView
    android:id="@+id/rv"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>

创建adapter（原生adapter）：，也可以其他adapter，但是要设置getItemCount()为Integer.MAX_VALUE,获取data时候要注意数组越界
public class AutoPollAdapter<T> extends RecyclerView.Adapter<AutoPollAdapter.BaseViewHolder> {
    private final Context mContext;
    private final List<T> mData;

    public AutoPollAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mData = list;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_main_item_top, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        T data = mData.get(position % mData.size());
        holder.binding.tv.setText("当前第" + position + "项");
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        ActivityMainItemTopBinding binding;

        public BaseViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
activity中使用：
    //设置列表方向
    YSetRecyclerView.init(this, binding.rv, RecyclerView.HORIZONTAL)
    val list = listOf("a", "b", "c", "d", "e", "f")
    val adapter = AutoPollAdapter(this, list)
    binding.rv.adapter = adapter
    binding.rv.speed=1
    binding.rv.start()
 */
public class AutoPollRecyclerView extends RecyclerView {
    //每16毫秒执行一次，每秒60帧，每33毫秒执行一次，那么就是每秒30帧
    public static final long TIME_AUTO_POLL = 33;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false

    public AutoPollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;
        private int speed = 1;//速度

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
                recyclerView.scrollBy(speed, speed);
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
            }
        }
    }

    public int getSpeed() {
        return autoPollTask.getSpeed();
    }

    //设置速度
    public void setSpeed(int speed) {
        autoPollTask.setSpeed(speed);
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running) stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }
}