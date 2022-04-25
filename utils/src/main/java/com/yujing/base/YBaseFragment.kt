package com.yujing.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.base.fragment.YFragment
import com.yujing.bus.YBusUtil
import com.yujing.utils.TTS
import com.yujing.utils.YToast

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
abstract class YBaseFragment<B : ViewDataBinding>(var layout: Int?) : YFragment() {
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
        if (layout != null)//如果layout==null，请在initBefore里面给binding赋值
            binding = DataBindingUtil.inflate(inflater, layout!!, container, false)
        initBefore()
        init()
        initAfter()
        YBusUtil.init(this)
        return binding.root
    }

    /**
     * 初始化数据
     */
    protected abstract fun init()
    open fun initBefore() {}
    open fun initAfter() {}

    /**
     * 显示toast
     */
    open fun show(str: String?) {
        YToast.show(str,1)
    }

    /**
     * 播放语音
     */
    open fun speak(str: String?) {
        TTS.speak(str)
    }

    /**
     * 显示toast并播放语音
     */
    open fun showSpeak(str: String?) {
        YToast.showSpeak(str,1)
    }

    open fun startActivity(classActivity: Class<*>?) {
        val intent = Intent()
        intent.setClass(requireActivity(), classActivity!!)
        startActivity(intent)
    }

    open fun startActivity(classActivity: Class<*>?, resultCode: Int) {
        startActivityForResult(classActivity, resultCode)
    }

    open fun startActivityForResult(classActivity: Class<*>?, resultCode: Int) {
        val intent = Intent()
        intent.setClass(requireActivity(), classActivity!!)
        startActivityForResult(intent, resultCode)
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

    override fun onDestroy() {
        super.onDestroy()
        YBusUtil.onDestroy(this)
    }
}