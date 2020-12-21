package com.yujing.test.activity

import android.app.Activity
import com.yujing.base.YBaseDialog
import com.yujing.test.R
import com.yujing.test.databinding.TestDialogBinding

class TestDialog(activity: Activity) : YBaseDialog<TestDialogBinding>(activity, R.layout.test_dialog) {
    init {
        openAnimation = false
    }
    override fun init() {
        binding.button1.setOnClickListener { show("123") }
    }
}
