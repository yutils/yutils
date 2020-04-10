package com.yujing.crypt;

import com.yujing.utils.YBase64;
import com.yujing.utils.YConvert;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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

    public static SecretKeySpec getKey() {
        byte[] KEY = {8, 5, 1, 6, 1, 4, 4, 1, 1, 9, 9, 0, 0, 5, 2, 5};
        return  new SecretKeySpec(KEY, "AES");
    }

    /**
     * 创建一个随机秘钥
     * @return 秘钥
     */
    public static Key createKey() {
        try {
            // 生成key
            KeyGenerator keyGenerator;
            //构造密钥生成器，指定为AES算法,不区分大小写
            keyGenerator = KeyGenerator.getInstance("AES");
            //生成一个128位的随机源,根据传入的字节数组
            keyGenerator.init(128);
            //产生原始对称密钥
            SecretKey secretKey = keyGenerator.generateKey();
            //获得原始对称密钥的字节数组
            byte[] keyBytes = secretKey.getEncoded();
            // key转换,根据字节数组生成AES密钥
            Key key = new SecretKeySpec(keyBytes, "AES");
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**加密
     * @param context 需要加密的明文
     * @param key 加密用密钥
     * @return 加密后的内容
     */
    public static byte[] encrypt(String context, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //将加密并编码后的内容解码成字节数组
            return cipher.doFinal(context.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /** 解密
     * @param result 加密后的密文byte数组
     * @param key 解密用密钥
     * @return 解密后的内容
     */
    public static byte[] decrypt(byte[] result, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
            IvParameterSpec ipc= new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(),ipc);
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
            IvParameterSpec ipc= new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), ipc);
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


    // 加密成Base64字符串
    public static String encryptToBase64(String data, String password) {
        byte[] en = encrypt(data.getBytes(), password);
        return YBase64.encode(en);
    }

    // 解密Base64字符串
    public static String decryptFromBase64(String base64, String password) {
        byte[] encryptedBytes = YBase64.decode(base64);
        byte[] de = decrypt(encryptedBytes, password);
        return new String(de);
    }

    // 加密成16进制字符串
    public static String encryptToHex(String data, String password) {
        byte[] en = encrypt(data.getBytes(), password);
        return YConvert.bytesToHexString(en);
    }

    // 解密16进制字符串
    public static String decryptFromHex(String hexText, String password) {
        byte[] encryptedBytes = YConvert.hexStringToByte(hexText);
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
        System.arraycopy(bytes, 0, result, 0, Math.min(bytes.length, 16));
        for (int i = bytes.length; i < result.length; i++) {
            result[i] = 0x00;
        }
        return result;
    }
}