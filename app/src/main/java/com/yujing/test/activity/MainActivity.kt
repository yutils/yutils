package com.yujing.test.activity

import android.Manifest
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.yujing.base.contract.YLifeEvent
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.App
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
            } else if (event == YLifeEvent.onCreate) {
                YLog.d("onCreate")
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
                val latitude = location.latitude//纬度
                val longitude = location.longitude//经度
                textView1.text = "基站：latitude=${latitude}longitude$longitude"
            }
        }

        Create.button(binding.wll, "请求权限") {
            val yPermissions = YPermissions(this)
            yPermissions.setSuccessListener {
                YLog.i("成功$it")
            }.setFailListener {
                YLog.i("失败$it")
            }.setAllSuccessListener {
                YLog.i("全部成功")
                YPermissions.requestAll(this)
            }.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }

        Create.button(binding.wll, "BLE_Server") {
            startActivity(BleServerActivity::class.java)
        }

        Create.button(binding.wll, "BLE_Client") {
            startActivity(BleClientActivity::class.java)
        }

        Create.button(binding.wll, "线程中弹窗") {
            Thread {
                YImageDialog.show(R.mipmap.logo)
            }
        }
        Create.button(binding.wll, "统计线程") {
            YBusUtil.post("tag1", "" + YThread.printAllThread())
        }

        Create.button(binding.wll, "拍照") {
            YTake.take(this) {
                val bitmap = YConvert.uri2Bitmap(this, it)
                YImageDialog.show(bitmap)
            }
        }

        Create.button(binding.wll, "选择图片") {
            YTake.chosePicture(this) {
                val bitmap = YConvert.uri2Bitmap(this, it)
                YImageDialog.show(bitmap)
            }
        }

        Create.button(binding.wll, "更新APP") {
            val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
            yVersionUpdate.useNotificationDownload = false
            yVersionUpdate.update(999, true, url)
        }

        var i = 0
        Create.button(binding.wll, "YToast") {
            YToast.show("SHOW${i++}")
        }

    }

    //成员变量
    var yVersionUpdate = YVersionUpdate()

    //通知栏下载需要调用onDestroy()
    override fun onDestroy() {
        super.onDestroy()
        yVersionUpdate.onDestroy()
    }

    @YBus("tag1", "tag2")
    fun message1(tag: String, message: String) {
        YLog.i("收到：$tag$message")
        textView1.text = textView1.text.toString() + "收到1:$message \n"
    }
}
