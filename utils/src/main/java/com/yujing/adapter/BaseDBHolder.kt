package com.yujing.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewHolder 基于DataBinding视图绑定
 * @author 余静 2022年6月7日10:21:55
 * 全称 BaseDataBindingViewHolder
 */
class BaseDBHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)