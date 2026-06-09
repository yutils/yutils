package com.yujing.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.yujing.bus.YBusUtil
import com.yujing.utils.YShow
import java.lang.reflect.Method

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
abstract class YBaseActivity<B : ViewBinding>(var layout: Int?) : AppCompatActivity() {
    //open val binding: B by lazy { DataBindingUtil.setContentView(this, layout) }
    lateinit var binding: B
    var isActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //如果layout==null，请在initBefore里面给binding赋值
        layout?.let { layout ->
            @Suppress("UNCHECKED_CAST")
            binding = when {
                // 如果是 ViewDataBinding，走 DataBinding 的绑定流程（支持双向绑定等）
                isDataBindingType() -> {
                    DataBindingUtil.setContentView(this, layout) as B
                }
                // 否则走纯 ViewBinding 的 inflate 流程
                else -> {
                    val inflateMethod: Method = getBindingClass().getMethod("inflate", LayoutInflater::class.java)
                    val vb = inflateMethod.invoke(null, layoutInflater) as B
                    setContentView(vb.root)
                    vb
                }
            }
        }
        initBefore()//初始化之前执行，这儿可以请求权限：YPermissions.requestAll(this)
        // DataBinding 专属：绑定生命周期（不影响 ViewBinding）
        if (binding is ViewDataBinding) {
            (binding as ViewDataBinding).lifecycleOwner = this
        }
        init()
        initAfter()
        YBusUtil.register(this)
    }

    /**
     * 判断当前泛型是否是 ViewDataBinding 的子类
     */
    private fun isDataBindingType(): Boolean {
        return ViewDataBinding::class.java.isAssignableFrom(getBindingClass())
    }

    /**
     * 获取泛型 B 的实际 Class
     * 循环向上查找，兼容多层继承场景
     */
    private fun getBindingClass(): Class<*> {
        var clazz: Class<*>? = this::class.java
        while (clazz != null) {
            val superClass = clazz.genericSuperclass
            if (superClass is java.lang.reflect.ParameterizedType) {
                val typeArg = superClass.actualTypeArguments[0]
                if (typeArg is Class<*> && ViewBinding::class.java.isAssignableFrom(typeArg)) {
                    return typeArg
                }
            }
            clazz = clazz.superclass
        }
        throw IllegalStateException("无法获取 YBaseActivity 的泛型参数，请确保继承链中存在 YBaseActivity<XxxBinding>")
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

    override fun onPause() {
        isActive = false
        super.onPause()
    }

    override fun onStop() {
        isActive = false
        super.onStop()
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
        YBusUtil.unregister(this)
    }
}