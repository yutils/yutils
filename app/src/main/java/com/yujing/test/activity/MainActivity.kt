package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.yujing.base.YBaseActivity
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.bus.YMessage
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.*
import com.yutils.view.utils.Create
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : YBaseActivity<ActivityAllTestBinding>(null) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    override fun initBefore() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_test)
        EventBus.getDefault().register(this)
    }

    override fun init() {
        YPermissions.requestAll(this)
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)
        Create.button(binding.wll, "更改间距") {
            binding.wll.vertical_Space = 20F
            binding.wll.horizontal_Space = 20F
            binding.wll.gravity = 2
            //binding.wll.isFull = true
            binding.wll.requestLayout()
            binding.wll.invalidate()
        }
        Create.button(binding.wll, "弹出dialog") {
            val testDialog = TestDialog(this)
            testDialog.show()
        }
        Create.button(binding.wll, "清除屏幕") {
            textView1.text = ""
        }
        //--------------------------------------------------------------------------------
        Create.space(binding.wll)//换行
        editText1 = Create.editText(binding.wll, "123456789")
        Create.button(binding.wll, "Ybus发送消息1") {
            YBusUtil.post("tag1", editText1.text.toString())
        }
        Create.button(binding.wll, "Ybus发送消息2") {
            YBusUtil.post("tag2", "222222222")
        }
        //--------------------------------------------------------------------------------
        Create.space(binding.wll)//换行
        Create.button(binding.wll, "EventBus,发送0") {
            EventBus.getDefault().post("你好0")

        }
        Create.button(binding.wll, "EventBus,粘性1") {
            EventBus.getDefault().postSticky("你好1")
        }
        Create.button(binding.wll, "删除粘性1") {
            EventBus.getDefault().removeStickyEvent("你好1")
        }

        val ym = YMessage("tag1", "value1")
        Create.button(binding.wll, "EventBus,粘性2") {
            EventBus.getDefault().postSticky(ym)
        }

        Create.button(binding.wll, "删除粘性2") {
            EventBus.getDefault().removeStickyEvent(ym)
        }

        //--------------------------------------------------------------------------------
        Create.space(binding.wll)//换行
        Create.button(binding.wll, "发送粘性事件1") {
            YBusUtil.postSticky("粘性事件", "你好啊！11")
        }
        Create.button(binding.wll, "发送粘性事件2") {
            YBusUtil.postSticky("粘性事件", "你好啊！22")
        }
        Create.button(binding.wll, "删除粘性事件") {
            YBusUtil.removeSticky()
        }
        Create.button(binding.wll, "跳转页面2") {
            startActivity(Test2Activity::class.java)
        }
        //--------------------------------------------------------------------------------
        Create.space(binding.wll)//换行
        Create.button(binding.wll, "写文件") {
            YSaveFiles.setBytes("文件", YConvert.bitmap2Bytes(YConvert.view2Bitmap(binding.wll)))
        }
        Create.button(binding.wll, "读文件") {
            val byteArray = YSaveFiles.getBytes("文件")
            if (byteArray != null) {
                val bitmap = YConvert.bytes2Bitmap(byteArray)
                YImageDialog.show(this, bitmap)
            }
        }
        Create.button(binding.wll, "删文件") {
            YSaveFiles.removeBytes("文件")
        }
        Create.space(binding.wll)//换行
        //--------------------------------------------------------------------------------
        Create.button(binding.wll, "打印行号") {
            YLog.i("打印行号:" + YStackTrace.getLine())
        }
    }

    @YBus("tag1")
    fun message1(message: Any) {
        YLog.i("收到1：$message")
        textView1.text = textView1.text.toString() + "收到1:$message \n"
    }

    @YBus
    fun message2(key: Any, message: Any) {
        YLog.i("收到2：$key:$message")
        textView1.text = textView1.text.toString() + "收到2：$key:$message \n"
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMessage(message: String) {
        YLog.i("收到4：$message")
        textView1.text = textView1.text.toString() + "收到4：$message \n"
    }

    override fun onEvent(yMessage: YMessage<Any>) {
        YLog.i("收到3：${yMessage.type}:${yMessage.data}")
        textView1.text = textView1.text.toString() + "收到3：${yMessage.type}:${yMessage.data} \n"
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}