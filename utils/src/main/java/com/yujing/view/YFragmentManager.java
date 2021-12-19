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
yFragmentManager!!.showFragment(fragment1)

//重新加载fragment
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
    public synchronized void showFragment(Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) transaction.hide(currentFragment);
            transaction.add(layout, targetFragment).commit();
        } else {
            //如果存在currentFragment,而且不是自己就先隐藏currentFragment
            if (currentFragment != null && !currentFragment.equals(targetFragment))
                transaction.hide(currentFragment);
            transaction.show(targetFragment).commit();
        }
        currentFragment = targetFragment;//记录当前fragment
    }

    /**
     * 隐藏fragment
     *
     * @param targetFragment fragment
     */
    public synchronized void hideFragment(Fragment targetFragment) {
        if (targetFragment.isAdded())
            fragmentManager.beginTransaction().hide(targetFragment).commit();
    }

    /**
     * 隐藏当前fragment
     */
    public synchronized void hideCurrent() {
        if (currentFragment != null && currentFragment.isAdded())
            fragmentManager.beginTransaction().hide(currentFragment).commit();
    }

    /**
     * 显示当前fragment
     */
    public synchronized void showCurrent() {
        if (currentFragment != null && currentFragment.isAdded())
            fragmentManager.beginTransaction().show(currentFragment).commit();
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
    }
}
