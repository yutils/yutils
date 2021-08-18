@file:Suppress("MemberVisibilityCanBePrivate")

package com.yujing.test.base

import androidx.databinding.ViewDataBinding
import com.yujing.base.YBaseFragment
import com.yujing.utils.YApp
import com.yujing.utils.YTts

/**
 * 基础aFragment
 *
 * @param <B> ViewDataBinding
 * @author 余静 2020年9月7日21:40:20
 */
/* 用法举例
//kotlin
class AboutActivity : YBaseFragment<ActivityAboutBinding>(R.layout.activity_about) {
    override fun init() {}
}
//java
public class OldFragment extends YBaseFragment<Activity1101Binding> {
    public OldFragment() {
        super(R.layout.activity_1101);
    }
    @Override
    protected void init() {

    }
}
 */
abstract class KBaseFragment<B : ViewDataBinding>(layout: Int?) : YBaseFragment<B>(layout) {
    override fun speak(s: String?) {
        YTts.getInstance(YApp.get()).speak(s)
    }

    fun speakQueue(s: String) {
        YTts.getInstance(YApp.get()).speakQueue(s)
    }
}