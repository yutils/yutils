package com.yutils.view.utils

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.yujing.utils.YApp
import com.yujing.utils.YScreenUtil.dp2px
import kotlin.random.Random

/**
 * 动态创建view
 */
/*
val textView1 = Create.textView(binding.ll)
Create.button(binding.wll, "按钮") {}
val editText = Create.editText(binding.wll, "输入")
val checkBox2 = Create.checkBox(binding.wll, "配置",true)
 */
@Suppress("MemberVisibilityCanBePrivate")
class Create {
    companion object {
        //获取随机颜色0-255，Random.nextInt(min, max)包含min，不包含max，透明度0-255，最低浓度，最高浓度
        fun randomColor(alpha: Int = 255, min: Int = 0, max: Int = 256) = Color.argb(alpha, Random.nextInt(min, max), Random.nextInt(min, max), Random.nextInt(min, max))

        //动态创建TextView
        fun textView(value: String? = null, color: Int = Color.parseColor("#FF102030")): TextView {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.apply {
                marginStart = dp2px(5f)
                marginEnd = dp2px(5f)
                topMargin = dp2px(5f)
                bottomMargin = dp2px(5f)
            }
            val view = TextView(YApp.get()).apply {
                value?.let { text = it }
                setTextColor(color)
                //height = dp2px(30F)
                gravity = Gravity.CENTER
                this.layoutParams = layoutParams
            }
            return view
        }

        //动态创建TextView
        fun textView(linearLayout: ViewGroup?, value: String? = null, color: Int = Color.parseColor("#FF102030")): TextView {
            val view = textView(value, color)
            linearLayout?.addView(view)
            return view
        }

        //动态创建Button,之前颜色：Color.parseColor("#FF169CFA")
        fun button(name: String? = "按钮", color: Int = randomColor(255, 50, 256), listener: View.OnClickListener): Button {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val view = Button(YApp.get()).apply {
                isAllCaps = false//不自动变大写
                name?.let { text = it }
                this.layoutParams = layoutParams
                setOnClickListener(listener)
                setTextColor(Color.parseColor("#FFFFFFFF"))
                backgroundTintList = ColorStateList(arrayOf(IntArray(0)), intArrayOf(color))
            }
            return view
        }

        //动态创建Button,之前颜色：Color.parseColor("#FF169CFA")
        fun button(linearLayout: ViewGroup?, name: String = "按钮", color: Int = randomColor(255, 50, 256), listener: View.OnClickListener): Button {
            val view = button(name, color, listener)
            linearLayout?.addView(view)
            return view
        }

        //动态创建EditText
        fun editText(value: String = "", hint: String = "在这儿输入内容"): EditText {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.apply {
                marginStart = dp2px(5f)
                marginEnd = dp2px(5f)
                topMargin = dp2px(5f)
                bottomMargin = dp2px(5f)
            }
            val view = EditText(YApp.get()).apply {
                setText(value)
                this.hint = hint
                this.layoutParams = layoutParams
            }
            return view
        }

        //动态创建EditText
        fun editText(linearLayout: ViewGroup?, value: String = "", hint: String = "在这儿输入内容"): EditText {
            val view = editText(value, hint)
            linearLayout?.addView(view)
            return view
        }

        //动态创建imageView
        fun imageView(bitmap: Bitmap? = null, widthDp: Float = 300f, heightDp: Float = 300f): ImageView {
            val layoutParams = LinearLayout.LayoutParams(dp2px(widthDp), dp2px(heightDp))
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            val view = ImageView(YApp.get())
            view.layoutParams = layoutParams
            bitmap?.let { view.setImageBitmap(it) }
            return view
        }

        //动态创建imageView
        fun imageView(linearLayout: ViewGroup?, bitmap: Bitmap? = null, widthDp: Float = 300f, heightDp: Float = 300f): ImageView {
            val view = imageView(bitmap, widthDp, heightDp)
            linearLayout?.addView(view)
            return view
        }

        //动态创建checkBox
        fun checkBox(name: String = "按钮", isChecked: Boolean = false, listener: CompoundButton.OnCheckedChangeListener? = null): CheckBox {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.apply {
                marginStart = dp2px(5f)
                marginEnd = dp2px(5f)
                topMargin = dp2px(5f)
                bottomMargin = dp2px(5f)
            }
            val view = CheckBox(YApp.get()).apply {
                text = name
                this.isChecked = isChecked
                gravity = Gravity.CENTER
                setOnCheckedChangeListener(listener)
                this.layoutParams = layoutParams
                setTextColor(randomColor(255,0,200))
            }
            return view
        }

        //动态创建checkBox
        fun checkBox(linearLayout: ViewGroup?, name: String = "按钮", isChecked: Boolean = false, listener: CompoundButton.OnCheckedChangeListener? = null): CheckBox {
            val view = checkBox(name, isChecked, listener)
            linearLayout?.addView(view)
            return view
        }

        //动态创建linearLayout纵向布局
        fun linearLayoutV(color: Int = randomColor(16)): ViewGroup {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
            layoutParams.apply {
                marginStart = dp2px(5f)
                marginEnd = dp2px(5f)
                topMargin = dp2px(5f)
                bottomMargin = dp2px(5f)
            }
            val view = LinearLayout(YApp.get())
            view.apply {
                removeAllViews()
                this.layoutParams = layoutParams
                orientation = LinearLayout.VERTICAL //设置纵向布局
                setPadding(dp2px(5f), dp2px(5f), dp2px(5f), dp2px(5f))
                minimumHeight = dp2px(0f) //最小高度
                minimumWidth = dp2px(0f) //最小宽度
                weightSum = 1F
                setBackgroundColor(color)
            }
            return view
        }

        //动态创建linearLayout纵向布局
        fun linearLayoutV(linearLayout: ViewGroup?, color: Int = randomColor(16)): ViewGroup {
            val view = linearLayoutV(color)
            linearLayout?.addView(view)
            return view
        }

        //动态创建linearLayout横向布局
        fun linearLayoutH(color: Int = randomColor(16)): ViewGroup {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)
            layoutParams.apply {
                marginStart = dp2px(5f)
                marginEnd = dp2px(5f)
                topMargin = dp2px(5f)
                bottomMargin = dp2px(5f)
            }
            val view = LinearLayout(YApp.get())
            view.apply {
                removeAllViews()
                this.layoutParams = layoutParams
                orientation = LinearLayout.HORIZONTAL //设置纵向布局
                setPadding(dp2px(5f), dp2px(5f), dp2px(5f), dp2px(5f))
                minimumHeight = dp2px(0f) //最小高度
                minimumWidth = dp2px(0f) //最小宽度
                weightSum = 1F
                setBackgroundColor(color)
            }
            return view
        }

        //动态创建linearLayout横向布局
        fun linearLayoutH(linearLayout: ViewGroup?, color: Int = randomColor(16)): ViewGroup {
            val view = linearLayoutH(color)
            linearLayout?.addView(view)
            return view
        }

        //添加空行
        fun space(linearLayout: ViewGroup?): Space {
            val view = Space(linearLayout?.context)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            view.layoutParams = layoutParams
            linearLayout?.addView(view)
            return view
        }
    }
}