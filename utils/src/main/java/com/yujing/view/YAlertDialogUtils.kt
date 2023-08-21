package com.yujing.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.TextWatcher
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
YAlertDialogUtils().apply {
    titleTextSize = 20F
    contentTextSize = 18F
    buttonTextSize = 18F
    width = (YScreenUtil.getScreenWidth() * 0.45).toInt()
    contentPaddingTop = YScreenUtil.dp2px(40f)
    contentPaddingBottom = YScreenUtil.dp2px(40f)
    okButtonString = "确定"
    cancelButtonString = "取消"
    val content = """
    |您正在执行一项操作
    |
    |执行后将无法修改，是否继续？
    """.trimMargin()
    //显示消息，包含取消按键
    showMessageCancel("这是标题", content,{
        //确定事件
    },{})
}

//提示,有确定按钮
YAlertDialogUtils().showMessage("测试","确定删除？删除后不可撤销。"){
    //确定事件
}

//提示,有确定按钮，有取消按钮
YAlertDialogUtils().showMessageCancel("测试","确定删除？删除后不可撤销。",{
    //确定事件
},{})

//提示,无按钮，标题为null时不显示标题
YAlertDialogUtils().showMessage(null,"确定删除？删除后不可撤销。")


//单选
YAlertDialogUtils().showSingleChoice("请选择一个", listOf("123","456","789","000").toTypedArray(),1){
    //YLog.i("选择了：$it")
}

//多选
val listName: MutableList<String> = ArrayList<String>().apply {
    add("项目1")
    add("项目2")
    add("项目3")
    add("项目4")
    add("项目5")
    add("项目6")
}
val checked = BooleanArray(listName.size) { i -> false } //默认选中项，最终选中项
YAlertDialogUtils().showMultiChoice("请选择", listName.toTypedArray(), checked) {
    //筛选选中项
    val newList: MutableList<String> = ArrayList()
    for (index in checked.indices) {
        if (checked[index]) newList.add(listName[index])
    }
    textView1.text = "您选择了：${YJson.toJson(newList)}"
}

//列表
YAlertDialogUtils().showList("请选择一个", listOf("123","456","789","000").toTypedArray()){
    //YLog.i("选择了：$it")
}

//输入框
YAlertDialogUtils().showEdit("测试",text="123",hint="请输入内容",{
    //YLog.i("输入了：$it")
},{})

//显示后再修改按钮颜色
YAlertDialogUtils().apply { fullScreen = false }.showMessageCancel(null, "是否离开当前页面？", {
    finish()
}, {}).apply {
    getButton(AlertDialog.BUTTON_POSITIVE).apply {
        text = "是"
        setTextColor(Color.parseColor("#000000"))
        YView.setButtonBackgroundTint(this, Color.parseColor("#2045D0A0"), Color.parseColor("#00000000"))
    }
    getButton(AlertDialog.BUTTON_NEGATIVE).apply {
        text = "否"
        setTextColor(Color.parseColor("#000000"))
        YView.setButtonBackgroundTint(this, Color.parseColor("#2045D0A0"), Color.parseColor("#00000000"))
    }
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

    //允许关闭
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
    var okButtonString: CharSequence = "确定"

    //确定按钮文字
    var cancelButtonString: CharSequence = "取消"

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
    var contentPaddingTop = YScreenUtil.dp2px(30f)
    var contentPaddingRight = YScreenUtil.dp2px(10f)
    var contentPaddingBottom = YScreenUtil.dp2px(30f)

    //隔断线
    var titleImageViewColor = Color.parseColor("#5051A691")
    var contentImageViewColor = Color.parseColor("#5051A691")
    var titleImageViewHeight = 1 //px
    var contentImageViewHeight = 1 //px

    /**
     * 创建标题view
     */
    fun createTitleView(title: CharSequence?): LinearLayout {
        val linearLayout = LinearLayout(YApp.get()).apply {
            removeAllViews()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL //设置纵向布局
        }
        val titleTextView = TextView(YApp.get()).apply {
            text = title
            setPadding(titlePaddingLeft, titlePaddingTop, titlePaddingRight, titlePaddingBottom)
            gravity = titleTextViewGravity
            textSize = titleTextSize
            setTextColor(titleTextColor)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
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
    fun createContentView(message: CharSequence?): ScrollView {
        val scrollView = ScrollView(YApp.get()).apply {
            removeAllViews()
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }
        val linearLayout = LinearLayout(YApp.get()).apply {
            removeAllViews()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL //设置纵向布局
        }
        val textView = TextView(YApp.get()).apply {
            text = message
            setPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom)
            gravity = contentTextViewGravity
            textSize = contentTextSize
            setTextColor(contentTextColor)
            setTextIsSelectable(true)
        }
        if (contentTextViewGravity == Gravity.CENTER)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        //隔断线
        val contentImageView = ImageView(YApp.get()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, contentImageViewHeight)
            setBackgroundColor(contentImageViewColor)
        }
        linearLayout.addView(textView)
        linearLayout.addView(contentImageView)
        scrollView.addView(linearLayout)

        return scrollView
    }

    /**
     * 设置弹窗风格
     */
    fun setStyleAndShow(alertDialog: AlertDialog, title: CharSequence?) {
        alertDialog.apply {
            //没有标题就不显示
            if (title == null) requestWindowFeature(Window.FEATURE_NO_TITLE)
            //是否全屏
            if (fullScreen) {
                window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                show()
                window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            } else {
                show()
            }
            //设置透明度
            window?.attributes?.alpha = alpha
            //设置宽度
            width?.let { window?.attributes?.width = it }
            //设置高度
            height?.let { window?.attributes?.height = it }
            //设置h大小宽，高
            //alertDialog.window?.setLayout(width, height)
            //设置Dialog从窗体中间弹出
            window?.setGravity(Gravity.CENTER)
        }
    }

    /**
     * 设置按钮风格
     */
    fun setButton(alertDialog: AlertDialog, showCancel: Boolean = false) {
        //设置确定按钮
        val okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val okLayoutParams = (okButton.layoutParams as LinearLayout.LayoutParams).apply {
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            width = 0
            weight = 1F
            if (showCancel) marginStart = YScreenUtil.dp2px(5F)
            gravity = Gravity.CENTER
        }
        okButton.layoutParams = okLayoutParams
        YView.setPressBackgroundColor(okButton, okButtonBackgroundColor.xor(Color.parseColor("#60000000")), okButtonBackgroundColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //相当于  color^0x60000000
            val colorStateList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                intArrayOf(okButtonBackgroundColor.xor(Color.parseColor("#60000000")), okButtonBackgroundColor)
            )
            okButton.backgroundTintList = colorStateList
        } else {
            okButton.setBackgroundColor(okButtonBackgroundColor)
        }
        okButton.setTextColor(okButtonTextColor)
        okButton.textSize = buttonTextSize

        //设置取消按钮
        val cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val cancelLayoutParams = (cancelButton.layoutParams as LinearLayout.LayoutParams).apply {
            height = LinearLayout.LayoutParams.WRAP_CONTENT
            width = 0
            weight = 1F
        }
        if (showCancel) cancelLayoutParams.marginEnd = YScreenUtil.dp2px(5F)
        cancelLayoutParams.gravity = Gravity.CENTER
        cancelButton.layoutParams = cancelLayoutParams
        YView.setPressBackgroundColor(cancelButton, cancelButtonBackgroundColor.xor(Color.parseColor("#60000000")), cancelButtonBackgroundColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //相当于  color^0x60000000
            val colorStateList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                intArrayOf(cancelButtonBackgroundColor.xor(Color.parseColor("#60000000")), cancelButtonBackgroundColor)
            )
            cancelButton.backgroundTintList = colorStateList
        } else {
            cancelButton.setBackgroundColor(cancelButtonBackgroundColor)
        }
        cancelButton.setTextColor(cancelButtonTextColor)
        cancelButton.textSize = buttonTextSize
        //只显示需要按钮
        val parentView = okButton.parent as LinearLayout
        parentView.removeAllViews()
        if (showCancel) parentView.addView(cancelButton)
        parentView.addView(okButton)
    }

    /**
     * 消息框，无按钮
     */
    fun showMessage(title: CharSequence?, message: CharSequence?, time: Int? = 2000): AlertDialog {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setCancelable(cancelable)
            .create()

        setStyleAndShow(alertDialog, title)
        YDelay.run(time!!) {
            alertDialog.dismiss()
        }
        return alertDialog
    }

    /**
     * 消息框，确定按钮
     */
    fun showMessage(title: CharSequence?, message: CharSequence?, listener: (() -> Unit)? = null): AlertDialog {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setPositiveButton(okButtonString) { dialog, which ->
                listener?.invoke()
            }.setCancelable(cancelable)
            .create()
        setStyleAndShow(alertDialog, title)
        setButton(alertDialog, false)
        return alertDialog
    }

    /**
     * 消息框，确定按钮、取消按钮
     */
    fun showMessageCancel(title: CharSequence?, message: CharSequence?, listener: (() -> Unit)? = null): AlertDialog {
        return showMessageCancel(title, message, listener, null)
    }

    /**
     * 消息框，确定按钮、取消按钮
     */
    fun showMessageCancel(title: CharSequence?, message: CharSequence?, listener: (() -> Unit)? = null, cancelListener: (() -> Unit)? = null): AlertDialog {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(createContentView(message))
            .setPositiveButton(okButtonString) { dialog, which ->
                listener?.invoke()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
                cancelListener?.invoke()
            }.setCancelable(cancelable)
            .create()

        setStyleAndShow(alertDialog, title)
        setButton(alertDialog, true)
        return alertDialog
    }

    /**
     * 单选弹窗，确定按钮
     * @param index 单选框默认值：从0开始
     */
    fun showSingleChoice(title: CharSequence?, itemName: Array<String?>, default: Int = -1, listener: (Int) -> Unit): AlertDialog {
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

        setStyleAndShow(alertDialog, title)
        setButton(alertDialog, false)
        return alertDialog
    }


    /**
     * 列表框，无按钮
     */
    fun showList(title: CharSequence?, itemName: Array<String?>, listener: (Int) -> Unit): AlertDialog {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setItems(itemName) { dialog, which ->
                listener(which)
            }.setCancelable(cancelable)
            .create()
        setStyleAndShow(alertDialog, title)
        return alertDialog
    }

    /**
     * 输入框，确定按钮、取消按钮
     */
    fun showEdit(title: CharSequence? = "请输入内容", text: CharSequence? = null, hint: String? = "请输入内容", listener: (String) -> Unit): AlertDialog {
        return showEdit(title, text, hint, null, listener = listener, null)
    }

    /**
     * 输入框，确定按钮、取消按钮
     */
    fun showEdit(title: CharSequence? = "请输入内容", text: CharSequence? = null, hint: String? = "请输入内容", textWatcher: TextWatcher? = null, listener: (String) -> Unit, cancelListener: (() -> Unit)? = null): AlertDialog {
        val editText = EditText(YApp.get())
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setView(
                run {
                    val linearLayout = LinearLayout(YApp.get()).apply {
                        removeAllViews()
                        orientation = LinearLayout.VERTICAL //设置纵向布局
                        setPadding(0, 50, 0, 50)
                    }
                    //输入框设置
                    editText.apply {
                        setPadding(10, 20, 10, 20)
                        text?.let {
                            setText(it)
                            setSelection(it.length)
                        }
                        this.hint = hint
                        textSize = contentTextSize
                        setTextColor(Color.BLACK)
                        setBackgroundColor(Color.parseColor("#EEEEEE"))
                        textWatcher?.let { addTextChangedListener(textWatcher) }
                    }
                    linearLayout.addView(editText)
                    linearLayout
                }
            )
            .setPositiveButton(okButtonString) { dialog, which ->
                listener.invoke(editText.text.toString())
            }.setNegativeButton(cancelButtonString) { dialog, which ->
                cancelListener?.invoke()
            }.setCancelable(cancelable)
            .create()

        setStyleAndShow(alertDialog, title)
        setButton(alertDialog, true)
        return alertDialog
    }

    /**
     * 多选弹窗，确定按钮、取消按钮
     */
    fun showMultiChoice(title: CharSequence?, itemName: Array<String?>, checked: BooleanArray, listener: () -> Unit): AlertDialog {
        return showMultiChoice(title, itemName, checked, listener, null)
    }

    /**
     * 多选弹窗，确定按钮、取消按钮
     */
    fun showMultiChoice(title: CharSequence?, itemName: Array<String?>, checked: BooleanArray, listener: () -> Unit, cancelListener: (() -> Unit)? = null): AlertDialog {
        //创建alertDialog
        val alertDialog = AlertDialog.Builder(YActivityUtil.getCurrentActivity())
            .setCustomTitle(if (title != null) createTitleView(title) else null)
            .setMultiChoiceItems(itemName, checked) { dialog, which, isChecked ->
            }.setPositiveButton(okButtonString) { dialog, which ->
                listener()
            }.setNegativeButton(cancelButtonString) { dialog, which ->
                cancelListener?.invoke()
            }.setCancelable(cancelable)
            .create()

        setStyleAndShow(alertDialog, title)
        setButton(alertDialog, true)
        return alertDialog
    }
}