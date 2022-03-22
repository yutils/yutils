package com.yujing.view

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.view.size
import com.yujing.utils.YActivityUtil
import com.yujing.utils.YApp
import com.yujing.utils.YDelay
import com.yujing.utils.YScreenUtil
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
/*
用法举例：

//提示,有确定按钮，有取消按钮
YAlertDialogUtils.showMessageCancel("测试","确定删除？删除后不可撤销。"){
    //确定事件
}

//提示,有确定按钮
YAlertDialogUtils.showMessage("测试","确定删除？删除后不可撤销。"){
    //确定事件
}

//提示,无按钮
YAlertDialogUtils.showMessage(null,"确定删除？删除后不可撤销。")


//单选
YAlertDialogUtils.showSingleChoice("请选择一个", listOf<String>("123","456","789","000").toTypedArray(),1){
    //YLog.i("选择了：$it")
}

//多选
val listName: MutableList<String> = ArrayList()
listName.add("项目1")
listName.add("项目2")
listName.add("项目3")
//选中项
val checked = BooleanArray(listName.size) { i -> false }
YAlertDialogUtils.showMultiChoice("绑定卸货位", listName.toTypedArray(), checked) {
    //筛选选中项
    val newList: MutableList<String> = ArrayList()
    for (index in checked.indices) {
        if (checked[index]) newList.add(listName[index])
    }
    //newList
}

//列表
YAlertDialogUtils.showList("请选择一个", listOf("123","456","789","000").toTypedArray()){
    //YLog.i("选择了：$it")
}

//输入框
YAlertDialogUtils.showEdit("测试","请输入内容"){
    //YLog.i("输入了：$it")
}
 */
/**
 * AlertDialog常用弹窗封装
 * 支持：消息，提示，单选，多选，列表，输入
 * @author yujing 2022年3月22日09:25:05
 */
object YAlertDialogUtils {
    //全屏
    var fullScreen = true

    //全屏
    var cancelable = true

    //透明度
    var alpha = 1F

    //宽度
    var width: Int? = null //(YScreenUtil.getScreenWidth() * 0.4).toInt()

    //高度
    var height: Int? = null

    //确定按钮颜色
    var okButtonColor = Color.parseColor("#51A691")

    //取消按钮颜色
    var cancelButtonColor = Color.parseColor("#51A691")

    //文字大小
    var textSize = 18F

    //确定按钮文字
    var okButtonString = "确定"

    //确定按钮文字
    var cancelButtonString = "取消"

    /**
     * 多选弹窗
     */
    fun showMultiChoice(title: String?, itemName: Array<String>, checked: BooleanArray, listener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            )
            .setMultiChoiceItems(itemName, checked) { dialog, which, isChecked ->
            }.setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        okLayoutParams.marginStart = YScreenUtil.dp2px(5F)
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonColor)
        okButton.setTextColor(Color.WHITE)
        okButton.textSize = textSize

        //设置取消按钮
        val cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val cancelLayoutParams = cancelButton.layoutParams as LinearLayout.LayoutParams
        cancelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        cancelLayoutParams.width = 0
        cancelLayoutParams.weight = 1F
        cancelLayoutParams.marginEnd = YScreenUtil.dp2px(5F)
        cancelLayoutParams.gravity = Gravity.CENTER
        cancelButton.layoutParams = cancelLayoutParams
        cancelButton.setBackgroundColor(cancelButtonColor)
        cancelButton.setTextColor(Color.WHITE)
        cancelButton.textSize = textSize

        //设置只显示这俩按钮
        for (i in 0 until (okButton.parent as LinearLayout).size) {
            val view = (okButton.parent as LinearLayout)[i]
            view.visibility = View.GONE
        }
        okButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
    }

    /**
     * 消息框，确定按钮,取消按钮
     */
    fun showMessageCancel(title: String?, message: String?, listener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            ).setView(
                run {
                    val textView = TextView(YApp.get())
                    textView.text = message
                    textView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f))
                    textView.gravity = Gravity.CENTER
                    textView.textSize = textSize
                    textView.setTextColor(Color.BLACK)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textView
                }
            )
            .setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        okLayoutParams.marginStart = YScreenUtil.dp2px(5F)
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonColor)
        okButton.setTextColor(Color.WHITE)
        okButton.textSize = textSize

        //设置取消按钮
        val cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val cancelLayoutParams = cancelButton.layoutParams as LinearLayout.LayoutParams
        cancelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        cancelLayoutParams.width = 0
        cancelLayoutParams.weight = 1F
        cancelLayoutParams.marginEnd = YScreenUtil.dp2px(5F)
        cancelLayoutParams.gravity = Gravity.CENTER
        cancelButton.layoutParams = cancelLayoutParams
        cancelButton.setBackgroundColor(cancelButtonColor)
        cancelButton.setTextColor(Color.WHITE)
        cancelButton.textSize = textSize

        //设置只显示这俩按钮
        for (i in 0 until (okButton.parent as LinearLayout).size) {
            val view = (okButton.parent as LinearLayout)[i]
            view.visibility = View.GONE
        }
        okButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
    }

    /**
     * 消息框，确定按钮
     */
    fun showMessage(title: String?, message: String?, listener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            ).setView(
                run {
                    val textView = TextView(YApp.get())
                    textView.text = message
                    textView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f))
                    textView.gravity = Gravity.CENTER
                    textView.textSize = textSize
                    textView.setTextColor(Color.BLACK)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textView
                }
            )
            .setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        okLayoutParams.marginStart = 0
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonColor)
        okButton.setTextColor(Color.WHITE)
        okButton.textSize = textSize

        //设置只显示这俩按钮
        for (i in 0 until (okButton.parent as LinearLayout).size) {
            val view = (okButton.parent as LinearLayout)[i]
            view.visibility = View.GONE
        }
        okButton.visibility = View.VISIBLE
    }

    /**
     * 消息框，无按钮
     */
    fun showMessage(title: String?, message: String?, time: Int? = 2000) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            )
            .setView(
                run {
                    val textView = TextView(YApp.get())
                    textView.text = message
                    textView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(40f))
                    textView.gravity = Gravity.CENTER
                    textView.textSize = textSize
                    textView.setTextColor(Color.BLACK)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textView
                }
            )
            .setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }

        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }

        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        YDelay.run(time!!) {
            alertDialog.dismiss()
        }
    }


    /**
     * 单选弹窗
     * @param index 单选框默认值：从0开始
     */
    fun showSingleChoice(title: String?, itemName: Array<String>, default: Int = -1, listener: (Int) -> Unit) {
        //-1 是未选择
        val finalWhich = intArrayOf(-1)
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            )
            .setSingleChoiceItems(itemName, default) { dialog, which ->
                finalWhich[0] = which
            }
            .setPositiveButton(okButtonString) { dialog, which ->
                if (finalWhich[0] == -1) {
                    //"未选择")
                    return@setPositiveButton
                }
                listener(finalWhich[0])
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        okLayoutParams.marginStart = 0
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonColor)
        okButton.setTextColor(Color.WHITE)
        okButton.textSize = textSize

        //设置只显示这俩按钮
        for (i in 0 until (okButton.parent as LinearLayout).size) {
            val view = (okButton.parent as LinearLayout)[i]
            view.visibility = View.GONE
        }
        okButton.visibility = View.VISIBLE
    }


    /**
     * 列表框
     */
    fun showList(title: String?, itemName: Array<String>, listener: (Int) -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            )
            .setItems(itemName) { dialog, which ->
                listener(which)
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)
    }


    /**
     * 消息框，确定按钮,取消按钮
     */
    fun showEdit(title: String?, hint: String?, listener: (String) -> Unit) {
        val editText = EditText(YApp.get())
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(
                if (title != null) {
                    val titleTextView = TextView(YApp.get())
                    titleTextView.text = title
                    titleTextView.setPadding(YScreenUtil.dp2px(10f), YScreenUtil.dp2px(20f), YScreenUtil.dp2px(10f), YScreenUtil.dp2px(10f))
                    titleTextView.gravity = Gravity.CENTER
                    titleTextView.textSize = textSize
                    titleTextView.setTextColor(Color.BLACK)
                    titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    titleTextView
                } else null
            ).setView(
                run {
                    val linearLayout = LinearLayout(YApp.get())
                    linearLayout.removeAllViews()
                    linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局
                    linearLayout.setPadding(0, 50, 0, 50)

                    editText.setPadding(10, 10, 10, 10)
                    editText.hint = hint
                    editText.textSize = textSize
                    editText.setTextColor(Color.BLACK)
                    editText.setBackgroundColor(Color.parseColor("#EEEEEE"))
                    linearLayout.addView(editText)
                    linearLayout
                }
            )
            .setPositiveButton(okButtonString) { dialog, which ->
                listener(editText.text.toString())
            }.setNegativeButton(cancelButtonString) { dialog, which ->
            }.setCancelable(cancelable)
            .create()

        //没有标题就不显示
        if (title == null) {
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        //是否全屏
        if (fullScreen) {
            alertDialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            alertDialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            alertDialog.show()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            alertDialog.show()
        }

        //设置透明度
        alertDialog.window?.attributes?.alpha = alpha
        //设置宽度
        width?.let { alertDialog.window?.attributes?.width = it }
        //设置高度
        height?.let { alertDialog.window?.attributes?.height = it }
        //设置h大小宽，高
        //alertDialog.window?.setLayout(width, height)
        //设置Dialog从窗体中间弹出
        alertDialog.window?.setGravity(Gravity.CENTER)

        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        okLayoutParams.marginStart = YScreenUtil.dp2px(5F)
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonColor)
        okButton.setTextColor(Color.WHITE)
        okButton.textSize = textSize

        //设置取消按钮
        val cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val cancelLayoutParams = cancelButton.layoutParams as LinearLayout.LayoutParams
        cancelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        cancelLayoutParams.width = 0
        cancelLayoutParams.weight = 1F
        cancelLayoutParams.marginEnd = YScreenUtil.dp2px(5F)
        cancelLayoutParams.gravity = Gravity.CENTER
        cancelButton.layoutParams = cancelLayoutParams
        cancelButton.setBackgroundColor(cancelButtonColor)
        cancelButton.setTextColor(Color.WHITE)
        cancelButton.textSize = textSize

        //设置只显示这俩按钮
        for (i in 0 until (okButton.parent as LinearLayout).size) {
            val view = (okButton.parent as LinearLayout)[i]
            view.visibility = View.GONE
        }
        okButton.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
    }
}