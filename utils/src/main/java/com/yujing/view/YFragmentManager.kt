package com.yujing.view

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.yujing.utils.YLog

/**
 * fragment管理器
 *
 * @author 余静 2022年8月11日15:58:24
 */
/*用法
//实例化fragment
Global.fmHome = YFragmentManager(R.id.fl, activity)

//显示/切换fragment
Global.fmHome.show(fragment1)
//重新加载fragment，会触发新fragment的onCreateView,旧fragment的onDestroy
Global.fmHome.replace(fragment1)

//前进
Global.fmHome.goto(Fragment1())
//前进
Global.fmHome.goto(Fragment2())
//后退
Global.fmHome.back()
//后退到指定级
Global.fmHome.back(Fragment1::class.java.name)
//后退全部
Global.fmHome.backAll()
 */
class YFragmentManager {
    //布局
    var layout: Int

    //管理器
    var fragmentManager: FragmentManager

    /**
     * 构造函数
     *
     * @param layout          布局
     * @param fragmentManager 管理器
     */
    constructor(@IdRes layout: Int, fragmentManager: FragmentManager) {
        this.layout = layout
        this.fragmentManager = fragmentManager
    }

    /**
     * 构造函数
     *
     * @param layout           布局
     * @param fragmentActivity fragmentActivity
     */
    constructor(@IdRes layout: Int, fragmentActivity: FragmentActivity) {
        this.layout = layout
        fragmentManager = fragmentActivity.supportFragmentManager
    }

    /**
     * 获取最顶层fragment
     */
    fun getTopFragment(): Fragment? {
        if (fragmentManager.fragments.size == 0) return null
        return fragmentManager.fragments[fragmentManager.fragments.size - 1]
    }

    /**
     * 显示/切换 fragment
     * fragment嵌套的时候使用，避免show(fragment)方法隐藏父fragment
     * @param targetFragment 要显示的fragment
     * @param hideFragment 要隐藏的fragment
     */
    @Synchronized
    fun show(targetFragment: Fragment?, hideFragment: Fragment?) {
        if (targetFragment == null) return
        val ft = fragmentManager.beginTransaction()
        //如果没有添加过,就先添加，添加会自动显示
        if (!targetFragment.isAdded) {
            hideFragment?.let { ft.hide(it) }
            ft.add(layout, targetFragment).commit()
        } else {
            //如果已经添加过，就直接显示
            //如果存在topFragment不是targetFragment，先隐藏topFragment
            if (targetFragment != hideFragment) hideFragment?.let { ft.hide(it) }
            ft.hide(hideFragment!!)
            ft.show(targetFragment).commit()
        }
    }

    /**
     * 显示/切换 fragment
     *
     * @param targetFragment 要显示的fragment
     */
    @Synchronized
    fun show(targetFragment: Fragment?) {
        show(targetFragment, getTopFragment())
    }

    /**
     * 显示当前fragment
     */
    @Synchronized
    fun show() {
        show(getTopFragment())
    }

    /**
     * 隐藏fragment
     *
     * @param targetFragment fragment
     */
    @Synchronized
    fun hide(targetFragment: Fragment?) {
        if (targetFragment == null) return
        if (targetFragment.isAdded) fragmentManager.beginTransaction().hide(targetFragment).commit()
    }

    /**
     * 隐藏当前fragment
     */
    @Synchronized
    fun hide() {
        hide(getTopFragment())
    }

    /**
     * 移除fragment
     *
     * @param fragment fragment
     */
    @Synchronized
    fun remove(fragment: Fragment?) {
        if (fragment == null) return
        fragmentManager.beginTransaction()
            .remove(fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .commit()
    }

    /**
     * 显示fragment
     *
     * @param targetFragment fragment
     */
    @Deprecated("", ReplaceWith("show(targetFragment)"))
    @Synchronized
    fun showFragment(targetFragment: Fragment?) {
        show(targetFragment)
    }

    /**
     * 隐藏fragment
     *
     * @param targetFragment fragment
     */
    @Deprecated("", ReplaceWith("hide(targetFragment)"))
    @Synchronized
    fun hideFragment(targetFragment: Fragment?) {
        hide(targetFragment)
    }

    /**
     * 隐藏当前fragment
     */
    @Deprecated("", ReplaceWith("hide()"))
    @Synchronized
    fun hideCurrent() {
        hide()
    }

    /**
     * 显示当前fragment
     */
    @Deprecated("", ReplaceWith("show()"))
    @Synchronized
    fun showCurrent() {
        show()
    }

    /**
     * 重新加载fragment
     * 新的fragment替换旧的的Fragment，旧的fragment会触发onDestroy()，新的fragment会触onCreateView。
     * @param fragment 新的Fragment
     */
    @Synchronized
    fun replace(fragment: Fragment?) {
        if (fragment == null) return
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(layout, fragment)
        transaction.commit()
    }

    /**
     * 重新加载当前页面
     */
    @Synchronized
    fun replace() {
        replace(getTopFragment())
    }

    /**
     * 重新加载fragment
     * 新的fragment替换旧的的Fragment，旧的fragment会触发onDestroy()，新的fragment会触onCreateView。
     * 如果有回退栈，将清空回退栈
     * fragment嵌套时候不能使用
     * @param fragment 新的Fragment
     */
    @Synchronized
    fun replaceAndBackAll(fragment: Fragment?) {
        backAll()
        replace(fragment)
    }

    /**
     * 打开一个新的fragmentManager，当前fragment不会被销毁，可以回退
     */
    @Synchronized
    fun goto(fragment: Fragment?) {
        if (fragment == null) return
        val transaction = fragmentManager.beginTransaction()
        getTopFragment()?.let {
            transaction.addToBackStack(it::class.java.name)
        }
        transaction.replace(layout, fragment)
        transaction.commit()
    }

    /**
     * 回退到上一级fragment
     */
    @Synchronized
    fun back(): Boolean {
        printBackStack()
        val num = fragmentManager.backStackEntryCount
        if (num == 0) return false
        fragmentManager.popBackStack()
        return num > 0
    }

    /**
     * 打印全部fragment
     */
    fun printFragments(): String {
        val num = fragmentManager.fragments.size
        var s = "Fragment数量：$num，分别是："
        for (f in fragmentManager.fragments) s += "\n" + f.javaClass.name
        YLog.d("Fragment", s)
        return s
    }

    /**
     * 打印全部fragment回退栈
     */
    fun printBackStack(): String {
        val num = fragmentManager.backStackEntryCount
        var s = "Fragment可回退栈数量：$num，分别是："
        for (i in 0 until num) s += "\n" + fragmentManager.getBackStackEntryAt(i).name
        YLog.d("Fragment", s)
        return s
    }


    /**
     * 回退到指定级
     */
    @Synchronized
    fun back(name: String): Boolean {
        printBackStack()
        val num = fragmentManager.backStackEntryCount
        if (num == 0) return false
        fragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        return num > 0
    }

    /**
     * 回退全部fragment
     */
    @Synchronized
    fun backAll(): Boolean {
        printBackStack()
        YLog.d("Fragment", "退出全部栈")
        val num = fragmentManager.backStackEntryCount
        for (i in 0 until num) fragmentManager.popBackStack()
        return num > 0
    }

    /**
     * 重新加载fragment,如果topFragment和fragment一样，不做任何操作
     * 新的fragment替换旧的的Fragment，旧的fragment会触发onDestroy()，新的fragment会触onCreateView。
     *
     * @param fragment 新的Fragment
     */
    @Synchronized
    fun replaceIgnoreCurrent(fragment: Fragment?) {
        if (fragment == null) return
        if (fragment.javaClass.name == getTopFragment()?.javaClass?.name) return
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(layout, fragment)
        transaction.commit()
    }
}