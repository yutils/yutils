package com.yujing.test.activity

import android.Manifest
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import com.yujing.base.contract.YLifeEvent
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.R
import com.yujing.test.activity.bluetooth.BleClientActivity
import com.yujing.test.activity.bluetooth.BleServerActivity
import com.yujing.test.base.KBaseActivity
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.*
import com.yutils.view.utils.Create

class MainActivity : KBaseActivity<ActivityAllTestBinding>(null) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    override fun initBefore() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_test)
    }

    override fun init() {
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)
        //生命周期监听
        setEventListener { event, obj ->
            if (event == YLifeEvent.onDestroy) {
                YLog.d("关闭了")
            } else if (event == YLifeEvent.onStart) {
                YLog.d("onStart")
            }
        }

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

        val yPermissions = YPermissions(this)
        yPermissions.register()
        Create.button(binding.wll, "请求权限") {
            yPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).setSuccessListener {
                YLog.i("成功$it")
            }.setFailListener {
                YLog.i("失败$it")
            }.setAllSuccessListener {
                YLog.i("全部成功")
                YPermissions.requestAll(this)
            }
        }

        Create.button(binding.wll, "BLE_Server") {
            startActivity(BleServerActivity::class.java)
        }
        Create.button(binding.wll, "BLE_Client") {
            startActivity(BleClientActivity::class.java)
        }

        Create.button(binding.wll, "1111111") {
           YRunOnceOfTime.run(8000,"1111111"){

           }
        }
        Create.button(binding.wll, "2222222") {
            YRunOnceOfTime.run(8000,"2222222"){

            }
        }
        Create.button(binding.wll, "3333") {
            YRunOnceOfTime.toString()
        }
    }


    @YBus("tag1", "tag2", mainThread = false)
    fun message1(message: Any?) {
        YLog.i("收到：$message")
        textView1.text = textView1.text.toString() + "收到1:$message \n"
    }
}
