package com.yujing.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes256 加密解密
 *
 * @author yujing 2019年8月27日16:28:24
 */
@SuppressWarnings("unused")
public class YAes {
    private static final String TRANSFORMATION = "AES/CBC/NoPadding";
    //16位byte[]
    private static final byte[] KEY = {8, 5, 1, 6, 1, 4, 4, 1, 1, 9, 9, 0, 0, 5, 2, 5};
    private static final SecretKeySpec SKS = new SecretKeySpec(KEY, "AES");

    /**
     * 加密
     *
     * @param bytes    加密对象
     * @param password 密码
     * @return 结果
     */
    public static byte[] encrypt(byte[] bytes, String password) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = createIV(password);
            cipher.init(Cipher.ENCRYPT_MODE, SKS, new IvParameterSpec(iv));
            return cipher.doFinal(paddingData(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param bytes    解密对象
     * @param password 密码
     * @return 结果
     */
    public static byte[] decrypt(byte[] bytes, String password) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = createIV(password);
            cipher.init(Cipher.DECRYPT_MODE, SKS, new IvParameterSpec(iv));
            byte[] decy = cipher.doFinal(bytes);
            //去除decy末尾的0
            int i = decy.length - 1;
            for (; i >= 0; i--) {
                if (decy[i] != 0) {
                    break;
                }
            }
            byte[] nb = new byte[i + 1];
            System.arraycopy(decy, 0, nb, 0, nb.length);
            return nb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 解密16进制字符串
    public static String decryptHex(String hexText, String password) {
        byte[] encryptedBytes = hexStringToByte(hexText);
        byte[] de = decrypt(encryptedBytes, password);
        return new String(de);
    }

    // 加密成16进制字符串
    public static String encryptHex(String data, String password) {
        byte[] en = encrypt(data.getBytes(), password);
        return bytesToHexString(en);
    }

    // 加密成Base64字符串
    public static String encryptBase64(String data, String password) {
        byte[] en = encrypt(data.getBytes(), password);
        return Base64.encode(en);
    }

    // 解密Base64字符串
    public static String decryptBase64(String base64, String password) {
        byte[] encryptedBytes = Base64.decode(base64);
        byte[] de = decrypt(encryptedBytes, password);
        return new String(de);
    }

    //补齐的16位的整数倍
    private static byte[] paddingData(byte[] bytes) {
        int length = bytes.length / 16;
        if (length * 16 < bytes.length) {
            length++;
        }
        byte[] result = new byte[length * 16];
        System.arraycopy(bytes, 0, result, 0, bytes.length);
        for (int i = bytes.length; i < result.length; i++) {
            result[i] = 0x00;
        }
        return result;
    }

    //初始化向量到16位
    private static byte[] createIV(String pIv) {
        byte[] bytes = pIv.getBytes(StandardCharsets.US_ASCII);
        byte[] result = new byte[16];
        System.arraycopy(bytes, 0, result, 0, bytes.length > 16 ? 16 : bytes.length);
        for (int i = bytes.length; i < result.length; i++) {
            result[i] = 0x00;
        }
        return result;
    }

    // bytesToHexString
    private static String bytesToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase(Locale.US));
        }
        return sb.toString();
    }

    // hexStringToByte
    private static byte[] hexStringToByte(String hex) {
        if (hex != null) {
            hex = hex.toUpperCase(Locale.US);
        } else {
            return new byte[0];
        }
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static class Base64 {
        public static char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        public static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

        public static String encode(byte[] data) {
            StringBuilder sb = new StringBuilder();
            int len = data.length;
            int i = 0;
            int b1, b2, b3;
            while (i < len) {
                b1 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                    sb.append("==");
                    break;
                }
                b2 = data[i++] & 0xff;
                if (i == len) {
                    sb.append(base64EncodeChars[b1 >>> 2]);
                    sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                    sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                    sb.append("=");
                    break;
                }
                b3 = data[i++] & 0xff;
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
                sb.append(base64EncodeChars[b3 & 0x3f]);
            }

            return sb.toString();
        }

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
                    b1 = base64DecodeChars[data[i++]];
                } while (i < len && b1 == -1);
                if (b1 == -1)
                    break;
                /* b2 */
                do {
                    b2 = base64DecodeChars[data[i++]];
                } while (i < len && b2 == -1);
                if (b2 == -1)
                    break;
                sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
                /* b3 */
                do {
                    b3 = data[i++];
                    if (b3 == 61)
                        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                    b3 = base64DecodeChars[b3];
                } while (i < len && b3 == -1);
                if (b3 == -1)
                    break;
                sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
                /* b4 */
                do {
                    b4 = data[i++];
                    if (b4 == 61)
                        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                    b4 = base64DecodeChars[b4];
                } while (i < len && b4 == -1);
                if (b4 == -1)
                    break;
                sb.append((char) (((b3 & 0x03) << 6) | b4));
            }
            return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        }
    }
}