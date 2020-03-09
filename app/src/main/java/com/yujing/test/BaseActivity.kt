package com.yujing.test

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yujing.utils.YPermissions
import com.yujing.utils.YShow
import com.yujing.utils.YToast
import com.yujing.utils.YUtils
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * activity基类
 * @author yujing 2020年3月5日16:00:44
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YUtils.setFullScreen(this, true)
        YUtils.setImmersive(this, true)
        setContentView(layoutId)
        YPermissions.requestAll(this)
        init()
    }

    abstract val layoutId: Int
    abstract fun init()

    /**
     * 跳转
     */
    fun start(ActivityClass: Class<*>) {
        val intent = Intent(this, ActivityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    }

    /**
     * 延时关闭YShow
     */
    protected open fun delayedShowFinish(time: Long) {
        val sTpe = ScheduledThreadPoolExecutor(1)
        val runnable = Runnable {
            if (isFinishing) sTpe.shutdown()//关闭线程池
            if (isFinishing) sTpe.queue.remove()//移除当前线程且不继续执行此行之后代码
            YShow.finish()
            sTpe.shutdown()//关闭线程池
        }
        sTpe.schedule(runnable, time, TimeUnit.MILLISECONDS);//延迟启动任务
    }

    /**
     * 弹出toast
     */
    @SuppressLint("ShowToast")
    fun show(text: String) {
        YToast.show(this, text)
    }

    override fun finish() {
        super.finish()
        YShow.finish()
    }
}
