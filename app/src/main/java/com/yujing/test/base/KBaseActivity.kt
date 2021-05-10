@file:Suppress("MemberVisibilityCanBePrivate")

package com.yujing.test.base

import android.view.KeyEvent
import androidx.databinding.ViewDataBinding
import com.yujing.base.YBaseActivity
import com.yujing.utils.*

/**
 * 基础activity
 *
 * @param <B> ViewDataBinding
 * @author yujing 2020年9月7日21:40:20
 */
/*
用法：
//kotlin
class AboutActivity : YBaseActivity<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {
        binding.include.ivBack.setOnClickListener { finish() }
        binding.include.tvTitle.text = "关于我们"
    }
}
//java
public class OldActivity extends YBaseActivity<Activity1101Binding> {
    public OldActivity() {
        super(R.layout.activity_1101);
    }
    @Override
    protected void init() { }
}
 */
abstract class KBaseActivity<B : ViewDataBinding>(layout: Int?) : YBaseActivity<B>(layout) {
    lateinit var yPermissions: YPermissions

    override fun initAfter() {
        YUtils.setFullScreen(this, true)
        YUtils.setImmersive(this, true)
        yPermissions = YPermissions(this)
        yPermissions.requestAll()
        yPermissions.setSuccessListener { /*请求权限成功*/ }
        yPermissions.setFailListener { list -> /* show("部分权限未赋予")*/ }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        yPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            YUtils.setFullScreen(this, true)
            YUtils.setImmersive(this, true)
        }
    }

    var oldSpeak = ""
    fun speak(str: String) {
        var s = str
        s = s.replace("皮重不合规", "皮众不合规")
        YTts.getInstance(YApp.get()).speak(s)
        YLog.i("speak", s,1)
        oldSpeak = s
    }

    fun speakQueue(str: String) {
        var s = str
        s = s.replace("皮重不合规", "皮众不合规")
        YTts.getInstance(YApp.get()).speakQueue(s)
        YLog.i("speak", s,1)
        oldSpeak = s
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }
}