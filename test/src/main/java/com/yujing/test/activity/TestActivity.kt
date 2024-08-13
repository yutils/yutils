package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import com.yujing.bus.ThreadMode
import com.yujing.bus.YBus
import com.yujing.test.R
import com.yujing.test.base.KBaseActivity
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YLog
import com.yutils.view.utils.Create

class TestActivity : KBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    override fun init() {
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)

        Create.button(binding.wll, "退出") {
            finish()
        }

        Create.button(binding.wll, "清除屏幕") {
            textView1.text = ""
            textView2.text = ""
        }

        //--------------------------------------------------------------------------------
        Create.space(binding.wll)//换行
        editText1 = Create.editText(binding.wll, "123456789")
        Create.button(binding.wll, "按钮") {

        }
    }

    @YBus("tag1", "tag2", threadMode = ThreadMode.MAIN)
    fun message1(message: String?) {
        YLog.i("收到：tag$message")
        textView2.text = textView2.text.toString() + "收到:$message \n"
    }
}