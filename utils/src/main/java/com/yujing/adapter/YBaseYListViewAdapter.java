package com.yujing.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * listView 通用适配器
 *
 * @author 余静 2018年11月30日12:13:23
 */
/*
用法举例
class MyAccountAdapter(context: Context?, list: List<*>?) :
    YBaseYListViewAdapter<Any?>(context, list) {
    override fun setLayout(): Int {
        return R.layout.activity_my_account_item
    }

    override fun setViewHolder(): BaseViewHolder {
        return object : BaseViewHolder(){
            var binding: ActivityMyAccountItemBinding? = null
            override fun findView(convertView: View?) {
                binding = DataBindingUtil.bind(view)
            }
            override fun setData(position: Int, obj: Any?, adapterList: MutableList<Any?>?, adapter: YBaseYListViewAdapter<*>?) {

            }
        }
    }
}

原生用法：
public class MyListAdapter extends BaseAdapter {
    private List<Data> list;
    private LayoutInflater inflater;
    public  MyListAdapter (List<Data> list, Context context) {
           this.list = list;
           this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.listview_item,null);
        Data data = (Data) getItem(position);

        TextView tvName = view.findViewById(R.id.text_name);

        //返回含有数据的view
        return view;
    }
}
 */
@SuppressWarnings("unused")
@Deprecated
public abstract class YBaseYListViewAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context context;
    protected boolean recyclable = true;// 是否是可回收的，重复利用item
    private boolean itemIsEmpty;//传进来的list的长度是否为0
    private CharSequence itemEmptyText = "无数据";//如果list的长度为0显示什么内容

    public YBaseYListViewAdapter(Context context, List<T> list) {
        super();
        this.context = context;
        this.list = list;
        if (list == null)
            list = new ArrayList<>();
        itemIsEmpty = (list.size() == 0);
        if (list.size() == 0)
            this.list.add(null);
    }

    @Override
    public int getCount() {
        if (itemIsEmpty) return 0;
        return (this.list != null) ? this.list.size() : 0;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            itemIsEmpty = (list.size() == 0);
            if (itemIsEmpty) this.list.add(null);
            notifyDataSetChanged();
        }
    }

    public void addItem(int position, T t) {
        if (itemIsEmpty) list = new ArrayList<>();
        list.add(position, t);
        itemIsEmpty = (list.size() == 0);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getList() {
        return itemIsEmpty ? new ArrayList<>() : list;
    }

    public void setList(List<T> list) {
        this.list = list;
        if (this.list == null) this.list = new ArrayList<>();
        itemIsEmpty = (this.list.size() == 0);
        if (this.list.size() == 0) {
            this.list.add(null);
        }
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public void setRecyclable(boolean recyclable) {
        this.recyclable = recyclable;
    }

    public CharSequence getItemEmptyText() {
        return itemEmptyText;
    }

    public void setItemEmptyText(CharSequence itemEmptyText) {
        this.itemEmptyText = itemEmptyText;
    }

    private View getEmptyView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;//设置中心对其
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.VERTICAL);//设置纵向布局
        //文字
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvParams.gravity = Gravity.CENTER;//TextView在父布局里面居中
        //实例化一个TextView
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(tvParams);
        tv.setGravity(Gravity.CENTER);//文字在TextView里面居中
        tv.setTextSize(25);
        tv.setTextColor(Color.parseColor("#999999"));
        tv.setText(itemEmptyText);
        linearLayout.addView(tv);
        return linearLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (itemIsEmpty && itemEmptyText != null) {
            return getEmptyView();
        }
        BaseViewHolder viewHolder;
        if (recyclable) {
            if (convertView == null) {
                viewHolder = setViewHolder();
                viewHolder.setContext(context);
                convertView = LayoutInflater.from(context).inflate(setLayout(), null);
                viewHolder.findView(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BaseViewHolder) convertView.getTag();
            }
        } else {
            viewHolder = setViewHolder();
            viewHolder.setContext(context);
            convertView = LayoutInflater.from(context).inflate(setLayout(), null);
            viewHolder.findView(convertView);
        }
        viewHolder.setData(position, list.get(position), list, this);
        return convertView;
    }

    public abstract int setLayout();

    public abstract BaseViewHolder setViewHolder();


    public static abstract class BaseViewHolder {
        private Context context;

        public abstract void findView(View convertView);

        @SuppressWarnings("rawtypes")
        public abstract void setData(int position, Object obj, List adapterList, YBaseYListViewAdapter adapter);

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }
}
