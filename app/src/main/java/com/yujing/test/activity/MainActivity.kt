package com.yujing.test.activity

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.yujing.base.contract.YLifeEvent
import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.R
import com.yujing.test.activity.bluetooth.BleClientActivity
import com.yujing.test.activity.bluetooth.BleServerActivity
import com.yujing.test.base.KBaseActivity
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.*
import com.yujing.view.YAlertDialogUtils
import com.yutils.view.utils.Create


class MainActivity : KBaseActivity<ActivityAllTestBinding>(null) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText
    var yVersionUpdate = YVersionUpdate()

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
            when (event) {
                YLifeEvent.onDestroy -> {
                    YLog.d("MainActivity，onDestroy")
                }
                YLifeEvent.onStart -> {
                    YLog.d("MainActivity，onStart")
                }
                YLifeEvent.onCreate -> {
                    YLog.d("MainActivity，onCreate")
                }
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
//            val yPermissions = YPermissions(this)
//            yPermissions.setSuccessListener {
//                YLog.i("成功$it")
//            }.setFailListener {
//                YLog.i("失败$it")
//            }.setAllSuccessListener {
//                YLog.i("全部成功")
//            }.request(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            )
            YPermissions.requestAll(this)
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
            YTake.chosePictureAndCorp(this) {
                val bitmap = YConvert.uri2Bitmap(this, it)
                YImageDialog.show(bitmap)
            }
        }

        Create.button(binding.wll, "更新APP") {
            val url = "https://down.qq.com/qqweb/QQlite/qqlite_5.9.3.3468_Android_537067382.apk"
            yVersionUpdate.useNotificationDownload = false
            val description = "1.更新了xxxxxxx\n2.新增xxxxxxxxxxx\n3.修复xxxxx的bug\n4.版本迭代如果出现异常或者问题，请尽快联系开发者，进行修复和处理。谢谢大家的积极配合。"
            //yVersionUpdate.dialog.okButtonBackgroundColor= Color.parseColor("#21A9FA")
            yVersionUpdate.update(634, true, url, "6.3.4", description + description + description)
        }

        Create.button(binding.wll, "弹窗测试") {
            //多选
            val listName: MutableList<String> = ArrayList()
            listName.add("项目1")
            listName.add("项目2")
            listName.add("项目3")
            listName.add("项目4")
            listName.add("项目5")
            listName.add("项目6")
            val checked = BooleanArray(listName.size) { i -> false } //默认选中项，最终选中项
            YAlertDialogUtils().showMultiChoice("请选择", listName.toTypedArray(), checked) {
                //筛选选中项
                val newList: MutableList<String> = ArrayList()
                for (index in checked.indices) {
                    if (checked[index]) newList.add(listName[index])
                }
                textView1.text = "您选择了：${YJson.toJson(newList)}"
            }
        }

        TTS.filter = { it.replace("不正确", "不在范围内") }
        //语音过滤测试
        Create.button(binding.wll, "TTS过滤") {
            YTts.getInstance().speakToast("电子秤皮重不正确")
        }
    }

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
