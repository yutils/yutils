package com.yujing.test.activity;

import com.yujing.utils.YPath;
import com.yujing.utils.YSave;

public class Test7 {
    //或
    public static boolean getP() { return YSave.create(YPath.get(),"txt").get("p", boolean.class); }
    public static void setP(Boolean b) { YSave.create(YPath.get(),"txt").put("p", b); }
}
