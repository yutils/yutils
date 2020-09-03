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

System.out.println("公钥：" + publicKeyString);
System.out.println("私钥：" + privateKeyString);
String content = "你好，我是余静。";
System.out.println("============   分隔符     ===========");

// 2.使用私钥加密
byte[] encodeContent = YRsa.encryptPrivateKey(content.getBytes(), privateKeyString);
System.out.println("私钥加密后的数据：" + new String(encodeContent));

// 3.使用公钥解密
byte[] decodeContent = YRsa.decryptPublicKey(encodeContent, publicKeyString);
System.out.println("公钥解密后的数据：" + new String(decodeContent));

System.out.println("============   分隔符     ===========");
// 4.使用公钥加密
byte[] encodeContent2 = YRsa.encryptPublicKey(content.getBytes(), publicKeyString);
System.out.println("公钥加密后的数据：" + new String(encodeContent2));

// 5.使用私钥解密
byte[] decodeContent2 = YRsa.decryptPrivateKey(encodeContent2, privateKeyString);
System.out.println("私钥解密后的数据：" + new String(decodeContent2));

System.out.println("============   分隔符     ===========");
// 6.加签
String sign = YRsa.sign(content.getBytes(), privateKeyString);
System.out.println("加签后的数据：" + sign);

// 7.验签
boolean result =  YRsa.verify(content.getBytes(), publicKeyString, sign);
System.out.println("验签结果：" + result);
 */
public class YRsa {
    //加密解密方式
    private final static String RSA_ALGORITHM = "RSA";
    //签名算法
    private final static String SIGNATURE_ALGORITHM = "MD5withRSA";
    //公钥 map ----> key
    private final static String RSA_PUBLIC_KEY = "RSAPublicKey";
    //私钥 map ----> key
    private final static String RSA_PRIVATE_KEY = "RSAPrivateKey";
    //填充方式
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
     *
     * @return Map，RSA_PUBLIC_KEY，RSA_PRIVATE_KEY
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static Map<String, Object> getKey() throws NoSuchAlgorithmException {
        // 因为只存公钥和私钥，所以指明Map的长度是2
        Map<String, Object> keyMap = new HashMap<>(2);
        // 获取RSA算法实例
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        // 1024代表密钥二进制位数
        keyPairGen.initialize(1024);
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
