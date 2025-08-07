package com.yujing.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.bus.YBusUtil
import com.yujing.utils.YShow

/**
 * 基础activity
 *
 * @param <B> ViewDataBinding
 * @author 余静 2021年1月13日10:13:26
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

RxBus用法
RxBus.getDefault().post(YMessage<Any?>(key,value))

YBus用法
//发送消息
YBusUtil.post("tag1","123456789")
//接收消息
@YBus("tag1")
fun message(message: Any) {
    YLog.i("收到：$message")
}
 */
/*
原生方案
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
 */
abstract class YBaseActivity<B : ViewDataBinding>(var layout: Int?) : AppCompatActivity() {
    //open val binding: B by lazy { DataBindingUtil.setContentView(this, layout) }
    lateinit var binding: B
    var isActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layout != null)//如果layout==null，请在initBefore里面给binding赋值
            binding = DataBindingUtil.setContentView(this, layout!!)
        initBefore()//初始化之前执行，这儿可以请求权限：YPermissions.requestAll(this)
        init()
        initAfter()
        YBusUtil.init(this)
    }

    /**
     * 初始化数据
     */
    protected abstract fun init()
    open fun initBefore() {}
    open fun initAfter() {}

    /**
     * 跳转
     */
    open fun startActivity(classActivity: Class<*>?) {
        isActive = false
        val intent = Intent(this, classActivity!!)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun finish() {
        super.finish()
        YShow.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        YBusUtil.onDestroy(this)
    }
}