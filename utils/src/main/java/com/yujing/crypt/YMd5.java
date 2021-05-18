package com.yujing.crypt;

import android.util.Log;

import com.yujing.utils.YLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 算法
 *
 * @author yujing 2018年11月30日12:11:57
 */
@SuppressWarnings({"unused"})
public class YMd5 {
    private final static String MD5 = "MD5";
    // 全局数组
    private final static String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

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
        return getMd5(strObj.getBytes()).toUpperCase();
    }

    public static String getMd5(byte[] bytes) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            resultString = byteToString(md.digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            YLog.e("MD5", "getMd5(byte[] bytes)异常", e);
        }
        return resultString;
    }
}