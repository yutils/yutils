package com.yujing.test

import android.util.Log
import com.yujing.utils.YCheck
import com.yujing.utils.YDateDialog
import com.yujing.utils.YSave
import com.yujing.utils.YUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.text = "YSave写入"
        button1.setOnClickListener { IP = "123456" }
        button2.text = "YSave读取"
        button2.setOnClickListener { show(IP) }
        button3.text = "Date测试"
        button3.setOnClickListener { openDate() }
        button4.text = "对象复制"
        button4.setOnClickListener { copy() }
        button5.setOnClickListener { port() }
        button6.setOnClickListener { }
        button7.setOnClickListener { }
        button8.setOnClickListener { }
        var a=123456
        text4.text =
            """
                测试测试测试测试测试测试测试测试测${a}试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试
                测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测
                测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测
                测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测
            """
    }

    private fun port() {
        if (YCheck.isPort("10136")){
            show("正确")
        }else{
            show("不正确")
        }
    }


    private fun copy() {
        var user1:User= User("111",123)
        var user2=YUtils.copyObject(user1)
        Log.e("T"," "+user2.name)
        user2.name="456789"
        Log.e("T"," "+user1.name)
        Log.e("T"," "+user2.name)
    }

    private fun openDate() {
        YDateDialog.setDefaultFullScreen(true)
        val yDateDialog = YDateDialog(this)
        yDateDialog.setFormat("yyyy年MM月dd日") // 设置日期格式（如："yyyy年MM月dd日HH:mm"）
        yDateDialog.initTime(Date()) //设置初始化日期，必须和设置格式相同（如："2016年07月01日15:19"）
        yDateDialog.isShowDay = true // 设置是否显示日滚轮,默认显示
        yDateDialog.isShowTime = false // 设置是否显示时间滚轮,默认显示
        yDateDialog.isShowMonth = true // 设置是否显示时间滚轮,默认显示
        yDateDialog.show { format: String?, calendar: Calendar?, date: Date?, yyyy: String?, MM: String?, dd: String?, HH: String?, mm: String? -> }
    }

    //ip
    var IP: String
        get() = YSave.get(App.get(), "IP", String::class.java, "127.0.0.1")
        set(ip) {
            //设置还可以这样 YSave.create(this)["IP",String::class.java,""]
            YSave.put(App.get(), "IP", ip)
        }
}
