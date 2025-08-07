package com.yujing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerViewAdapter 基于DataBinding视图绑定
 * @author 余静 2022年6月7日10:21:55
 * 全称 BaseDataBindingRecyclerViewAdapter
 */
/*
用法举例：
class UserAdapter(var data: List<User>) : BaseDBAdapter<User>(R.layout.user_item, data) {
    override fun item(holder: BaseDBHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list?.get(position) as User

        binding.user = item
        binding.iv.setOnClickListener { YToast.show("点击：" + item.name) }
    }
}

class UserAdapter<T>(var data: List<T>) : BaseDBAdapter<T>(R.layout.user_item, data) {
    override fun item(holder: BaseDBHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list?.get(position) as User
    }
}

//或者
val list :MutableList<String> =ArrayList()
list.add("A")
list.add("B")
list.add("C")

val adapter = object : BaseDBAdapter<String>(R.layout.user_item, list) {
    override fun item(holder: BaseDBHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list?.get(position) as String
        binding.tvName.text = item
        binding.iv.setOnClickListener { YToast.show("点击图片：" + item) }
    }
}

//下拉刷新
adapter.onScrollToTopListener={//刷新}
//上拉加载
adapter.onToBottomListener={//加载}


原生用法：
//ViewHolder
class MyViewHolder(var binding: ActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {}
class CarAdapter<T>(var list: List<T>?) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.activity_list_item, parent, false))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list?.get(position) as User
        holder.binding.data = item
        holder.binding.iv.setOnClickListener { YToast.show("点击：" + item.name) }
        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}
 */
@Deprecated("废弃")
abstract class BaseDBAdapter<T>(var layout: Int, var list: List<T>?) : RecyclerView.Adapter<BaseDBHolder>() {
    //recyclerView
    var recyclerView: RecyclerView? = null

    //单击
    var onItemClickListener: ((position: Int) -> Unit)? = null

    //长按
    var onItemLongClickListener: ((position: Int) -> Unit)? = null

    //到顶部监听，到顶部后继续滑动，不会触发
    var onToTopListener: (() -> Unit)? = null

    //到底部监听，到底部后继续滑动，不会触发
    var onToBottomListener: (() -> Unit)? = null

    //滑动到顶部监听，到顶部后继续滑动，会触发 （下拉刷新）
    var onScrollToTopListener: (() -> Unit)? = null

    //滑动到底部监听，到底部后继续滑动，会触发 （上拉加载）
    var onScrollToBottomListener: (() -> Unit)? = null

    var isSelect: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDBHolder {
        return BaseDBHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false))
    }

    abstract fun item(holder: BaseDBHolder, position: Int)

    override fun onBindViewHolder(holder: BaseDBHolder, position: Int) {
        item(holder, position)

        //单击
        holder.binding.root.setOnClickListener { onItemClickListener?.invoke(position) }

        //长按
        holder.binding.root.setOnLongClickListener { onItemLongClickListener?.invoke(position);false }

        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        //滚动监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            //滚动完毕
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) onToTopListener?.invoke()
                if (!recyclerView.canScrollVertically(1)) onToBottomListener?.invoke()
            }

            //滚动状态改变
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //newState分 0,1,2三个状态,2是滚动状态,0是停止
                if (newState == 0) {
                    if (!recyclerView.canScrollVertically(-1)) onScrollToTopListener?.invoke()
                    if (!recyclerView.canScrollVertically(1)) onScrollToBottomListener?.invoke()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}
