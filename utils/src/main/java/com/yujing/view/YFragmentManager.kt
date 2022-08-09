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
 * @author 余静 2022年8月5日12:11:46
 */
/*用法
private var yFragmentManager: YFragmentManager? = null
private var fragment1: Fragment1? = null
private var fragment2: Fragment2? = null
private var fragment3: Fragment3? = null

//实例化fragment
yFragmentManager = YFragmentManager(R.id.fl_main, this)
fragment1 = Fragment1()
fragment2 = Fragment2()
fragment3 = Fragment3()


//显示/切换fragment
yFragmentManager!!.show(fragment1)

//重新加载fragment，会触发新fragment的onCreateView,旧fragment的onDestroy
yFragmentManager!!.replace(fragment1)

//前进
yFragmentManager!!.goto(Fragment1())
//后退
yFragmentManager!!.back()
//后退到指定级
yFragmentManager!!.back(Fragment1::class.java.name)
//后退全部
yFragmentManager!!.backAll()
 */
class YFragmentManager {
    //当前fragment
    var currentFragment: Fragment? = null

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
     * 显示fragment
     *
     * @param targetFragment fragment
     */
    @Synchronized
    fun show(targetFragment: Fragment?) {
        if (targetFragment == null) return
        val ft = fragmentManager.beginTransaction()
        //如果没有添加过,就先添加，添加会自动显示
        if (!targetFragment.isAdded) {
            currentFragment?.let { ft.hide(it) }
            ft.add(layout, targetFragment).commit()
        } else {
            //如果已经添加过，就直接显示
            //如果存在currentFragment不是targetFragment，先隐藏currentFragment
            if (targetFragment != currentFragment) currentFragment?.let { ft.hide(it) }
            ft.show(targetFragment).commit()
        }
        currentFragment = targetFragment //记录当前fragment
    }

    /**
     * 显示当前fragment
     */
    @Synchronized
    fun show() {
        show(currentFragment)
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
        hide(currentFragment)
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
        if (fragment == currentFragment) currentFragment = null
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
     * 如果有回退栈，将清空回退栈
     * @param fragment 新的Fragment
     */
    @Synchronized
    fun replace(fragment: Fragment?) {
        if (fragment == null) return
        backAll()
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(layout, fragment)
        transaction.commit()
        currentFragment = fragment
    }

    @Synchronized
    fun replace() {
        replace(currentFragment)
    }

    /**
     * 打开一个新的fragmentManager，当前fragment不会被销毁，可以回退
     */
    @Synchronized
    fun goto(fragment: Fragment?) {
        if (fragment == null) return
        val transaction = fragmentManager.beginTransaction()
        currentFragment?.let {
            transaction.addToBackStack(it::class.java.name)
        }
        transaction.replace(layout, fragment)
        transaction.commit()
        currentFragment = fragment
    }

    /**
     * 回退到上一级fragment
     */
    @Synchronized
    fun back(): Boolean {
        val num = fragmentManager.backStackEntryCount
        var s = "Fragment可回退栈数量：$num，分别是："
        for (i in 0 until num) s += "\n" + fragmentManager.getBackStackEntryAt(i).name
        YLog.d("Fragment", s)
        if (num == 0) return false
        hide(currentFragment)
        remove(currentFragment)
        fragmentManager.popBackStack()
        return num > 0
    }

    /**
     * 回退到指定级
     */
    @Synchronized
    fun back(name: String): Boolean {
        val num = fragmentManager.backStackEntryCount
        var s = "Fragment可回退栈数量：$num，分别是："
        for (i in 0 until num) s += "\n" + fragmentManager.getBackStackEntryAt(i).name
        YLog.d("Fragment", s)
        if (num == 0) return false
        hide(currentFragment)
        remove(currentFragment)
        fragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        return num > 0
    }

    /**
     * 回退全部fragment
     */
    @Synchronized
    fun backAll(): Boolean {
        YLog.d("Fragment", "退出全部栈")
        val num = fragmentManager.backStackEntryCount
        for (i in 0 until num) fragmentManager.popBackStack()
        return num > 0
    }

    /**
     * 重新加载fragment,如果currentFragment和fragment一样，不做任何操作
     * 新的fragment替换旧的的Fragment，旧的fragment会触发onDestroy()，新的fragment会触onCreateView。
     *
     * @param fragment 新的Fragment
     */
    @Synchronized
    fun replaceIgnoreCurrent(fragment: Fragment?) {
        if (fragment == null) return
        if (fragment.javaClass.name == currentFragment?.javaClass?.name) return
        val transaction = fragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(layout, fragment)
        transaction.commit()
        currentFragment = fragment
    }
}