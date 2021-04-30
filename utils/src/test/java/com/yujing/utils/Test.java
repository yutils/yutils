package com.yujing.utils;

import java.util.Arrays;
import java.util.List;

public class Test {

    @org.junit.Test
    public void test() {
        YBytes yBytes = new YBytes("拉丁课教案拉丝机的拉速度快结案率手机登录看得见".getBytes());
        System.out.println(Arrays.toString(yBytes.getBytes()));

        List<byte[]> list = YBytes.split("2123".getBytes(),11);
        for (byte[] item : list) {
            System.out.println(Arrays.toString(item));
        }
    }
}
