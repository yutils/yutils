package com.yujing.test.activity

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.yujing.bluetooth.BleServer
import com.yujing.contract.YListener1
import com.yujing.test.R
import com.yujing.test.base.KBaseActivity
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YConvert
import com.yutils.view.utils.Create

/**
 * BLE蓝牙客户端
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BleClientActivity : KBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText

    lateinit var bleServer: BleServer

    override fun init() {
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)

        bleServer = BleServer(this)
        bleServer.name = "YBle"
        bleServer.init()
        bleServer.connectListener =
            YListener1 { device: BluetoothDevice? -> textView1.text = "连接成功:" + device?.address }
        bleServer.disConnectListener =
            YListener1 { device: BluetoothDevice? -> textView1.text = "断开连接:" + device?.address }
        bleServer.errorListener =
            YListener1 { s: String -> textView1.text = "错误：$s" }
        bleServer.readListener =
            YListener1 { bytes: ByteArray? ->
                textView2.text =
                    "收到：" + YConvert.bytesToHexString(bytes) + "\n" + textView2.text.toString()
            }

        Create.button(binding.wll, "退出") {
            finish()
        }

        Create.button(binding.wll, "清除屏幕") {
            textView1.text = ""
            textView2.text = ""
        }

        //--------------------------------------------------------------------------------

        Create.space(binding.wll)//换行
        Create.button(binding.wll, "打开蓝牙") {
            bleServer.open()
        }

        Create.button(binding.wll, "打开服务") {
            bleServer.startService()
        }

        Create.button(binding.wll, "关闭服务") {
            bleServer.stopService()
        }.setTextColor(Color.parseColor("#FF0000"))

        Create.button(binding.wll, "关闭蓝牙") {
            bleServer.close()
        }.setTextColor(Color.parseColor("#FF0000"))

        Create.space(binding.wll)//换行
        editText1 = Create.editText(binding.wll, "1122AABB", "hex:0123456789ABCDEF")
        Create.button(binding.wll, "发送消息") {
            val hex = editText1.text.toString()
            bleServer.send(YConvert.hexStringToByte(hex))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleServer.stopService()
    }
}