package com.yujing.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.yujing.crypt.YMd5;
import com.yujing.crypt.YSha1;

/**
 * 获取APP的签名信息
 * //在代码中调用（获取SHA1值）：
 * String signal = YAppInfoUtils.getSign(getApplicationContext(), getPackageName(), YAppInfoUtils.SHA1);
 * YAppInfoUtils.getSign(applicationContext, packageName, YAppInfoUtils.SHA1)
 */
public class YAppInfoUtils {
    public final static String SHA1 = "SHA1";
    public final static String MD5 = "MD5";

    /**
     * 返回一个签名的对应类型的字符串
     *
     * @param context     上下文
     * @param packageName 包名
     * @param type        类型
     * @return 对应类型的签名信息
     */
    public static String getSign(Context context, String packageName, String type) {
        String tmp = null;
        Signature[] signs = getSign(context, packageName);
        for (Signature sig : signs) {
            if (SHA1.equals(type)) {
                tmp = YSha1.getSha1(sig.toByteArray());
                break;
            } else if (MD5.equals(type)) {
                tmp = YMd5.getMd5(sig.toByteArray());
            }
        }
        return tmp;
    }

    /**
     * 返回对应包的签名信息
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 签名信息
     */
    public static Signature[] getSign(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
