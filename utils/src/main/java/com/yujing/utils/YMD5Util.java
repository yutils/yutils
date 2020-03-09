package com.yujing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 算法
 * @author yujing 2018年11月30日12:11:57
*/
@SuppressWarnings({"unused", "WeakerAccess"})
public class YMD5Util {
    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    // 返回形式只为数字
    private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (byte aBByte : bByte) {
            sBuffer.append(byteToArrayString(aBByte));
        }
        return sBuffer.toString();
    }

    public static String MD5(String strObj) {
        String resultString = null;
        try {
            resultString = strObj;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString.toUpperCase();
    }
}