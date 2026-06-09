package com.yujing.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.yujing.bus.YBusUtil
import java.lang.reflect.Method

/**
 * 基础aFragment
 *
 * @param <B> ViewDataBinding
 * @author 余静 2020年12月21日17:01:56
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
abstract class YBaseFragment<B : ViewBinding>(var layout: Int?) : Fragment() {
    // open val binding: B by lazy { DataBindingUtil.inflate(inflater, layout, container, false) }
    lateinit var binding: B
    lateinit var inflater: LayoutInflater
    var container: ViewGroup? = null
    var isActive = false

    //要判断是否添加，因为实例化后isHidden默认true
    var isShow: Boolean? = false
        get() = isAdded && !isHidden

    /** onCreateView*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container
        if (layout != null) {
            @Suppress("UNCHECKED_CAST")
            binding = when {
                // DataBinding: 使用 DataBindingUtil.inflate，并设置 lifecycleOwner
                isDataBindingType() -> {
                    val db = DataBindingUtil.inflate(inflater, layout!!, container, false) as B
                    if (db is ViewDataBinding) db.lifecycleOwner = viewLifecycleOwner
                    db
                }
                // 纯 ViewBinding: 反射调用 inflate(inflater, container, attachToParent)
                else -> {
                    val inflateMethod: Method = getBindingClass().getMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.javaPrimitiveType
                    )
                    inflateMethod.invoke(null, inflater, container, false) as B
                }
            }
        }
        initBefore()
        init()
        initAfter()
        YBusUtil.register(this)
        return binding.root
    }

    /**
     * 判断当前泛型是否是 ViewDataBinding 的子类
     */
    private fun isDataBindingType(): Boolean {
        return ViewDataBinding::class.java.isAssignableFrom(getBindingClass())
    }

    /**
     * 获取泛型 B 的实际 Class
     * 注意：如果存在多层继承，需循环向上查找 genericSuperclass
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
        throw IllegalStateException("无法获取 YBaseFragment 的泛型参数，请确保直接继承 YBaseFragment<XxxBinding>")
    }

    /**
     * 初始化数据
     */
    protected abstract fun init()
    open fun initBefore() {}
    open fun initAfter() {}

    open fun startActivity(classActivity: Class<*>?) {
        val intent = Intent()
        intent.setClass(requireActivity(), classActivity!!)
        startActivity(intent)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isShow = !hidden
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // ⚠️ Fragment 视图销毁时清除 binding 引用，防止内存泄漏
        if (::binding.isInitialized && binding is ViewDataBinding) {
            (binding as ViewDataBinding).unbind()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        YBusUtil.unregister(this)
    }
}