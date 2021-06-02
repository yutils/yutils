package com.yujing.crypt;

import com.yujing.utils.YBase64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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
System.out.println("验签签名结果：" + result);

//运行结果：
公钥base64：MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaR3uFHMlOjUEbz3DjxfwtE9T1KqHMpSU7Zg6WFz7li3iZfLFgraoYuU8w0rGfOsm8mswcsZx+yqbjL9MBvpMrOYPk23/3Ofw0F7ux4m3mXpXt3nWn/cdTNFA78WPvJQczHc4t4FzvUHUbCXWJ+/XgQfd7oWJCiWl3DuaA73eHxQIDAQAB
私钥base64：MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJpHe4UcyU6NQRvPcOPF/C0T1PUqocylJTtmDpYXPuWLeJl8sWCtqhi5TzDSsZ86ybyazByxnH7KpuMv0wG+kys5g+Tbf/c5/DQXu7HibeZele3edaf9x1M0UDvxY+8lBzMdzi3gXO9QdRsJdYn79eBB93uhYkKJaXcO5oDvd4fFAgMBAAECgYAoxwAE3OjwVDGDUj76VRgkKfu9mTkOyA+hNYZhcV90eHq1xtlzPjOZOVGPDAFansU3joqoguFkOdgGcFuLOH3ZI9j0sCostFxRzhmTsHIA1PxL09sM4Deku+AnTHjMUkmwXsnVS1jqogXzcul6Vo/Eq8X8BGQpA3y5uocpeZ0AOQJBAMija3u03YkGQontUD5+tmHa/igz6LLyQs70CNF766rbo94Mr+mI9YmwW/CQsuw25NKj5W48FWMXPLMNNA58rzcCQQDE2V9RNWgoxdnA80XL7jkEbZnnR7yVdUDRlnDVAWRaAtlIxJgqG5CgILz00dhzPGg+baFWaLCv4xKdVfIJSibjAkBWgrA7nNbQ2FQkaKDq8XPuaaCg8RDq566K0Ypj2QzalO3pNos7JQTKI7Lg3WNompq7gFPS3jFSkphnk8/YV0atAkEAhkcFNx8oQw/bXzxTMy34ZOXioxqTMJyAL7fgldxSOPhSgcnhRm/xMtnCK3ptnQXq0hL0iD33sLNDwmGbLe0QIwJACcAwOI4/1PNcrnJ52jFPVxcJg6AsfbhvUD7ZBvY+AtE+0h8xzr1ga4TVRy5AIXJw7wgGOIzWJ/gD4OqXf4JuEg==
加密的数据：你好，余静。
============   分隔符     ===========
私钥加密后的数据base64：FBw+6EOklHrONL2sw0lz/IqAH0FmvOvobukNWfRM+k5yyJfmifwgHmNWRD6XhX9VBYK1ljUtUfi7jp4ZW0Wqr0MkG9aGHFBiMjIBSryd4rCIMfE9fn5ux9Bx9vUnTKjNiFlwYf6HPAYfdwL0ItPjFPdQ84qIMuKILII+a6xYDV0=
公钥解密后的数据：你好，余静。
============   分隔符     ===========
公钥加密后的数据base64：V0N+LEif30mnvjfBWCkyrNiSDmIMwmclUq1wCS2esJw0c8+OGIWJ9S7naPqPt55w51SwpghvDzrFyeqQ5zTUnxHA1GygGnuqjK/eo7Uumv+92qailypgpZeC+ssIgLh1WHx9syNDOluYYJk3HXdE0cjA5g8aEmMyC321pX5sndQ=
私钥解密后的数据：你好，余静。
============   分隔符     ===========
签名后的数据base64：jKbe0ckBtFDbwq9Y66S46h3f/ZuUZcJU/9Dd5Ky1kWKVKgj80HlX4S///DB3NxTqwYg73Eq5QfTBb9sSz/XlicO7zOe/YEh8OpZMkODzCJXx80K+POdQ/21senfhd6vIXTlimhX98d9wWJCCAY7U10gLdx/CGBYoG2BhTDKKlzM=
验签签名结果：true
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
    //签名算法,SHA1withRSA
    /*
        SHA1withDSA
        MD2withRSA
        MD5withRSA
        SHA1withRSA
        SHA256withRSA
        SHA384withRSA
        SHA512withRSA
     */
    public final static String SIGNATURE_ALGORITHM = "MD5withRSA";
    //填充方式
    /*
        RSA/ECB/PKCS1Padding
        RSA/ECB
        RSA//PKCS1Padding
        RSA
     */
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
     * @return Map，RSA_PUBLIC_KEY，RSA_PRIVATE_KEY
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static Map<String, Object> getKey() throws NoSuchAlgorithmException {
        return getKey(1024);
    }
    /**
     * 获取一对公钥和私钥
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
