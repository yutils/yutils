package com.yujing.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.rxbus.RxBus
import com.yujing.base.fragment.YFragment
import com.yujing.bus.YBusUtil
import com.yujing.bus.YMessage
import com.yujing.utils.YToast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 基础aFragment
 *
 * @param <B> ViewDataBinding
 * @author yujing 2020年12月21日17:01:56
 */
/* 用法举例
//kotlin
class AboutActivity : YBaseFragment<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {}
}
//java
public class OldFragment extends YBaseFragment<Activity1101Binding> {
    public OldFragment() {
        super(R.layout.activity_1101);
    }
    @Override
    protected void init() {

    }
}
 */
abstract class YBaseFragment<B : ViewDataBinding>(var layout: Int) : YFragment() {
    open val binding: B by lazy { DataBindingUtil.inflate(inflater, layout, container, false) }
    lateinit var inflater: LayoutInflater
    var container: ViewGroup? = null
    var isShow = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container
        binding//第一次使用变量的时候调用lazy初始化变量
        RxBus.getDefault().subscribeSticky(this, AndroidSchedulers.mainThread(), defaultCallback)
        YBusUtil.init(this)
        init()
        return binding.root
    }

    /**
     * 注册默认RxBus
     */
    var defaultCallback = object : RxBus.Callback<YMessage<Any>>() {
        override fun onEvent(yMessage: YMessage<Any>) {
            this@YBaseFragment.onEvent(yMessage)
        }
    }

    /**
     * RxBus回调
     *
     * @param yMessage 回调内容
     */
    open fun onEvent(yMessage: YMessage<Any>) {}

    /**
     * 初始化数据
     */
    protected abstract fun init()

    fun show(str: String?) {
        YToast.show(activity, str)
    }

    fun startActivity(classActivity: Class<*>?) {
        val intent = Intent()
        intent.setClass(activity!!, classActivity!!)
        startActivity(intent)
    }

    fun startActivity(classActivity: Class<*>?, resultCode: Int) {
        startActivityForResult(classActivity, resultCode)
    }

    fun startActivityForResult(classActivity: Class<*>?, resultCode: Int) {
        val intent = Intent()
        intent.setClass(activity!!, classActivity!!)
        startActivityForResult(intent, resultCode)
    }

    override fun onDestroy() {
        RxBus.getDefault().unregister(this)
        YBusUtil.onDestroy(this)
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isShow = !hidden
    }
}