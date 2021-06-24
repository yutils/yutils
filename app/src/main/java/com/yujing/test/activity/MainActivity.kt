package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.R
import com.yujing.test.base.KBaseActivity
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YGps
import com.yujing.utils.YLog
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create
import org.greenrobot.eventbus.EventBus

class MainActivity : KBaseActivity<ActivityAllTestBinding>(null) {
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
        Create.button(binding.wll, "退出APP") {
            finish()
        }
        Create.button(binding.wll, "更改间距") {
            binding.wll.vertical_Space = 20F
            binding.wll.horizontal_Space = 20F
            binding.wll.gravity = 2
            //binding.wll.isFull = true
            binding.wll.requestLayout()
            binding.wll.invalidate()
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

        Create.button(binding.wll, "GPS") {
            //创建
            val yGps = YGps(this)
            //.每秒获取一次基站位置
            yGps.getLocationNET { location ->
                //location位置
                var latitude = location.latitude//纬度
                var longitude = location.longitude//经度
                textView1.text = "基站：latitude=${latitude}longitude$longitude"
            }
        }

        Create.button(binding.wll, "BLE_Server") {
            startActivity(BleServerActivity::class.java)
        }
    }


    @YBus("tag1", "tag2", mainThread = false)
    fun message1(message: Any?) {
        YLog.i("收到：$message")
        textView1.text = textView1.text.toString() + "收到1:$message \n"
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
