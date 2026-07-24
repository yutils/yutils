package com.yujing.test.activity.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.yujing.base.YBaseActivity
import com.yujing.bluetooth.BleServer
import com.yujing.contract.YListener1
import com.yujing.test.R
import com.yujing.test.databinding.ActivityAllTestBinding
import com.yujing.utils.YConvert
import com.yujing.utils.YPermissions
import com.yutils.view.utils.Create

/**
 * BLE蓝牙服务广播
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BleServerActivity : YBaseActivity<ActivityAllTestBinding>(R.layout.activity_all_test) {
    lateinit var textView1: TextView
    lateinit var textView2: TextView
    lateinit var editText1: EditText

    private var bleServer: BleServer? = null

    override fun init() {
        binding.wll.removeAllViews()
        binding.ll.removeAllViews()
        textView1 = Create.textView(binding.ll)
        textView2 = Create.textView(binding.ll)
        textView1.text = "正在请求蓝牙权限…"

        Create.button(binding.wll, "退出") {
            finish()
        }
        Create.button(binding.wll, "清除屏幕") {
            textView1.text = ""
            textView2.text = ""
        }

        requestBlePermissionsThenInit()
    }

    private fun blePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    }

    private fun requestBlePermissionsThenInit() {
        val perms = blePermissions()
        if (YPermissions.hasPermissions(this, *perms)) {
            initBleUiAndServer()
            return
        }
        YPermissions(this)
            .setAllSuccessListener { initBleUiAndServer() }
            .setFailListener {
                textView1.text = "权限被拒绝：$it\n请到系统设置开启蓝牙相关权限后重试"
                Toast.makeText(this, "缺少蓝牙权限，无法启动 BLE Server", Toast.LENGTH_LONG).show()
            }
            .request(*perms)
    }

    private fun initBleUiAndServer() {
        val server = BleServer(this)
        bleServer = server
        server.name = "YBle"
        if (!server.init()) {
            textView1.text = "BLE 初始化失败（可能未开蓝牙或不支持）"
        } else {
            textView1.text = "BLE Server 已就绪"
        }
        server.connectListener =
            YListener1 { device: BluetoothDevice? -> textView1.text = "连接成功:" + device?.address }
        server.disConnectListener =
            YListener1 { device: BluetoothDevice? -> textView1.text = "断开连接:" + device?.address }
        server.errorListener =
            YListener1 { s: String -> textView1.text = "错误：$s" }
        server.readListener =
            YListener1 { bytes: ByteArray? ->
                textView2.text =
                    "收到：" + YConvert.bytesToHexString(bytes) + "\n" + textView2.text.toString()
            }

        Create.space(binding.wll)
        Create.button(binding.wll, "打开蓝牙") {
            server.open()
        }
        Create.button(binding.wll, "打开服务") {
            server.startService()
        }
        Create.button(binding.wll, "关闭服务") {
            server.stopService()
        }.setTextColor(Color.parseColor("#FF0000"))
        Create.button(binding.wll, "关闭蓝牙") {
            server.close()
        }.setTextColor(Color.parseColor("#FF0000"))

        Create.space(binding.wll)
        editText1 = Create.editText(binding.wll, "1122AABB", "hex:0123456789ABCDEF")
        Create.button(binding.wll, "发送消息") {
            val hex = editText1.text.toString()
            server.send(YConvert.hexStringToByte(hex))
        }
    }

    override fun onDestroy() {
        bleServer?.stopService()
        super.onDestroy()
    }
}
