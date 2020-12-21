@file:Suppress("MemberVisibilityCanBePrivate")

package com.yujing.base

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.rxbus.RxBus
import com.yujing.base.activity.YActivity
import com.yujing.contract.YMessage
import com.yujing.utils.YShow
import com.yujing.utils.YToast
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * 基础activity
 *
 * @param <B> ViewDataBinding
 * @author yujing 2020年12月21日17:01:19
 */
/*
用法：
//kotlin
class AboutActivity : YBaseActivity<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {
        binding.include.ivBack.setOnClickListener { finish() }
        binding.include.tvTitle.text = "关于我们"
    }
}
//java
public class OldActivity extends YBaseActivity<Activity1101Binding> {
    public OldActivity() {
        super(R.layout.activity_1101);
    }
    @Override
    protected void init() { }
}
 */
abstract class YBaseActivity<B : ViewDataBinding>(var layout: Int) : YActivity() {
    open val binding: B by lazy { DataBindingUtil.setContentView(this, layout) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding//第一次使用变量的时候调用lazy初始化变量
        RxBus.getDefault().subscribeSticky(this, AndroidSchedulers.mainThread(), defaultCallback)
        //YPermissions.requestAll(this)
        init()
    }

    /**
     * 注册默认RxBus
     */
    private var defaultCallback = object : RxBus.Callback<YMessage<Any>>() {
        override fun onEvent(yMessage: YMessage<Any>) {
            this@YBaseActivity.onEvent(yMessage)
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

    /**
     * 跳转
     */
    fun startActivity(classActivity: Class<*>?) {
        val intent = Intent()
        intent.setClass(this, classActivity!!)
        startActivity(intent)
    }

    /**
     * 跳转
     */
    fun startActivity(classActivity: Class<*>?, resultCode: Int) {
        startActivityForResult(classActivity, resultCode)
    }

    /**
     * 跳转
     */
    fun startActivityForResult(classActivity: Class<*>?, resultCode: Int) {
        val intent = Intent()
        intent.setClass(this, classActivity!!)
        startActivityForResult(intent, resultCode)
    }

    /**
     * 显示toast
     */
    fun show(str: String?) {
        if (str == null) return
        YToast.showLong(applicationContext, str)
    }

    /**
     * 窗口焦点改变监听
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onDestroy() {
        System.gc() // 系统自动回收
        RxBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        YShow.finish()
    }
}