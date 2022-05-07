package com.yujing.view;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * fragment管理器
 *
 * @author 余静 2019年11月15日11:07:23
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
yFragmentManager!!replace(fragment1)

 */
public class YFragmentManager {
    private Fragment currentFragment;//当前fragment
    private int layout; //布局
    private FragmentManager fragmentManager; //管理器

    /**
     * 构造函数
     *
     * @param layout          布局
     * @param fragmentManager 管理器
     */
    public YFragmentManager(@IdRes int layout, FragmentManager fragmentManager) {
        this.layout = layout;
        this.fragmentManager = fragmentManager;
    }

    /**
     * 构造函数
     *
     * @param layout           布局
     * @param fragmentActivity fragmentActivity
     */
    public YFragmentManager(@IdRes int layout, FragmentActivity fragmentActivity) {
        this.layout = layout;
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    /**
     * 显示fragment
     *
     * @param targetFragment fragment
     */
    public synchronized void show(Fragment targetFragment) {
        if (targetFragment == null) return;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //如果没有添加过,就先添加，添加会自动显示
        if (!targetFragment.isAdded()) {
            ft.hide(currentFragment);
            ft.add(layout, targetFragment).commit();
        } else {
            //如果已经添加过，就直接显示
            //如果存在currentFragment不是targetFragment，先隐藏currentFragment
            if (!targetFragment.equals(currentFragment))
                ft.hide(currentFragment);
            ft.show(targetFragment).commit();
        }
        currentFragment = targetFragment;//记录当前fragment
    }

    /**
     * 显示当前fragment
     */
    public synchronized void show() {
        show(currentFragment);
    }

    /**
     * 隐藏fragment
     *
     * @param targetFragment fragment
     */
    public synchronized void hide(Fragment targetFragment) {
        if (targetFragment == null) return;
        if (targetFragment.isAdded())
            fragmentManager.beginTransaction().hide(targetFragment).commit();
    }

    /**
     * 隐藏当前fragment
     */
    public synchronized void hide() {
        hide(currentFragment);
    }

    /**
     * 移除fragment
     *
     * @param fragment fragment
     */
    public synchronized void remove(Fragment fragment) {
        if (fragment == null) return;
        fragmentManager.beginTransaction().remove(fragment);
        if (fragment.equals(currentFragment)) {
            currentFragment = null;
        }
    }

    /**
     * 显示fragment
     *
     * @param targetFragment fragment
     */
    @Deprecated
    public synchronized void showFragment(Fragment targetFragment) {
        show(targetFragment);
    }

    /**
     * 隐藏fragment
     *
     * @param targetFragment fragment
     */
    @Deprecated
    public synchronized void hideFragment(Fragment targetFragment) {
        hide(targetFragment);
    }

    /**
     * 隐藏当前fragment
     */
    @Deprecated
    public synchronized void hideCurrent() {
        hide();
    }

    /**
     * 显示当前fragment
     */
    @Deprecated
    public synchronized void showCurrent() {
        show();
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    /**
     * 重新加载fragment
     * 新的fragment替换旧的的Fragment，旧的fragment会触发onDestroy()，新的fragment会触onCreateView。
     *
     * @param fragment 新的Fragment
     */
    public synchronized void replace(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(layout, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    public synchronized void replace() {
        replace(currentFragment);
    }
}
