package com.yujing.crypt;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class YSha1 {
    private final static String SHA1 = "SHA1";

    /**
     * 获取字符串的SHA1
     *
     * @param str 字符串
     * @return String
     */
    public static String SHA1(String str) {
        return getSha1(str.getBytes()).toUpperCase();
    }

    /**
     * 获取byte[]的SHA1值
     *
     * @param bytes 要获取的数据
     * @return SHA1值
     */
    public static String getSha1(byte[] bytes) {
        String fingerprint = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA1);
            byte[] digestBytes = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte digestByte : digestBytes) {
                sb.append((Integer.toHexString((digestByte & 0xFF) | 0x100)).substring(1, 3));
            }
            fingerprint = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("YSha1", "getSha1(byte[] bytes)异常：", e);
        }
        return fingerprint;
    }

    /**
     * 获取文件的Sha1
     *
     * @param file 文件
     * @return String
     */
    public static String getFileSha1(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance(SHA1);
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            String sha1 = new BigInteger(1, digest.digest()).toString(16);
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1 = "0" + sha1;
                }
            }
            return sha1;
        } catch (IOException | NoSuchAlgorithmException e) {
            Log.e("YSha1", "getFileSha1(File file)异常：", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e("YSha1", "in.close()异常：", e);
            }
        }
        return null;
    }
}
