package com.yujing.test


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yujing.utils.YToast

/**
 * fragment基类
 * @author yujing 2020年3月5日16:00:27
 */
abstract class BaseFragment : Fragment() {
    private var rootView: View? = null// 缓存Fragment view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(layoutId, container, false)
        init()
        return rootView
    }

    abstract val layoutId: Int
    abstract fun init()


    /**
     * 跳转
     */
    fun start(ActivityClass: Class<*>) {
        val intent = Intent(activity, ActivityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    }


    /**
     * 显示toast
     */
    protected open fun show(str: String?) {
        if (str == null) return
        YToast.showLong(activity, str)
    }

}
