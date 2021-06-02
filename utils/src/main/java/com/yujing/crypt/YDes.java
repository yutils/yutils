package com.yujing.crypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * DES加密解密
 *
 * @author 余静 2019年8月27日16:42:02
 */
/*
加密模式和填充：
Algorithm   Modes         Paddings        Supported API Levels
   AES       CBC      ISO10126Padding              1+
             CFB         NoPadding
             CTR        PKCS5Padding
             CTS
             ECB
             OFB
             GCM         NoPadding                10+

 AES_128     CBC         NoPadding                26+
             ECB        PKCS5Padding
             GCM         NoPadding                26+

 AES_256     CBC         NoPadding                26+
             ECB        PKCS5Padding
             GCM         NoPadding                26+

   ARC4      ECB         NoPadding                10+
            NONE         NoPadding                28+

 BLOWFISH    CBC      ISO10126Padding             10+
             CFB         NoPadding
             CTR        PKCS5Padding
             CTS
             ECB
             OFB

 ChaCha20   NONE         NoPadding                28+
          Poly1305

   DES       CBC      ISO10126Padding              1+
             CFB         NoPadding
             CTR        PKCS5Padding
             CTS
             ECB
             OFB

  DESede     CBC      ISO10126Padding              1+
             CFB         NoPadding
             CTR        PKCS5Padding
             CTS
             ECB
             OFB

   RSA       ECB         NoPadding                 1+
            NONE        OAEPPadding
                        PKCS1Padding
                   OAEPwithSHA-1andMGF1Pa         10+
                   OAEPwithSHA-256andMGF1Padding
                   OAEPwithSHA-224andMGF1          23
                   OAEPwithSHA-384andMGF1Padding
                   OAEPwithSHA-512andMGF1Padding
 */
@SuppressWarnings("unused")
public class YDes {
    public static final String ALGORITHM_MODE = "DESede";
    public static final String ALGORITHM_DES = "DESede/ECB/NOPadding";

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
