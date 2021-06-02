package com.yujing.crypt;

import com.yujing.utils.YBase64;
import com.yujing.utils.YConvert;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Aes128 加密解密
 * @author 余静 2020年9月17日18:59:38
 */
/* 使用方法
// AES CBC 加密
    byte[] en = YAes.encryptCBC("余静".getBytes(), "123456".getBytes(), "8516144119920625");
    byte[] dec = YAes.decryptCBC(en, "123456".getBytes(), "8516144119920625");
    System.out.println("加密后：" + YBase64.encode(en));
    System.out.println("解密：" + new String(dec));

// AES ECB 加密
    YAes.AES_ECB_Padding="AES/ECB/ISO10126Padding";
    byte[] en = YAes.encryptECB("余静".getBytes(), "123456".getBytes());
    byte[] dec = YAes.decryptECB(en, "123456".getBytes());
    System.out.println("加密后：" + YBase64.encode(en));
    System.out.println("解密：" + new String(dec));

// AES ECB 加密
    String en = YAes.encryptToBase64("余静", "123456");
    String dec = YAes.decryptFromBase64(en, "123456");
    System.out.println("加密后：" + en);
    System.out.println("解密：" + dec);

// AES 生成KEY 加密
    Key key = YAes.createKey();
    byte[] en = YAes.encrypt("余静".getBytes(), key);
    byte[] dec = YAes.decrypt(en, key);
    System.out.println("加密后：" + YBase64.encode(en));
    System.out.println("解密：" + new String(dec));
 */
@SuppressWarnings("unused")
public class YAes {
    private static final String AES = "AES";
    public static String AES_CBC_Padding = "AES/CBC/PKCS5Padding";
    public static String AES_ECB_Padding = "AES/ECB/PKCS5Padding";
    // C# 不支持PKCS5，通用填充只有NoPadding和 "AES/ECB/ISO10126Padding";

    /**
     * 创建一个随机秘钥
     *
     * @return 秘钥
     */
    public static SecretKeySpec createKey() throws Exception {
        // 生成key,构造密钥生成器，指定为AES算法,不区分大小写
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        //生成一个128位的随机源,根据传入的字节数组
        keyGenerator.init(128);
        //产生原始对称密钥
        SecretKey secretKey = keyGenerator.generateKey();
        //获得原始对称密钥的字节数组
        byte[] keyBytes = secretKey.getEncoded();
        // key转换,根据字节数组生成AES密钥
        return new SecretKeySpec(keyBytes, AES);
    }

    /**
     * 创建一个随机秘钥
     *
     * @param key 根据password创建
     * @return 秘钥
     */
    public static SecretKeySpec createKey(byte[] key) throws Exception {
        // 生成key//构造密钥生成器，指定为AES算法,不区分大小写
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        //生成一个128位的随机源,根据传入的字节数组
        keyGenerator.init(128, new SecureRandom(key));
        //产生原始对称密钥
        SecretKey secretKey = keyGenerator.generateKey();
        //获得原始对称密钥的字节数组
        byte[] keyBytes = secretKey.getEncoded();
        // key转换,根据字节数组生成AES密钥
        return new SecretKeySpec(keyBytes, AES);
    }

    /**
     * 加密
     *
     * @param context 需要加密的明文
     * @param key     加密用密钥
     * @return 加密后的内容
     */
    public static byte[] encrypt(byte[] context, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_Padding);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //将加密并编码后的内容解码成字节数组
        return cipher.doFinal(context);
    }

    /**
     * 解密
     *
     * @param result 加密后的密文byte数组
     * @param key    解密用密钥
     * @return 解密后的内容
     */
    public static byte[] decrypt(byte[] result, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ECB_Padding);
        //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(result);
    }

    /**
     * CBC加密
     *
     * @param bytes 明文
     * @param key   密码
     * @param iv    偏移量16位
     * @return 加密后
     * @throws Exception Exception
     */
    public static byte[] encryptCBC(byte[] bytes, byte[] key, String iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(paddingData(key), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_Padding);
        byte[] ivBytes = createIV(iv);
        IvParameterSpec ipc = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ipc);
        return cipher.doFinal(bytes);
    }

    /**
     * CBC解密
     *
     * @param bytes 密文
     * @param key   密码
     * @param iv    偏移量16位
     * @return 解密后
     * @throws Exception Exception
     */
    public static byte[] decryptCBC(byte[] bytes, byte[] key, String iv) throws Exception {
        SecretKey secretKey = new SecretKeySpec(paddingData(key), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_Padding);
        byte[] ivBytes = createIV(iv);
        IvParameterSpec ipc = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ipc);
        return cipher.doFinal(bytes);
    }

    /**
     * ECB加密
     *
     * @param bytes 明文
     * @param key   密码
     * @return 加密后
     * @throws Exception Exception
     */
    public static byte[] encryptECB(byte[] bytes, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(paddingData(key), AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_Padding);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(bytes);
    }

    /**
     * ECB解密
     *
     * @param bytes 密文
     * @param key   密码
     * @return 解密后
     * @throws Exception Exception
     */
    public static byte[] decryptECB(byte[] bytes, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(paddingData(key), AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_Padding);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(bytes);
    }


    // 加密成Base64字符串
    public static String encryptToBase64(String data, String password) throws Exception {
        byte[] en = encryptECB(data.getBytes(), password.getBytes());
        return YBase64.encode(en);
    }

    // 解密Base64字符串
    public static String decryptFromBase64(String base64, String password) throws Exception {
        byte[] encryptedBytes = YBase64.decode(base64);
        byte[] de = decryptECB(encryptedBytes, password.getBytes());
        return new String(de);
    }

    // 加密成16进制字符串
    public static String encryptToHex(String data, String password) throws Exception {
        byte[] en = encryptECB(data.getBytes(), password.getBytes());
        return YConvert.bytesToHexString(en);
    }

    // 解密16进制字符串
    public static String decryptFromHex(String hexText, String password) throws Exception {
        byte[] encryptedBytes = YConvert.hexStringToByte(hexText);
        byte[] de = decryptECB(encryptedBytes, password.getBytes());
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