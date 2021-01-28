package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.yujing.base.YBaseActivity
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.bus.YMessage
import com.yujing.test.App
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YDelay
import com.yujing.utils.YLog
import com.yujing.utils.YPath
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create


class MainActivity : YBaseActivity<ActivityAllTestBinding>(null) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    override fun initBefore() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_test)
    }

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
            textView1.text = ""
            YBusUtil.post("tag1", editText1.text.toString())
        }
        Create.button(binding.wll, "消息22") {
            textView1.text = ""
            YBusUtil.post("tag2", "222222222")
        }
        Create.space(binding.wll)//换行

        Create.button(binding.wll, "文件路径") {
            val path = YPath.getFilePath(App.get(), "配置") + "/" + "AAA.txt"
            YLog.i(path)
            show(path)
        }
        Create.button(binding.wll, "更改间距") {
            binding.wll.vertical_Space = 20F
            binding.wll.horizontal_Space = 20F
            binding.wll.gravity = 2
            //binding.wll.isFull = true
            binding.wll.requestLayout()
            binding.wll.invalidate()
        }
    }

    @YBus("tag1")
    fun message1(message: Any) {
        YLog.i("收到1：$message")
        textView1.text = textView1.text.toString() + "收到1:$message \n"
    }

    @YBus()
    fun message2(key: Any, message: Any) {
        YLog.i("收到2：$key:$message")
        textView1.text = textView1.text.toString() + "收到2：$key:$message \n"
    }

    override fun onEvent(yMessage: YMessage<Any>) {
        YLog.i("收到3：${yMessage.type}:${yMessage.data}")
        textView1.text = textView1.text.toString() + "收到3：${yMessage.type}:${yMessage.data} \n"
    }
}