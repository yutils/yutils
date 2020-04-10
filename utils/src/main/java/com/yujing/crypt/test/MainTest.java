
package com.yujing.crypt.test;

import com.yujing.crypt.YAes;
import com.yujing.crypt.YRsa;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;

public class MainTest {
    public static void main(String[] args) throws Exception {
        // AES加密
        {
            String en = YAes.encryptToBase64("1111111111111余静11111111111               ", "wtugeqh");
            String decy = YAes.decryptFromBase64(en, "wtugeqh");
            System.out.println(en);
            System.out.println(decy);
        }
        // AES加密
        {
            byte[] en = YAes.encrypt("1111111111111余静11111111111".getBytes(), "wtugeqh");
            byte[] decy = YAes.decrypt(en, "wtugeqh");
            // decy[4]=0;
            System.out.println(Arrays.toString(decy));
            System.out.println(new String(decy));
        }
        // RSA
        {
            HashMap<String, Object> map = YRsa.getKeys();
            // 生成公钥和私钥
            RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
            RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
            // 模
            String modulus = publicKey.getModulus().toString();
            System.out.println("modulus:" + modulus);
            // 公钥指数
            String public_exponent =
                    publicKey.getPublicExponent().toString();
            System.out.println("public_exponent：" + public_exponent);
            // 私钥指数
            String private_exponent =
                    privateKey.getPrivateExponent().toString();
            System.out.println("private_exponent：" + private_exponent);

            // 明文
            String ming = "1234567890余静";
            // 使用模和指数生成公钥和私钥
            RSAPublicKey pubKey = YRsa.getPublicKey(modulus, public_exponent);
            RSAPrivateKey priKey = YRsa.getPrivateKey(modulus,
                    private_exponent);
            System.out.println("pubKey：" + pubKey);
            System.out.println("priKey：" + priKey);

            // 加密后的密文
            String mi = YRsa.encryptByPublicKey(ming, pubKey);
            System.out.println("密文：" + mi);
            // 解密后的明文
            ming = YRsa.decryptByPrivateKey(mi, priKey);
            System.out.println("解密：" + ming);
        }
    }
}
