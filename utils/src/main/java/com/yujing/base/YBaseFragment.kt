package com.yujing.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yujing.base.fragment.YFragment
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.bus.YMessage
import com.yujing.utils.YToast

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
abstract class YBaseFragment<B : ViewDataBinding>(var layout: Int?) : YFragment() {
    lateinit var binding: B // open val binding: B by lazy { DataBindingUtil.inflate(inflater, layout, container, false) }
    lateinit var inflater: LayoutInflater
    var container: ViewGroup? = null
    var isShow = false

    /** onCreateView*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container
        if (layout != null)//如果layout==null，请在initBefore里面给binding赋值
            binding = DataBindingUtil.inflate(inflater, layout!!, container, false)
        YBusUtil.init(this)
        initBefore()
        init()
        initAfter()
        return binding.root
    }

    /**
     * YBus总线消息
     *
     * @param yMessage 总线内容
     */
    @YBus()
    open fun onEvent(yMessage: YMessage<Any>) {
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
        if (str == null) return
        YToast.show(activity, str)
    }

    open fun startActivity(classActivity: Class<*>?) {
        val intent = Intent()
        intent.setClass(activity!!, classActivity!!)
        startActivity(intent)
    }

    open fun startActivity(classActivity: Class<*>?, resultCode: Int) {
        startActivityForResult(classActivity, resultCode)
    }

    open fun startActivityForResult(classActivity: Class<*>?, resultCode: Int) {
        val intent = Intent()
        intent.setClass(activity!!, classActivity!!)
        startActivityForResult(intent, resultCode)
    }

    override fun onDestroy() {
        YBusUtil.onDestroy(this)
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isShow = !hidden
    }
}