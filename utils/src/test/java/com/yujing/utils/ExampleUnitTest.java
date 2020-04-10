package com.yujing.utils;

import com.yujing.crypt.YAes;

import org.junit.Test;

import java.security.Key;
import java.util.Arrays;


public class ExampleUnitTest {
    @Test
    public void test() {

        String s="余静";
        String mm= YAes.encryptToBase64(s,"卧槽卧槽卧槽卧槽卧槽卧槽卧槽");
        System.out.println(mm);

        String mm2= YAes.encryptToHex(s,"卧槽卧槽卧槽卧槽卧槽卧槽卧槽");
        System.out.println(mm2);

        s= YAes.decryptFromBase64(mm,"卧槽卧槽卧槽卧槽卧槽卧槽卧槽");
        System.out.println(s);
    }

    @Test
    public void test2() {

        String s="余静";
        Key p= YAes.getKey();
        System.out.println(Arrays.toString(p.getEncoded()));

        byte[]  mm= YAes.encrypt(s,p);
        System.out.println(YBase64.encode(mm));

        mm= YAes.encrypt(s,p);
        System.out.println(YBase64.encode(mm));

        byte[] j= YAes.decrypt(mm,p);
        System.out.println(new String(j));
    }
}