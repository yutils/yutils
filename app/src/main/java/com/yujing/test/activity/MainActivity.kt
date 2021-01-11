package com.yujing.test.activity

import android.widget.TextView
import com.yujing.base.YBaseActivity
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create


class MainActivity : YBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
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
            show("测试")
        }
    }
}