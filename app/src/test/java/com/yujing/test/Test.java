package com.yujing.test;

import android.app.Activity;

import com.yujing.utils.YDelay;
import com.yujing.utils.YLog;
import com.yujing.utils.YLoop;
import com.yujing.utils.YNumber;

public class Test {

    public void main() {
        //循环调用abc方法每1000毫秒，abc不能为private，不能有参数
//        YLoop.start(this,"abc",1000);
        System.out.println(YNumber.fill(39.985));
    }

    //此方法会被调用
    public void abc() {
        YLog.d("我被调用了");
    }

    protected void onDestroy() {
        //停止循环调用abc方法
        YLoop.stop(this,"abc");

    }
}
