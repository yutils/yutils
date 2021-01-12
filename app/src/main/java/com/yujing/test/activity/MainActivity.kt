package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import com.yujing.base.YBaseActivity
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YDelay
import com.yujing.utils.YLog
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create


class MainActivity : YBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    override fun init() {
        YPermissions.requestAll(this)
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)
        Create.button(binding.wll, "弹出dialog") {
            val testDialog = TestDialog(this)
            testDialog.show()
        }
        Create.button(binding.wll, "测试") {
            YDelay.run(2000) {
                show("测试2")
            }
            show("测试1")
        }
        Create.space(binding.wll)//换行
        editText1 = Create.editText(binding.wll, "123456789")
        Create.button(binding.wll, "发送总线消息") {
            YBusUtil.post("tag1", editText1.text.toString())
        }
        Create.button(binding.wll, "消息22") {
            YBusUtil.post("tag2", "22222")
        }
    }

    @YBus("tag1")
    fun message(message: Any) {
        YLog.i("收到：$message")
        textView1.text = "收到:$message"
    }

    @YBus()
    fun message(key: Any,message: Any) {
        YLog.i("收到：$key:$message")
        textView1.text = "收到：$key:$message"
    }
}