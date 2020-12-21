package com.yujing.test.activity


import com.yujing.base.YBaseActivity
import com.yujing.test.R
import com.yujing.test.databinding.ActivityMainBinding


class MainActivity : YBaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun init() {
        binding.button1.setOnClickListener { show("按钮1") }
        binding.button2.setOnClickListener {
            var testDialog=TestDialog(this)
            testDialog.show()
        }
    }
}
