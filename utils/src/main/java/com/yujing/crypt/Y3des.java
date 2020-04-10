package com.yujing.crypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 3DES加密解密
 *
 * @author yujing 2019年8月27日16:28:05
 */
@SuppressWarnings("unused")
public class Y3des {
    private static final String algorithm = "DESede";

    public static byte[] getKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);// 密钥生成器
        keyGen.init(168); // 可指定密钥长度为112或168，默认为168
        SecretKey secretKey = keyGen.generateKey();// 生成密钥
        return secretKey.getEncoded();
    }

    /**
     * 3DES加密
     *
     * @param bytes 加密的对象
     * @param key   key，生成的
     * @return 加密结果
     * @throws Exception 异常
     */
    public static byte[] encode(byte[] bytes, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, algorithm);// 恢复密钥
        Cipher cipher = Cipher.getInstance(algorithm);// Cipher完成加密或解密工作类
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 对Cipher初始化，解密模式
        return cipher.doFinal(bytes);
    }

    /**
     * 3DES解密
     *
     * @param bytes 解密的对象
     * @param key   key，生成的
     * @return 解密结果
     * @throws Exception 异常
     */
    public static byte[] decode(byte[] bytes, byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, algorithm);// 恢复密钥
        Cipher cipher = Cipher.getInstance(algorithm);// Cipher完成加密或解密工作类
        cipher.init(Cipher.DECRYPT_MODE, secretKey);// 对Cipher初始化，解密模式
        return cipher.doFinal(bytes);
    }
}
