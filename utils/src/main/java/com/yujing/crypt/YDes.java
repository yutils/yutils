package com.yujing.crypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * DES加密解密
 *
 * @author yujing 2019年8月27日16:42:02
 */
@SuppressWarnings("unused")
public class YDes {
    private static final String ALGORITHM_MODE = "DESede";
    private static final String ALGORITHM_DES = "DESede/ECB/NOPadding";

    /**
     * DES加密算法
     *
     * @param data 原始字符串
     * @param pwd  密私钥，长度不能够小于8位
     * @return 结果
     */
    public static byte[] encode(byte[] data, byte[] pwd) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(pwd);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_MODE);
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * DES 解密算法
     *
     * @param data 待加密字符串
     * @param pwd  密码
     * @return 解密后的字符串
     */
    public static byte[] decode(byte[] data, byte[] pwd) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(pwd);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_MODE);
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
