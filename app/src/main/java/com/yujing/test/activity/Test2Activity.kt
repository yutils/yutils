package com.yujing.test.activity

import android.widget.TextView
import com.yujing.base.YBaseActivity
import com.yujing.bus.YBus
import com.yujing.bus.YMessage
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YLog
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class Test2Activity : YBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView

    override fun init() {
        YPermissions.requestAll(this)
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)
        Create.button(binding.wll, "退出") {
            finish()
        }
        Create.button(binding.wll, "测试") {

        }
    }

    override fun initAfter() {
        EventBus.getDefault().register(this)
    }

    @YBus("粘性事件")
    fun message1(message: Any) {
        YLog.i("收到：$message")
        textView1.text = textView1.text.toString() + "收到:$message \n"
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMessage(message: String) {
        YLog.i("收到eventBus：$message")
        textView1.text = textView1.text.toString() + "收到eventBus：$message \n"
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMessage(message: YMessage<Any>) {
        EventBus.getDefault().removeStickyEvent(message)
        YLog.i("收到eventBus：$message")
        textView1.text =
            textView1.text.toString() + "收到eventBus：${message.type} ， ${message.data.toString()} \n"
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}