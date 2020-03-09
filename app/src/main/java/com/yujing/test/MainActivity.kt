package com.yujing.test

import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.setOnClickListener { show("123") }
        button2.setOnClickListener { }
        button3.setOnClickListener { }
        button4.setOnClickListener { }
        button5.setOnClickListener { }
        button6.setOnClickListener { }
        button7.setOnClickListener { }
        button8.setOnClickListener { }
    }
}
