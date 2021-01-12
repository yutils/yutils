package com.yutils.view.utils

import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.yujing.test.App
import com.yujing.utils.YScreenUtil.dp2px

/**
 * 动态创建view
 */
@Suppress("MemberVisibilityCanBePrivate")
class Create {
    companion object {
        //动态创建Button
        fun button(name: String = "按钮", listener: View.OnClickListener): Button {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val view = Button(App.get())
            view.isAllCaps = false//不自动变大写
            name.let { view.text = it }
            view.layoutParams = layoutParams
            view.setOnClickListener(listener)
            return view
        }

        //动态创建Button
        fun button(
            linearLayout: ViewGroup?, name: String = "按钮",
            listener: View.OnClickListener
        ): Button {
            val view = button(name, listener)
            linearLayout?.addView(view)
            return view
        }

        //动态创建TextView
        fun textView(value: String? = null): TextView {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val view = TextView(App.get())
            value.let { view.text = it }
            view.layoutParams = layoutParams
            return view
        }

        //动态创建TextView
        fun textView(linearLayout: ViewGroup?, value: String? = null): TextView {
            val view = textView(value)
            linearLayout?.addView(view)
            return view
        }

        //动态创建EditText
        fun editText(value: String = ""): EditText {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val view = EditText(App.get())
            view.setText(value)
            view.hint = "在这儿输入内容"
            view.layoutParams = layoutParams
            return view
        }


        //动态创建EditText
        fun editText(linearLayout: ViewGroup?, value: String = ""): EditText {
            val view = editText(value)
            linearLayout?.addView(view)
            return view
        }

        //动态创建EditText
        fun imageView(bitmap: Bitmap? = null): ImageView {
            val layoutParams = LinearLayout.LayoutParams(dp2px(300f), dp2px(300f))
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            val view = ImageView(App.get())
            view.layoutParams = layoutParams
            bitmap.let { view.setImageBitmap(it) }
            return view
        }

        //动态创建EditText
        fun imageView(linearLayout: ViewGroup?, bitmap: Bitmap? = null): ImageView {
            val view = imageView(bitmap)
            linearLayout?.addView(view)
            return view
        }

        //添加空行
        fun space(linearLayout: ViewGroup?): Space {
            val view = Space(linearLayout?.context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view.layoutParams = layoutParams
            linearLayout?.addView(view)
            return view
        }
    }
}