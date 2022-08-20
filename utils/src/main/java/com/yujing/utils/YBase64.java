package com.yujing.utils;

import android.os.Build;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * YBase64 标准base64
 *
 * @author 余静
 * 2022年8月20日16:26:55
 */
@SuppressWarnings("unused")
public class YBase64 {
    private static final char[] ENCODE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final byte[] DECODE = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    public static String encodeString(String data) {
        return encode(data.getBytes());
    }

    public static String decodeString(String str) {
        return new String(Objects.requireNonNull(decode(str)));
    }

    /**
     * 编码成base64
     * 相当于：
     * String string = android.util.Base64.encodeToString(bytes, Base64.NO_WRAP);
     *
     * @param data byte数组
     * @return base64
     */
    @SuppressWarnings("DuplicateExpressions")
    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(ENCODE[b1 >>> 2]);
                sb.append(ENCODE[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            if (i >= len) break;
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(ENCODE[b1 >>> 2]);
                sb.append(ENCODE[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(ENCODE[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            if (i >= len) break;
            b3 = data[i++] & 0xff;
            sb.append(ENCODE[b1 >>> 2]);
            sb.append(ENCODE[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(ENCODE[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(ENCODE[b3 & 0x3f]);
        }
        return sb.toString();
    }

    /**
     * base64解码
     * 相当于：
     * byte[] bs = android.util.Base64.decode(Base64String, Base64.DEFAULT)
     *
     * @param str 目标字符串
     * @return byte数组
     */
    public static byte[] decode(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] data;
        data = str.getBytes(StandardCharsets.US_ASCII);
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            /* b1 */
            do {
                b1 = DECODE[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1)
                break;
            /* b2 */
            if (i >= len) break;
            do {
                b2 = DECODE[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
            /* b3 */
            if (i >= len) break;
            do {
                b3 = data[i++];
                if (b3 == 61)
                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                b3 = DECODE[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
            /* b4 */
            if (i >= len) break;
            do {
                b4 = data[i++];
                if (b4 == 61)
                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                b4 = DECODE[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    //--------------------------------------原生用法--------------------------------------
    public static String encodeStringDefault(String data) {
        return encodeDefault(data.getBytes());
    }

    public static String decodeStringDefault(String str) {
        return new String(Objects.requireNonNull(decodeDefault(str)));
    }

    /**
     * 编码成base64 原生用法
     *
     * @param data byte数组
     * @return base64
     */
    public static String encodeDefault(byte[] data) {
        if (YClass.isAndroid()) {
            return Base64.encodeToString(data, Base64.NO_WRAP);//不自动换行
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return java.util.Base64.getEncoder().encodeToString(data);
            }
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);//不自动换行
    }

    /**
     * base64解码 原生用法
     *
     * @param str 目标字符串
     * @return byte数组
     */
    public static byte[] decodeDefault(String str) {
        if (YClass.isAndroid()) {
            return Base64.decode(str, Base64.NO_WRAP);//不自动换行
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return java.util.Base64.getDecoder().decode(str.replace("\n", ""));//遇到换行也不怕
            }
        }
        return Base64.decode(str, Base64.NO_WRAP);//不自动换行
    }
}
