package com.yujing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.yujing.contract.YListener;

import java.util.List;
import java.util.Objects;

/**
 * RecyclerView 通用适配器
 *
 * @author 余静 2018年11月30日12:13:23
 */
/*用法举例:
//java
import androidx.databinding.DataBindingUtil;
import com.yujing.adapter.YBaseYRecyclerViewAdapter;
public class MAdapter extends YBaseYRecyclerViewAdapter {
    public MAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public int setLayout() {
        return  R.layout.activity_page1;
    }
    @Override
    public BaseViewHolder setViewHolder(View itemView) {
        return new BaseViewHolder(itemView) {
            ActivityPage1Binding binding;
            @Override
            public void findView(View convertView) {
                binding= DataBindingUtil.bind(convertView);
            }
            @Override
            public void setData(int position, Object obj, List adapterList, YBaseYRecyclerViewAdapter adapter) {

            }
        };
    }
}


//kotlin
import androidx.databinding.DataBindingUtil
import com.yujing.adapter.YBaseYRecyclerViewAdapter
class MyAccountAdapter<T>(context: Context, list: List<T>) : YBaseYRecyclerViewAdapter<T>(context, list) {
    override fun setLayout(): Int {
        return R.layout.activity_my_account_item
    }

    var isSelect: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun setViewHolder(itemView: View?): BaseViewHolder {
        return object : BaseViewHolder(itemView) {
            lateinit var binding: ActivityMyAccountItemBinding
            override fun findView(view: View) {
                binding = DataBindingUtil.bind(view)!!
            }
            override fun setData(position: Int, obj: Any?, adapterList: MutableList<Any?>?, adapter: YBaseYRecyclerViewAdapter<*>?) {

                //选中行变色
                binding.ll.setBackgroundColor( if (position == isSelect) Color.parseColor("#3000b2EE") else Color.parseColor("#00000000"))
            }
        }
    }
}

//原生用法 java
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
        T data = mData.get(position);
        holder.binding.tv.setText("当前第" + position + "项");
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        ActivityMainItemTopBinding binding;

        public BaseViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}

//原生用法 kotlin  基于DataBinding
class MyViewHolder(var binding: ActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {}
class CarAdapter<T>(var data: List<T>?) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.activity_list_item, parent, false))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data?.get(position) as User
        holder.binding.iv.setOnClickListener { YToast.show("点击：" + item.name) }

        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }
    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
}
 */
@SuppressWarnings({"unused", "NullableProblems"})
@Deprecated
public abstract class YBaseYRecyclerViewAdapter<T> extends RecyclerView.Adapter<YBaseYRecyclerViewAdapter.BaseViewHolder> {
    private List<T> list;//数据
    private Context context;//context
    private Boolean recyclable = null;// 是否是可回收的，重复利用item
    private OnItemClickListener onItemClickListener = null;//单击监听
    private OnItemLongClickListener onItemLongClickListener = null;//长按监听
    protected RecyclerView recyclerView;//recyclerView
    private boolean isShowEmpty = true;//当没有数据是否显示“emptyRecyclerViewAdapter”
    private YEmptyRecyclerViewAdapter emptyRecyclerViewAdapter;//没有数据时默认显示的Adapter
    private YListener onTopListener = null; //到顶部监听，到顶部后继续滑动，不会触发
    private YListener onBottomListener = null; //到底部监听，到底部后继续滑动，不会触发
    private YListener onScrollToTopListener = null; //滑动到顶部监听，到顶部后继续滑动，会触发
    private YListener onScrollToBottomListener = null; //滑动到底部监听，到底部后继续滑动，会触发

    public YBaseYRecyclerViewAdapter(Context context, List<T> list) {
        super();
        this.context = context;
        this.list = list;
        emptyRecyclerViewAdapter = new YEmptyRecyclerViewAdapter(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        showItemEmpty();
        //滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //newState分 0,1,2三个状态,2是滚动状态,0是停止
                if (newState == 0) {
                    if (!recyclerView.canScrollVertically(-1))
                        if (onScrollToTopListener != null) onScrollToTopListener.value();
                    if (!recyclerView.canScrollVertically(1))
                        if (onScrollToBottomListener != null) onScrollToBottomListener.value();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1))
                    if (onTopListener != null) onTopListener.value();
                if (!recyclerView.canScrollVertically(1))
                    if (onBottomListener != null) onBottomListener.value();
            }
        });
    }

    public void showItemEmpty() {
        if (isShowEmpty) {
            if (list.size() == 0) {
                if (!Objects.requireNonNull(recyclerView.getAdapter()).equals(emptyRecyclerViewAdapter))
                    recyclerView.setAdapter(emptyRecyclerViewAdapter);
            } else {
                if (!Objects.requireNonNull(recyclerView.getAdapter()).equals(this))
                    recyclerView.setAdapter(this);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (this.list != null) ? this.list.size() : 0;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            showItemEmpty();
            notifyDataSetChanged();
        }
    }

    public void removeItemAnimation(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            showItemEmpty();
            notifyItemRemoved(position);
            if (position != list.size()) {
                notifyItemRangeChanged(position, list.size() - position);//通知数据与界面重新绑定
            }
        }
    }

    public void addItem(int position, T t) {
        list.add(position, t);
        showItemEmpty();
        notifyDataSetChanged();
    }

    public void addItemAnimation(int position, T t) {
        list.add(position, t);
        showItemEmpty();
        notifyItemInserted(position);//通知演示插入动画
        if (position != list.size()) {
            notifyItemRangeChanged(position, list.size() - position);//通知数据与界面重新绑定
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
        showItemEmpty();
        notifyDataSetChanged();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int point) {
        View itemView = LayoutInflater.from(getContext()).inflate(setLayout(), viewGroup, false);
        //回调给Holder
        BaseViewHolder holder = setViewHolder(itemView);
        if (recyclable != null) holder.setIsRecyclable(recyclable);
        holder.setContext(getContext());
        holder.findView(holder.itemView);
        //设置监听
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, (Integer) v.getTag());
        });
        //长按监听
        itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null)
                onItemLongClickListener.onItemClick(v, (Integer) v.getTag());
            return false;
        });
        return holder;
    }

    // 会反复调用，初始化时调用屏幕个数加1，滑动时候回重复调用此方法
    @SuppressWarnings("NullableProblems")
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position, list.get(position), list, this);
        holder.itemView.setTag(position);
    }

    //-----------------------abstract-----------------------
    public abstract int setLayout();

    public abstract BaseViewHolder setViewHolder(View itemView);

    //-----------------------get-----AND-----set-----------------------
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Boolean isRecyclable() {
        return recyclable;
    }

    public boolean isShowEmpty() {
        return isShowEmpty;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CharSequence getItemEmptyText() {
        return emptyRecyclerViewAdapter.getItemEmptyText();
    }

    public void setItemEmptyText(CharSequence itemEmptyText) {
        emptyRecyclerViewAdapter.setItemEmptyText(itemEmptyText);
    }

    public void setShowEmpty(boolean showEmpty) {
        isShowEmpty = showEmpty;
    }

    public void setRecyclable(Boolean recyclable) {
        this.recyclable = recyclable;
    }

    public YListener getOnTopListener() {
        return onTopListener;
    }

    public void setOnTopListener(YListener onTopListener) {
        this.onTopListener = onTopListener;
    }

    public YListener getOnBottomListener() {
        return onBottomListener;
    }

    public void setOnBottomListener(YListener onBottomListener) {
        this.onBottomListener = onBottomListener;
    }

    public YListener getOnScrollToTopListener() {
        return onScrollToTopListener;
    }

    public void setOnScrollToTopListener(YListener onScrollToTopListener) {
        this.onScrollToTopListener = onScrollToTopListener;
    }

    public YListener getOnScrollToBottomListener() {
        return onScrollToBottomListener;
    }

    public void setOnScrollToBottomListener(YListener onScrollToBottomListener) {
        this.onScrollToBottomListener = onScrollToBottomListener;
    }

    //--------------------interface-------------------------
    public interface OnItemLongClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //--------------------class-------------------------
    @Deprecated
    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        @SuppressWarnings("EmptyMethod")
        public abstract void findView(View convertView);

        @SuppressWarnings({"rawtypes", "EmptyMethod"})
        public abstract void setData(int position, Object obj, List adapterList, YBaseYRecyclerViewAdapter adapter);

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }
}
