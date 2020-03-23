package com.yujing.test

import com.yujing.utils.YSave
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.text="YSave写入"
        button1.setOnClickListener { IP="123456" }
        button2.text="YSave读取"
        button2.setOnClickListener { show(IP) }
        button3.setOnClickListener { }
        button4.setOnClickListener { }
        button5.setOnClickListener { }
        button6.setOnClickListener { }
        button7.setOnClickListener { }
        button8.setOnClickListener {  }
    }

    //ip
    var IP: String
        get() = YSave.get(App.get(), "IP", String::class.java, "127.0.0.1")
        set(ip) {
            YSave.put(App.get(), "IP", ip)
        }
}
