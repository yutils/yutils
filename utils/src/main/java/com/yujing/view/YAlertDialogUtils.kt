package com.yujing.view

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.yujing.utils.*
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
/*
用法举例：

//提示,有确定按钮，有取消按钮
YAlertDialogUtils().showMessageCancel("测试","确定删除？删除后不可撤销。"){
    //确定事件
}

//提示,有确定按钮
YAlertDialogUtils().showMessage("测试","确定删除？删除后不可撤销。"){
    //确定事件
}

//提示,无按钮
YAlertDialogUtils().showMessage(null,"确定删除？删除后不可撤销。")


//单选
YAlertDialogUtils().showSingleChoice("请选择一个", listOf("123","456","789","000").toTypedArray(),1){
    //YLog.i("选择了：$it")
}

//多选
val listName: MutableList<String> = ArrayList()
listName.add("项目1")
listName.add("项目2")
listName.add("项目3")
val checked = BooleanArray(listName.size) { i -> false } //默认选中项，最终选中项
YAlertDialogUtils().showMultiChoice("请选择", listName.toTypedArray(), checked) {
    //筛选选中项
    val newList: MutableList<String> = ArrayList()
    for (index in checked.indices) {
        if (checked[index]) newList.add(listName[index])
    }
    //newList
}

//列表
YAlertDialogUtils().showList("请选择一个", listOf("123","456","789","000").toTypedArray()){
    //YLog.i("选择了：$it")
}

//输入框
YAlertDialogUtils().showEdit("测试","请输入内容"){
    //YLog.i("输入了：$it")
}
 */
/**
 * AlertDialog常用弹窗封装
 * 支持：消息，提示，单选，多选，列表，输入
 * @author yujing 2022年3月22日09:25:05
 */
class YAlertDialogUtils {
    //全屏
    var fullScreen = true

    //运行关闭
    var cancelable = true

    //透明度
    var alpha = 1F

    //宽度
    var width: Int? = null //(YScreenUtil.getScreenWidth() * 0.4).toInt()

    //高度
    var height: Int? = null

    //确定按钮 颜色
    var okButtonBackgroundColor = Color.parseColor("#51A691")
    var okButtonTextColor = Color.WHITE

    //取消按钮 颜色
    var cancelButtonBackgroundColor = Color.parseColor("#51A691")
    var cancelButtonTextColor = Color.WHITE

    //文字大小
    var titleTextSize = 18F
    var contentTextSize = 14F
    var buttonTextSize = 16F

    //确定按钮文字
    var okButtonString = "确定"

    //确定按钮文字
    var cancelButtonString = "取消"

    //title 文字对齐方式
    var titleTextViewGravity = Gravity.CENTER

    //title 文字颜色
    var titleTextColor = Color.BLACK

    //正文 文字对齐方式
    var contentTextViewGravity = Gravity.CENTER

    //正文 文字颜色
    var contentTextColor = Color.BLACK

    //标题间距
    var titlePaddingLeft = YScreenUtil.dp2px(10f)
    var titlePaddingTop = YScreenUtil.dp2px(10f)
    var titlePaddingRight = YScreenUtil.dp2px(10f)
    var titlePaddingBottom = YScreenUtil.dp2px(8f)

    //正文间距
    var contentPaddingLeft = YScreenUtil.dp2px(10f)
    var contentPaddingTop = YScreenUtil.dp2px(20f)
    var contentPaddingRight = YScreenUtil.dp2px(10f)
    var contentPaddingBottom = YScreenUtil.dp2px(20f)

    //隔断线
    var titleImageViewColor = Color.parseColor("#5051A691")
    var contentImageViewColor = Color.parseColor("#5051A691")
    var titleImageViewHeight = 1 //px
    var contentImageViewHeight = 1 //px

    /**
     * 创建标题view
     */
    fun createTitleView(title: String?): LinearLayout {
        val linearLayout = LinearLayout(YApp.get())
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayout.removeAllViews()
        linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局

        val titleTextView = TextView(YApp.get())
        titleTextView.text = title
        titleTextView.setPadding(titlePaddingLeft, titlePaddingTop, titlePaddingRight, titlePaddingBottom)
        titleTextView.gravity = titleTextViewGravity
        titleTextView.textSize = titleTextSize
        titleTextView.setTextColor(titleTextColor)
        titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        //隔断线
        val titleImageView = ImageView(YApp.get())
        titleImageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleImageViewHeight)
        titleImageView.setBackgroundColor(titleImageViewColor)

        linearLayout.addView(titleTextView)
        linearLayout.addView(titleImageView)
        return linearLayout
    }

    /**
     * 创建正文view
     */
    fun createContentView(message: String?): ScrollView {
        val scrollView = ScrollView(YApp.get())
        scrollView.removeAllViews()
        scrollView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)

        val linearLayout = LinearLayout(YApp.get())
        linearLayout.removeAllViews()
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局

        val textView = TextView(YApp.get())
        textView.text = message
        textView.setPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom)
        textView.gravity = contentTextViewGravity
        textView.textSize = contentTextSize
        textView.setTextColor(contentTextColor)
        if (contentTextViewGravity == Gravity.CENTER)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        //隔断线
        val contentImageView = ImageView(YApp.get())
        contentImageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, contentImageViewHeight)
        contentImageView.setBackgroundColor(contentImageViewColor)

        linearLayout.addView(textView)
        linearLayout.addView(contentImageView)
        scrollView.addView(linearLayout)

        return scrollView
    }

    //设置弹窗风格
    fun setStyle(alertDialog: AlertDialog, title: String?) {
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

    //设置按钮风格
    fun setButton(alertDialog: AlertDialog, showCancel: Boolean = false) {
        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = okButton.layoutParams as LinearLayout.LayoutParams
        okLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        okLayoutParams.width = 0
        okLayoutParams.weight = 1F
        if (showCancel) okLayoutParams.marginStart = YScreenUtil.dp2px(5F)
        okLayoutParams.gravity = Gravity.CENTER
        okButton.layoutParams = okLayoutParams
        okButton.setBackgroundColor(okButtonBackgroundColor)
        okButton.setTextColor(okButtonTextColor)
        okButton.textSize = buttonTextSize

        //设置取消按钮
        val cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val cancelLayoutParams = cancelButton.layoutParams as LinearLayout.LayoutParams
        cancelLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        cancelLayoutParams.width = 0
        cancelLayoutParams.weight = 1F
        if (showCancel) cancelLayoutParams.marginEnd = YScreenUtil.dp2px(5F)
        cancelLayoutParams.gravity = Gravity.CENTER
        cancelButton.layoutParams = cancelLayoutParams
        cancelButton.setBackgroundColor(cancelButtonBackgroundColor)
        cancelButton.setTextColor(cancelButtonTextColor)
        cancelButton.textSize = buttonTextSize
        //只显示需要按钮
        val parentView = okButton.parent as LinearLayout
        parentView.removeAllViews()
        if (showCancel) parentView.addView(cancelButton)
        parentView.addView(okButton)
    }

    /**
     * 消息框，确定按钮,取消按钮
     */
    fun showMessageCancel(title: String?, message: String?, listener: () -> Unit) {
        showMessageCancel(title, message, listener) {}
    }

    /**
     * 消息框，确定按钮,取消按钮
     */
    fun showMessageCancel(title: String?, message: String?, listener: () -> Unit, cancelListener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
                cancelListener()
            }.setCancelable(cancelable)
            .create()

        setStyle(alertDialog, title)
        setButton(alertDialog, true)
    }

    /**
     * 消息框，确定按钮
     */
    fun showMessage(title: String?, message: String?, listener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setCancelable(cancelable)
            .create()
        setStyle(alertDialog, title)
        setButton(alertDialog, false)
    }

    /**
     * 消息框，无按钮
     */
    fun showMessage(title: String?, message: String?, time: Int? = 2000) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setCancelable(cancelable)
            .create()

        setStyle(alertDialog, title)

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
            .setCustomTitle(if (title != null) createTitleView(title) else null)
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

        setStyle(alertDialog, title)
        setButton(alertDialog, false)
    }


    /**
     * 列表框
     */
    fun showList(title: String?, itemName: Array<String>, listener: (Int) -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setItems(itemName) { dialog, which ->
                listener(which)
            }.setCancelable(cancelable)
            .create()

        setStyle(alertDialog, title)
    }

    /**
     * 消息框，确定按钮,取消按钮
     */
    fun showEdit(title: String?, hint: String?, listener: (String) -> Unit) {
        showEdit(title, hint, listener) {}
    }

    fun showEdit(title: String?, hint: String?, listener: (String) -> Unit, cancelListener: () -> Unit) {
        val editText = EditText(YApp.get())
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(
                run {
                    val linearLayout = LinearLayout(YApp.get())
                    linearLayout.removeAllViews()
                    linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局
                    linearLayout.setPadding(0, 50, 0, 50)
                    //输入框设置
                    editText.setPadding(10, 20, 10, 20)
                    editText.hint = hint
                    editText.textSize = contentTextSize
                    editText.setTextColor(Color.BLACK)
                    editText.setBackgroundColor(Color.parseColor("#EEEEEE"))
                    linearLayout.addView(editText)
                    linearLayout
                }
            )
            .setPositiveButton(okButtonString) { dialog, which ->
                listener(editText.text.toString())
            }.setNegativeButton(cancelButtonString) { dialog, which ->
                cancelListener()
            }.setCancelable(cancelable)
            .create()

        setStyle(alertDialog, title)
        setButton(alertDialog, true)
    }

    /**
     * 多选弹窗
     */
    fun showMultiChoice(title: String?, itemName: Array<String>, checked: BooleanArray, listener: () -> Unit) {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setMultiChoiceItems(itemName, checked) { dialog, which, isChecked ->
            }.setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
            }.setCancelable(cancelable)
            .create()

        setStyle(alertDialog, title)
        setButton(alertDialog, true)
    }
}