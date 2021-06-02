package com.yujing.crypt;

import com.yujing.utils.YBase64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA加密解密
 *
 * @author 余静 2020年9月3日14:11:14
 */
/*用法

// 1.获取公钥私钥
Map<String, Object> map = YRsa.getKey();
String publicKeyString = YRsa.getPublicKey(map);
String privateKeyString = YRsa.getPrivateKey(map);

System.out.println("公钥base64：" + publicKeyString);
System.out.println("私钥base64：" + privateKeyString);
String content = "你好，余静。";
System.out.println("加密的数据："+content);

// 2.使用私钥加密
System.out.println("============   分隔符     ===========");
byte[] encodeContent = YRsa.encryptPrivateKey(content.getBytes(), privateKeyString);
System.out.println("私钥加密后的数据base64：" + YBase64.encode(encodeContent));

// 3.使用公钥解密
byte[] decodeContent = YRsa.decryptPublicKey(encodeContent, publicKeyString);
System.out.println("公钥解密后的数据：" + new String(decodeContent));

// 4.使用公钥加密
System.out.println("============   分隔符     ===========");
byte[] encodeContent2 = YRsa.encryptPublicKey(content.getBytes(), publicKeyString);
System.out.println("公钥加密后的数据base64：" + YBase64.encode(encodeContent2));

// 5.使用私钥解密
byte[] decodeContent2 = YRsa.decryptPrivateKey(encodeContent2, privateKeyString);
System.out.println("私钥解密后的数据：" + new String(decodeContent2));

// 6.加签
System.out.println("============   分隔符     ===========");
String sign = YRsa.sign(content.getBytes(), privateKeyString);
System.out.println("签名后的数据base64：" + sign);

// 7.验签
boolean result =  YRsa.verify(content.getBytes(), publicKeyString, sign);
System.out.println("验证签名结果：" + result);

//运行结果：
公钥base64：MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLcaf8qMbAvameflVHHatEBIDMko0kcLCdKkfNwkxrpzolRoOMu1IgVgy8fNB7ZiP+Ge1reNJb1MB9je3Bk31gv8yHB5mX2X7F6XbyP9sBgeqqSjg5hQi53Xma5BXJeb+6q2D9JKN73qrMf9xmv0JTUdyU0TEMqfhhY3zKR0HUewIDAQAB
私钥base64：MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAItxp/yoxsC9qZ5+VUcdq0QEgMySjSRwsJ0qR83CTGunOiVGg4y7UiBWDLx80HtmI/4Z7Wt40lvUwH2N7cGTfWC/zIcHmZfZfsXpdvI/2wGB6qpKODmFCLndeZrkFcl5v7qrYP0ko3veqsx/3Ga/QlNR3JTRMQyp+GFjfMpHQdR7AgMBAAECgYB8vLn58PyCM+dEiVw9lpO675BL75jkjQ3gOY8rx3BXKKuB5rcMsBCCLpeMENbqW+88gfL5HYaHcSST699QN2CCTXqGmo2v1JUrxJnqaNnPMnWYT1YlA+SRJySyXZMbUcm76rtei5CIl1FHsVuDORn2HcK3x6p3nam6BIlqmJhd2QJBAO2m82XJ7sWbfrz5KAI/QAYKwlM6mXQ4DH6meSrDfSOgD6yT40sJmehKdZqVsVPWZy7gFQ5HfgJ7PNLFps2xh68CQQCWNa5MpTopT56fghzvQU2QM+SAlatONXzPJqbwVOI+HFv36v6Ue+m+nRgEI7BzsznV0oqE85zT/1w/sEy3+Sb1AkEAvVfxvpyXj3tnC7rbZIbuRKIX12Xt00nAsruB+E9Oia7CLjaZtjRNGmUQs/wmD2zYQuGoBPty6xkzCZ8OWf3i4wJAZHB0og+BeooguOvZySnTYW8xhcGOkHc3g5SG4AECXyG7ZWUe4c6Rl8GXg30Ryeu36oAj247B+QexeWy7f/D72QJABZ+9yNT2zx8rMO9gmzfN1lQhmcN3pIJmBxSf8kB72sAcmoobw6b8G2ZykDPML6XAo7BT+zyelGM/MlaTvlJM8w==
加密的数据：你好，余静。
============   分隔符     ===========
私钥加密后的数据base64：EULFm3a9k3oukLdUP38fA1uiPHg/zwTMnf8iFwEb5ewd0zMv//TkIk57d8H3BI3TBlHWbrYfMDLOO7JL4ulQdvVg+8hDOGwYGYRFwkPI/JUY/BCahI6wa8xJWxsbeG7YkHFSJmq0wnJqBIU1VkH2a2xmOqtJ8N9K9M98DxLIeXs=
公钥解密后的数据：你好，余静。
============   分隔符     ===========
公钥加密后的数据base64：UJRRswkhqodUepcMssM+5lg+OjOoU9ZAcdpqSJsAsHSWwuPe9e3yfuSVs2W4UYHL55f4irQGUKqfS/u4gxGhod5CEKPm9J8TOOxdQzeXgaiJssedc3hHh9r5VrIeOv86Xo75SNV5G0v0P9fiSrcdgA0DkibRJfMv4CbBHt4OJZQ=
私钥解密后的数据：你好，余静。
============   分隔符     ===========
签名后的数据base64：EHI7CFc/dvDwn3zIHvuAfHtRuo4VVEvbnfSPiokPI0EqS4kb7gawZ1M+wagyH6BTsw7pGjkFZBHBA8+RQpRNzvlq7EbEkea/7ONKGohua2vMJagBFNXNf+/U33G3fzVXeFWkb6rx6OefGVpYrGQboFudRqr2DJU+8dNlGB0NCyk=
验证签名结果：true
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
public class YRsa {
    //加密解密方式
    private final static String RSA_ALGORITHM = "RSA";
    //公钥 map ----> key
    private final static String RSA_PUBLIC_KEY = "RSAPublicKey";
    //私钥 map ----> key
    private final static String RSA_PRIVATE_KEY = "RSAPrivateKey";
    //签名算法：SHA1withDSA；MD2withRSA；MD5withRSA；SHA1withRSA；SHA256withRSA；SHA384withRSA；SHA512withRSA；
    public final static String SIGNATURE_ALGORITHM = "SHA1withRSA";
    //填充方式：RSA/ECB/PKCS1Padding；RSA/ECB；RSA//PKCS1Padding；RSA；
    public static String RSA_PADDING_STYLE = "RSA/ECB/PKCS1Padding";

    /**
     * 使用私钥对数据进行加密
     *
     * @param binaryData 要加密的数据
     * @param privateKey 私钥Base64
     * @return 加密后的数据
     * @throws Exception Exception
     */
    public static byte[] encryptPrivateKey(byte[] binaryData, String privateKey) throws Exception {
        byte[] keyBytes = YBase64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key priKey = keyFactory.generatePrivate(keySpec);
        // 初始化加密器
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(RSA_PADDING_STYLE);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        return cipher.doFinal(binaryData);
    }

    /**
     * 使用公钥对数据进行加密
     *
     * @param binaryData 要加密的数据
     * @param publicKey  公钥Base64
     * @return 加密后的数据
     * @throws Exception Exception
     */
    public static byte[] encryptPublicKey(byte[] binaryData, String publicKey) throws Exception {
        byte[] keyBytes = YBase64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key pubKey = keyFactory.generatePublic(keySpec);
        // 初始化加密器
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(RSA_PADDING_STYLE);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(binaryData);
    }

    /**
     * 使用私钥对数据进行解密
     *
     * @param binaryData 要解密的数据
     * @param privateKey 私钥Base64
     * @return 解密后的数据
     * @throws Exception Exception
     */
    public static byte[] decryptPrivateKey(byte[] binaryData, String privateKey) throws Exception {
        byte[] keyBytes = YBase64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key priKey = keyFactory.generatePrivate(keySpec);
        // 初始化加密器
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(RSA_PADDING_STYLE);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(binaryData);
    }

    /**
     * 使用公钥对数据进行解密
     *
     * @param binaryData 要解密的数据
     * @param publicKey  公钥Base64
     * @return 解密后的数据
     * @throws Exception Exception
     */
    public static byte[] decryptPublicKey(byte[] binaryData, String publicKey) throws Exception {
        byte[] keyBytes = YBase64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key pubKey = keyFactory.generatePublic(x509KeySpec);
        // 初始化加密器
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(RSA_PADDING_STYLE);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(binaryData);
    }

    /**
     * 使用私钥对数据进行签名
     *
     * @param binaryData 要签名的数据
     * @param privateKey 私钥Base64
     * @return 签名后的数据 Base64
     * @throws Exception Exception
     */
    public static String sign(byte[] binaryData, String privateKey)
            throws Exception {
        byte[] keyBytes = YBase64.decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);
        // 获取签名算法
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(binaryData);
        return YBase64.encode(signature.sign());
    }

    /**
     * 使用公钥对数据签名进行验证
     *
     * @param binaryData 要验证的数据
     * @param publicKey  公钥Base64
     * @param sign       签名Base64
     * @return 是否验证通过
     * @throws Exception Exception
     */
    public static boolean verify(byte[] binaryData, String publicKey, String sign) throws Exception {
        byte[] keyBytes = YBase64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // 获取RSA算法实例
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        // 获取签名算法
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(binaryData);
        return signature.verify(YBase64.decode(sign));
    }

    /**
     * 获取一对公钥和私钥
     * 加密位数1024（128byte），1024整数倍
     *
     * @return Map，RSA_PUBLIC_KEY，RSA_PRIVATE_KEY
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static Map<String, Object> getKey() throws NoSuchAlgorithmException {
        return getKey(1024);
    }

    /**
     * 获取一对公钥和私钥
     *
     * @param digit 加密位数
     * @return Map，RSA_PUBLIC_KEY，RSA_PRIVATE_KEY
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static Map<String, Object> getKey(int digit) throws NoSuchAlgorithmException {
        // 因为只存公钥和私钥，所以指明Map的长度是2
        Map<String, Object> keyMap = new HashMap<>(2);
        // 获取RSA算法实例
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        //SecureRandom sr = new SecureRandom();
        //keyPairGen.initialize(digit,sr);
        // 1024代表密钥二进制位数，128位
        keyPairGen.initialize(digit);
        // 产生KeyPair工厂
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        keyMap.put(RSA_PUBLIC_KEY, publicKey);
        keyMap.put(RSA_PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 取出map中的公钥
     *
     * @param map 一对公钥和私钥
     * @return base64
     */
    public static String getPublicKey(Map<String, Object> map) {
        RSAPublicKey publicKey = (RSAPublicKey) map.get(RSA_PUBLIC_KEY);
        if (publicKey == null) return null;
        return YBase64.encode(publicKey.getEncoded());
    }

    /**
     * 取出map中的私钥
     *
     * @param map 一对公钥和私钥
     * @return base64
     */
    public static String getPrivateKey(Map<String, Object> map) {
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get(RSA_PRIVATE_KEY);
        if (privateKey == null) return null;
        return YBase64.encode(privateKey.getEncoded());
    }
}
