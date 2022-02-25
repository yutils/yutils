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
 * @author 余静 2020年9月7日21:40:20
 */
/*
用法：
//kotlin
class AboutActivity : KBaseActivity<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {
        binding.include.ivBack.setOnClickListener { finish() }
        binding.include.tvTitle.text = "关于我们"
    }
}
//java
public class OldActivity extends KBaseActivity<Activity1101Binding> {
    public OldActivity() {
        super(R.layout.activity_1101);
    }
    @Override
    protected void init() { }
}
 */
abstract class KBaseActivity<B : ViewDataBinding>(layout: Int?) : YBaseActivity<B>(layout) {
    override fun initAfter() {
        YUtils.setFullScreen(this, true)
        YUtils.setImmersive(this, true)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }
}