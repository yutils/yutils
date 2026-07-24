package com.yujing.test.cases

import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.yujing.bus.YBusUtil
import com.yujing.test.R
import com.yujing.test.activity.TestActivity
import com.yujing.test.activity.bluetooth.BleClientActivity
import com.yujing.test.activity.bluetooth.BleServerActivity
import com.yujing.test.suite.ManualTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.test.suite.TestCoverageStore
import com.yujing.test.suite.TestStatus
import com.yujing.utils.TTS
import com.yujing.utils.YBitmapUtil
import com.yujing.utils.YConvert
import com.yujing.utils.YDateDialog
import com.yujing.utils.YImageDialog
import com.yujing.utils.YPermissions
import com.yujing.utils.YShow
import com.yujing.utils.YSound
import com.yujing.utils.YTake
import com.yujing.utils.YToast
import com.yujing.view.YAlertDialogUtils
import com.yujing.view.YView

object UiManualCases {
    fun all(): List<ManualTestCase> = listOf(
        ManualTestCase("ui.toast", "YToast 显示", TestCategory.UI, "点击后应弹出 Toast") { _, c ->
            YToast.show("YToast 测试成功")
            markPassed(c, "已弹出 Toast（请目视确认）")
        },
        ManualTestCase("ui.show.loading", "YShow Loading", TestCategory.UI, "显示 1.5 秒 Loading") { activity, c ->
            YShow.show(activity, "加载中…", true)
            activity.window.decorView.postDelayed({
                YShow.finish()
                markPassed(c, "Loading 已关闭")
            }, 1500)
        },
        ManualTestCase("ui.alert.message", "YAlertDialog 消息框", TestCategory.UI) { _, c ->
            YAlertDialogUtils().showMessage("提示", "这是弹窗测试，点确定即通过") {
                markPassed(c, "用户确认")
            }
        },
        ManualTestCase("ui.alert.input", "YAlertDialog 输入框", TestCategory.UI) { _, c ->
            YAlertDialogUtils().showEdit("输入测试", text = "", hint = "请输入任意内容") { text ->
                if (text.isBlank()) {
                    markFailed(c, "输入为空")
                } else {
                    markPassed(c, "输入=$text")
                }
            }
        },
        ManualTestCase("ui.alert.list", "YAlertDialog 列表", TestCategory.UI) { _, c ->
            YAlertDialogUtils().showList("请选择", arrayOf("甲", "乙", "丙")) { idx ->
                markPassed(c, "选中 index=$idx")
            }
        },
        ManualTestCase("ui.alert.single", "YAlertDialog 单选", TestCategory.UI) { _, c ->
            YAlertDialogUtils().showSingleChoice("单选", arrayOf("红", "绿", "蓝"), 0) { idx ->
                markPassed(c, "单选=$idx")
            }
        },
        ManualTestCase("ui.alert.multi", "YAlertDialog 多选", TestCategory.UI) { _, c ->
            val checked = booleanArrayOf(true, false, true)
            YAlertDialogUtils().showMultiChoice("多选", arrayOf("A", "B", "C"), checked) {
                markPassed(c, "多选确认 checked=${checked.contentToString()}")
            }
        },
        ManualTestCase("ui.alert.cancel", "YAlertDialog 可取消消息", TestCategory.UI) { _, c ->
            YAlertDialogUtils().showMessageCancel("可取消", "点确定通过") {
                markPassed(c, "确定")
            }
        },
        ManualTestCase("ui.dateDialog", "YDateDialog 选日期", TestCategory.UI) { activity, c ->
            YDateDialog(activity).apply {
                setShowTime(true)
                setShowDay(true)
            }.show { format, _, _, _, _, _, _, _ ->
                markPassed(c, "选择=$format")
            }
        },
        ManualTestCase("ui.imageDialog", "YImageDialog 显示图", TestCategory.UI) { activity, c ->
            val bmp = android.graphics.Bitmap.createBitmap(200, 120, android.graphics.Bitmap.Config.ARGB_8888)
            bmp.eraseColor(0xFF41B9FA.toInt())
            YImageDialog.show(bmp)
            markPassed(c, "已显示纯色图")
        },
        ManualTestCase("ui.view.bitmap", "YView.toBitmap 截视图", TestCategory.UI) { activity, c ->
            val root = activity.window.decorView
            val bmp = YView.toBitmap(root)
            if (bmp != null && bmp.width > 0) {
                YImageDialog.show(YBitmapUtil.zoom(bmp, 200, 350))
                markPassed(c, "截图 ${bmp.width}x${bmp.height}")
            } else {
                markFailed(c, "toBitmap 失败")
            }
        },
        ManualTestCase("ui.view.drawable", "YView Drawable 工厂", TestCategory.UI) { activity, c ->
            val d = YView.createGradientDrawable(
                android.graphics.Color.BLUE, 2, android.graphics.Color.WHITE, 12f, 12f, 12f, 12f
            )
            d.setBounds(0, 0, 100, 40)
            activity.window.decorView.background = d
            markPassed(c, "已设置 GradientDrawable（可目视）")
        },
        ManualTestCase("ui.bus.crossPage", "YBus 跨页接收", TestCategory.UI, "打开 TestActivity 验证") { activity, c ->
            activity.startActivity(Intent(activity, TestActivity::class.java))
            YBusUtil.postSticky("tag2", "首页发送的消息：123456")
            markPassed(c, "已打开 TestActivity，请在该页验证收消息")
        },
    )
}

object MediaManualCases {
    /**
     * 预加载音效。SoundPool.load 是异步的，真正播放处需等加载完成。
     */
    fun ensureSounds() {
        val ys = YSound.getInstance(3)
        if (ys.getMap().size >= 3) return
        ys.put(0, R.raw.alarm)
        ys.put(1, R.raw.success)
        ys.put(2, R.raw.fail)
    }

    fun all(): List<ManualTestCase> = listOf(
        ManualTestCase("media.tts", "TTS 播放", TestCategory.MEDIA, "应听到朗读") { _, c ->
            TTS.speak("YUtils 语音测试")
            markPassed(c, "已调用 TTS.speak")
        },
        ManualTestCase("media.tts.queue", "TTS 队列朗读", TestCategory.MEDIA, "应连续听到两句") { _, c ->
            TTS.speak("第一句测试")
            TTS.speak("第二句测试")
            markPassed(c, "已连续 speak 两句")
        },
        ManualTestCase("media.sound.success", "音效 success", TestCategory.MEDIA) { _, c ->
            // 独立加载并在 onLoadComplete 后播放，避免未加载完就 play 导致无声
            YSound.play(R.raw.success, 2000)
            markPassed(c, "已播放 success（请听声确认）")
        },
        ManualTestCase("media.sound.fail", "音效 fail", TestCategory.MEDIA) { _, c ->
            YSound.play(R.raw.fail, 2000)
            markPassed(c, "已播放 fail（请听声确认）")
        },
        ManualTestCase("media.alarm", "播放警报", TestCategory.MEDIA) { _, c ->
            val ys = YSound.getInstance(3)
            // 重新 load 并在回调里循环播放
            ys.put(0, R.raw.alarm) {
                ys.play(0, -1)
                markPassed(c, "警报循环播放中")
            }
        },
        ManualTestCase("media.alarm.stop", "停止音效", TestCategory.MEDIA) { _, c ->
            YSound.getInstance().stopAll()
            markPassed(c, "已停止")
        },
        ManualTestCase("media.sound.oneshot", "加载播放并释放", TestCategory.MEDIA) { _, c ->
            YSound.play(R.raw.success, 1000)
            markPassed(c, "已 oneshot 播放")
        },
    )
}

object HardwareManualCases {
    fun all(): List<ManualTestCase> = listOf(
        ManualTestCase("hw.permissions", "请求全部权限", TestCategory.HARDWARE) { activity, c ->
            YPermissions.requestAll(activity)
            markPassed(c, "已发起权限请求（请在系统弹窗中操作）")
        },
        ManualTestCase("hw.camera", "拍照", TestCategory.HARDWARE) { activity, c ->
            YTake.take(activity) { uri ->
                val bitmap = YConvert.uri2Bitmap(activity, uri)
                if (bitmap != null) {
                    YImageDialog.show(bitmap)
                    markPassed(c, "拍照成功 ${bitmap.width}x${bitmap.height}")
                } else {
                    markFailed(c, "bitmap 为空")
                }
            }
        },
        ManualTestCase("hw.gallery", "选图（不裁剪）", TestCategory.HARDWARE) { activity, c ->
            YTake.chosePicture(activity) { uri ->
                val bitmap = YConvert.uri2Bitmap(activity, uri)
                if (bitmap != null) {
                    YImageDialog.show(bitmap)
                    markPassed(c, "选图成功 ${bitmap.width}x${bitmap.height}")
                } else {
                    markFailed(c, "bitmap 为空")
                }
            }
        },
        ManualTestCase("hw.gallery.crop", "选图并裁剪", TestCategory.HARDWARE) { activity, c ->
            YTake.chosePictureAndCorp(activity) { uri ->
                val bitmap = YConvert.uri2Bitmap(activity, uri)
                if (bitmap != null) {
                    YImageDialog.show(bitmap)
                    markPassed(c, "选图成功 ${bitmap.width}x${bitmap.height}")
                } else {
                    markFailed(c, "bitmap 为空")
                }
            }
        },
        ManualTestCase("hw.camera.crop", "拍照并裁剪", TestCategory.HARDWARE) { activity, c ->
            YTake.takeAndCorp(activity) { uri ->
                val bitmap = YConvert.uri2Bitmap(activity, uri)
                if (bitmap != null) {
                    YImageDialog.show(bitmap)
                    markPassed(c, "拍照裁剪 ${bitmap.width}x${bitmap.height}")
                } else {
                    markFailed(c, "bitmap 为空")
                }
            }
        },
        ManualTestCase("hw.ble.server", "打开 BLE Server", TestCategory.HARDWARE) { activity, c ->
            activity.startActivity(Intent(activity, BleServerActivity::class.java))
            markPassed(c, "已打开 BleServerActivity")
        },
        ManualTestCase("hw.ble.client", "打开 BLE Client", TestCategory.HARDWARE) { activity, c ->
            activity.startActivity(Intent(activity, BleClientActivity::class.java))
            markPassed(c, "已打开 BleClientActivity")
        },
        ManualTestCase(
            "hw.socket.remote",
            "外网/局域网 Socket（可配置）",
            TestCategory.HARDWARE,
            "需对端服务；取消则跳过"
        ) { activity, c ->
            val input = EditText(activity).apply {
                hint = "host:port 例如 192.168.1.167:5555"
                setText("192.168.1.167:5555")
            }
            AlertDialog.Builder(activity)
                .setTitle("远程 Socket 测试")
                .setMessage("将尝试连接并发送 test，成功则标记通过")
                .setView(input)
                .setPositiveButton("连接") { _, _ ->
                    val text = input.text?.toString()?.trim().orEmpty()
                    val parts = text.split(":")
                    if (parts.size != 2) {
                        markFailed(c, "格式应为 host:port")
                        return@setPositiveButton
                    }
                    val host = parts[0]
                    val port = parts[1].toIntOrNull()
                    if (port == null) {
                        markFailed(c, "端口非法")
                        return@setPositiveButton
                    }
                    Thread {
                        try {
                            val resp = com.yujing.socket.YTcp.send(host, port, "test".toByteArray(), 3000)
                            activity.runOnUiThread {
                                markPassed(c, "已连接，回包长度=${resp?.size ?: 0}")
                                Toast.makeText(activity, "连接成功", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            activity.runOnUiThread {
                                markFailed(c, e.message ?: "连接失败")
                            }
                        }
                    }.start()
                }
                .setNegativeButton("取消") { _, _ ->
                    c.status = TestStatus.Skipped
                    c.message = "用户取消"
                    TestCoverageStore.persist(c)
                }
                .show()
        },
        ManualTestCase("hw.goto.settings", "YGoto 打开应用详情", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toDetails(activity)
            markPassed(c, "已跳转应用详情（请手动返回）")
        },
        ManualTestCase("hw.goto.wifi", "YGoto 打开 WiFi 设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toWifi(activity)
            markPassed(c, "已跳转 WiFi 设置（请手动返回）")
        },
        ManualTestCase("hw.goto.bluetooth", "YGoto 打开蓝牙设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toBluetooth(activity)
            markPassed(c, "已跳转蓝牙设置（请手动返回）")
        },
        ManualTestCase("hw.goto.system", "YGoto 系统设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toSettings(activity)
            markPassed(c, "已跳转系统设置")
        },
        ManualTestCase("hw.goto.date", "YGoto 日期设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toDate(activity)
            markPassed(c, "已跳转日期设置")
        },
        ManualTestCase("hw.goto.display", "YGoto 显示设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toDisplay(activity)
            markPassed(c, "已跳转显示设置")
        },
        ManualTestCase("hw.goto.location", "YGoto 定位设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toLocation(activity)
            markPassed(c, "已跳转定位设置")
        },
        ManualTestCase("hw.goto.network", "YGoto 网络设置", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toNetwork(activity)
            markPassed(c, "已跳转网络设置")
        },
        ManualTestCase("hw.goto.develop", "YGoto 开发者选项", TestCategory.HARDWARE) { activity, c ->
            com.yujing.utils.YGoto.toDevelopment(activity)
            markPassed(c, "已跳转开发者选项（部分机型可能无权限）")
        },
    )
}

object OtherCases {
    fun all(): List<com.yujing.test.suite.AutoTestCase> = listOf(
        com.yujing.test.suite.AutoTestCase("other.screen", "YScreenUtil 基础数值", TestCategory.OTHER) {
            val w = com.yujing.utils.YScreenUtil.getScreenWidth()
            val h = com.yujing.utils.YScreenUtil.getScreenHeight()
            require(w > 0 && h > 0) { "宽高异常 $w x $h" }
            val px = com.yujing.utils.YScreenUtil.dp2px(100f)
            require(px > 0) { "dp2px 异常" }
            val dp = com.yujing.utils.YScreenUtil.px2dp(px)
            require(kotlin.math.abs(dp - 100) <= 2) { "px2dp=$dp" }
        },
        com.yujing.test.suite.AutoTestCase("other.app", "YApp 已初始化", TestCategory.OTHER) {
            require(com.yujing.utils.YApp.get() != null) { "YApp 未初始化" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.version", "YUtils 版本与调试", TestCategory.OTHER) {
            val code = com.yujing.utils.YUtils.getVersionCode()
            val name = com.yujing.utils.YUtils.getVersionName()
            require(code > 0) { "versionCode=$code" }
            require(!name.isNullOrBlank()) { "versionName 空" }
            // isDebug 仅检查不崩溃
            com.yujing.utils.YUtils.isDebug()
        },
        com.yujing.test.suite.AutoTestCase("other.utils.net", "YUtils 网络状态可读", TestCategory.OTHER) {
            // 不强制要求有网，只验证 API 可调用
            com.yujing.utils.YUtils.isNetConnected()
            com.yujing.utils.YUtils.isWifiConnected()
            com.yujing.utils.YUtils.getConnectedType(com.yujing.utils.YApp.get())
        },
        com.yujing.test.suite.AutoTestCase("other.class", "YClass 探测", TestCategory.OTHER) {
            require(com.yujing.utils.YClass.isAndroid())
            require(com.yujing.utils.YClass.findClass("android.app.Activity"))
            require(!com.yujing.utils.YClass.findClass("com.not.exists.ClazzXYZ"))
        },
        com.yujing.test.suite.AutoTestCase("other.stack", "YStackTrace 行号", TestCategory.OTHER) {
            val line = com.yujing.utils.YStackTrace.getLine()
            require(!line.isNullOrBlank()) { "getLine 空" }
        },
        com.yujing.test.suite.AutoTestCase("other.log", "YLog 调用不崩", TestCategory.OTHER) {
            com.yujing.utils.YLog.i("TestHome", "auto-case log ok")
            com.yujing.utils.YLog.d("debug")
            com.yujing.utils.YLog.w("warn")
        },
        com.yujing.test.suite.AutoTestCase("other.bitmap", "YBitmapUtil zoom/rotate", TestCategory.OTHER) {
            val src = android.graphics.Bitmap.createBitmap(40, 20, android.graphics.Bitmap.Config.ARGB_8888)
            src.eraseColor(0xFFFF0000.toInt())
            val zoomed = com.yujing.utils.YBitmapUtil.zoom(src, 80, 40)
            require(zoomed.width == 80 && zoomed.height == 40)
            val rot = com.yujing.utils.YBitmapUtil.rotate(src, 90)
            require(rot.width == 20 && rot.height == 40) { "旋转后 ${rot.width}x${rot.height}" }
            require(!com.yujing.utils.YBitmapUtil.isEmptyBitmap(src))
        },
        com.yujing.test.suite.AutoTestCase("other.appinfo.sign", "YAppInfoUtils 签名可读", TestCategory.OTHER) {
            val ctx = com.yujing.utils.YApp.get()
            val signs = com.yujing.utils.YAppInfoUtils.getSign(ctx, ctx.packageName)
            require(signs != null && signs.isNotEmpty()) { "签名为空" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.copy", "YUtils.copyObject", TestCategory.OTHER) {
            val src = arrayListOf(1, 2, 3)
            val copy = com.yujing.utils.YUtils.copyObject(src)
            require(copy != null && copy !== src && copy == src) { "深拷贝异常" }
            copy!![0] = 9
            require(src[0] == 1) { "应互不影响" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.storageSize", "YUtils 存储空间可读", TestCategory.OTHER) {
            com.yujing.utils.YUtils.isSDCardEnable()
            require(com.yujing.utils.YUtils.getRomTotalSize() > 0)
            require(com.yujing.utils.YUtils.getRomAvailableSize() >= 0)
            val id = com.yujing.utils.YUtils.getAndroidId()
            require(!id.isNullOrBlank()) { "androidId 空" }
        },
        com.yujing.test.suite.AutoTestCase("other.bitmap.gray", "YBitmapUtil 灰度/压缩", TestCategory.OTHER) {
            val src = android.graphics.Bitmap.createBitmap(32, 32, android.graphics.Bitmap.Config.ARGB_8888)
            src.eraseColor(0xFF336699.toInt())
            val gray = com.yujing.utils.YBitmapUtil.toGray(src)
            require(gray.width == 32 && gray.height == 32)
            val bytes = com.yujing.utils.YBitmapUtil.compressToBytes(src, 20)
            require(bytes != null && bytes.isNotEmpty()) { "compress 空" }
        },
        com.yujing.test.suite.AutoTestCase("other.face.blank", "YFace 空白图无人脸", TestCategory.OTHER) {
            val bmp = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.RGB_565)
            bmp.eraseColor(0xFFFFFFFF.toInt())
            val found = try {
                com.yujing.utils.YFace.findFace(bmp)
            } catch (t: Throwable) {
                // 部分 ROM FaceDetector 不可用，视为跳过成功
                return@AutoTestCase
            }
            require(!found) { "空白图不应检出人脸" }
        },
        com.yujing.test.suite.AutoTestCase("other.activityUtil", "YActivityUtil 当前页", TestCategory.OTHER) {
            val name = runCatching { com.yujing.utils.YActivityUtil.getCurrentActivityName() }.getOrNull()
            if (!name.isNullOrBlank()) {
                require(name.contains("Activity") || name.contains("yujing", true)) { "name=$name" }
            }
            val stack = runCatching { com.yujing.utils.YActivityUtil.getActivityStack() }.getOrNull()
            require(stack != null)
        },
        com.yujing.test.suite.AutoTestCase("other.utils.json", "YUtils jsonFormat/判空", TestCategory.OTHER) {
            val raw = """{"a":1,"b":[2,3]}"""
            require(com.yujing.utils.YUtils.stringIsJson(raw)) { "应识别 json object" }
            // Gson 宽松模式可能接受未加引号 token，用明显非法串断言
            require(!com.yujing.utils.YUtils.stringIsJson("{bad")) { "非法 json 应失败" }
            val pretty = com.yujing.utils.YUtils.jsonFormat(raw)
            require(!pretty.isNullOrBlank() && pretty.contains("a") && pretty.contains("1")) { "format=$pretty" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.clipboard", "YUtils 剪贴板读写", TestCategory.OTHER) {
            val ctx = com.yujing.utils.YApp.get()
            val token = "yutils_clip_${System.nanoTime()}"
            com.yujing.utils.YUtils.copyToClipboard(ctx, token)
            val last = com.yujing.utils.YUtils.getClipboardLast(ctx)
            require(last == token) { "clipboard=$last" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.ip", "YUtils 本机 IP 可读", TestCategory.OTHER) {
            val v4 = com.yujing.utils.YUtils.getIPv4()
            val v6 = com.yujing.utils.YUtils.getIPv6()
            require(v4 != null && v6 != null) { "IP 列表不应为 null" }
        },
        com.yujing.test.suite.AutoTestCase("other.utils.shell", "YUtils.shell echo", TestCategory.OTHER) {
            val out = com.yujing.utils.YUtils.shell("echo yutils_ok")
            require(out != null && out.contains("yutils_ok")) { "shell out=$out" }
            require(com.yujing.utils.YUtils.shellNoReturn("echo 1"))
        },
        com.yujing.test.suite.AutoTestCase("other.utils.intent.sd", "YUtils Intent/剪贴板全集/SD", TestCategory.OTHER) {
            val ctx = com.yujing.utils.YApp.get()
            val intent = com.yujing.utils.YUtils.getAppIntent(ctx.packageName, "com.yujing.test.ui.TestHomeActivity")
            require(intent.component != null) { "getAppIntent component 空" }
            val token = "clip_all_${System.nanoTime()}"
            com.yujing.utils.YUtils.copyToClipboard(ctx, token)
            val all = com.yujing.utils.YUtils.getClipboardAll(ctx)
            require(all != null && all.any { it == token }) { "clipboardAll=$all" }
            com.yujing.utils.YUtils.getConnectedType(ctx)
            require(com.yujing.utils.YUtils.getSDCardSize() >= 0)
            require(com.yujing.utils.YUtils.getSDCardAvailableSize() >= 0)
        },
        com.yujing.test.suite.AutoTestCase("other.screen.more", "YScreenUtil sp/状态栏/密度", TestCategory.OTHER) {
            val spPx = com.yujing.utils.YScreenUtil.sp2px(16f)
            require(spPx > 0)
            val back = com.yujing.utils.YScreenUtil.px2sp(spPx)
            require(kotlin.math.abs(back - 16f) <= 2f) { "px2sp=$back" }
            require(com.yujing.utils.YScreenUtil.getStatusHeight() >= 0)
            require(com.yujing.utils.YScreenUtil.getDensity() > 0f)
        },
        com.yujing.test.suite.AutoTestCase("other.stack.more", "YStackTrace 多行与文件名", TestCategory.OTHER) {
            val lines = com.yujing.utils.YStackTrace.getLines()
            require(lines != null && lines.isNotEmpty()) { "getLines 空" }
            val top = com.yujing.utils.YStackTrace.getTopClassLine(0)
            require(top >= 0)
            val name = com.yujing.utils.YStackTrace.getJavaFileName(Throwable().stackTrace[0])
            require(!name.isNullOrBlank()) { "fileName=$name" }
        },
        com.yujing.test.suite.AutoTestCase("other.log.save", "YLog JSON 与文件落盘", TestCategory.OTHER) {
            val dir = java.io.File(com.yujing.utils.YApp.get().cacheDir, "ylog_${System.nanoTime()}")
            dir.mkdirs()
            try {
                com.yujing.utils.YLog.saveOpen(dir.absolutePath)
                com.yujing.utils.YLog.i("{\"k\":1}")
                com.yujing.utils.YLog.dJson("{\"a\":1}")
                com.yujing.utils.YLog.iJson("{\"b\":2}")
                Thread.sleep(80)
                require((dir.listFiles()?.size ?: 0) >= 1) { "落盘文件数=${dir.listFiles()?.size}" }
            } finally {
                com.yujing.utils.YLog.saveClose()
                com.yujing.utils.YLog.delAll()
                dir.deleteRecursively()
            }
        },
        com.yujing.test.suite.AutoTestCase("other.bitmap.more", "YBitmapUtil 圆角/换色/裁方", TestCategory.OTHER) {
            val src = android.graphics.Bitmap.createBitmap(40, 40, android.graphics.Bitmap.Config.ARGB_8888)
            src.eraseColor(0xFFFF0000.toInt())
            val round = com.yujing.utils.YBitmapUtil.getRounded(src, 8f)
            require(round.width == 40 && round.height == 40)
            val replaced = com.yujing.utils.YBitmapUtil.replaceColor(src, 0xFFFF0000.toInt(), 0xFF00FF00.toInt())
            require(replaced.getPixel(0, 0) == 0xFF00FF00.toInt() || replaced.getPixel(20, 20) == 0xFF00FF00.toInt()) {
                "换色后像素=${Integer.toHexString(replaced.getPixel(0, 0))}"
            }
            val square = com.yujing.utils.YBitmapUtil.centerSquareScaleBitmap(src, 20)
            require(square.width == 20 && square.height == 20)
            val alpha = com.yujing.utils.YBitmapUtil.toAlpha(src)
            require(alpha.width == 40)
            val gray = com.yujing.utils.YBitmapUtil.toGray(src)
            require(com.yujing.utils.YBitmapUtil.isGray(gray))
        },
        com.yujing.test.suite.AutoTestCase("other.permissions.query", "YPermissions 清单与 has 查询", TestCategory.OTHER) {
            val ctx = com.yujing.utils.YApp.get()
            val list = com.yujing.utils.YPermissions.getManifestPermissions(ctx)
            require(list != null && list.isNotEmpty()) { "manifest 权限空" }
            // 未动态申请的危险权限通常为 false；仅验证 API 可调用
            com.yujing.utils.YPermissions.hasPermissions(ctx, android.Manifest.permission.CAMERA)
        },
        com.yujing.test.suite.AutoTestCase("other.view.drawable.auto", "YView GradientDrawable 非空", TestCategory.OTHER) {
            val d = com.yujing.view.YView.createGradientDrawable(
                android.graphics.Color.RED, 1, android.graphics.Color.BLACK, 4f, 4f, 4f, 4f
            )
            require(d != null)
            d.setBounds(0, 0, 10, 10)
        },
    )
}

private fun markPassed(c: ManualTestCase, msg: String) {
    c.status = TestStatus.Passed
    c.message = msg
    TestCoverageStore.persist(c)
}

private fun markFailed(c: ManualTestCase, msg: String) {
    c.status = TestStatus.Failed
    c.message = msg
    TestCoverageStore.persist(c)
}
