package com.yujing.crypt.test;

import com.yujing.utils.YConvert;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.yujing.crypt.YOneBitCrypt.decrypt;
import static com.yujing.crypt.YOneBitCrypt.encrypt;
import static com.yujing.crypt.YOneBitCrypt.getPassword;

public class OneBitTest {
    public static void main(String[] args){

        {
            String content = "513434197011150056";
            // 加密
            byte[] target;
            target = content.getBytes(StandardCharsets.UTF_8);
            // byte password = (byte) ((int) (Math.random() * 256 - 128));
            byte password = (byte) 0x92;
            System.out.println("加密内容：" + content);
            System.out.println("加密密码：" + password);
            System.out.println("加密前：" + Arrays.toString(target));
            System.out.println(YConvert.bytesToHexString(target));
            byte[] mm = encrypt(target, password);
            System.out.println("加密后：" + Arrays.toString(mm));
            System.out.println(YConvert.bytesToHexString(mm));
            // 解密,先算出对称密码，再解密
            byte newpass = getPassword(password);
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }

        // 第0块：0901299BD20E783288F3140012022331
        // 第1块：1B42B5746CB7390A267E1C0D33AB6863
        // 第2块：AB0303BE9468350C0484317CCA207DE6
        // 第0块：52CA48D063F90B05E135D5426A54FC6A
        // 第1块：9E8A3CB539066C98151EC2550250E6FE
        // 第2块：C67E03831843E7B77407AAE2A39A4DE5
        // 第0块：9DEDAE9C9F43EE46414927C9A009FEFA
        // 第1块：07CDB429463C473F24AFDEFE080E0031

        // 090129 9BD20E783288F31400
        // 120223 311B42B5746CB7390A267E1C0D33AB6863AB
        // 0303BE 946835
        // 0C0484 317CCA207DE652CA48D063F9
        // 0B05E1 35D5426A54FC6A9E8A3CB5
        // 39066C 98151EC2550250E6FEC67E03831843E7B77407AAE2A39A4DE59DEDAE9C9F43EE46414927C9A009FEFA07CDB429463C473F24AFDEFE080E0031
        {
            byte newpass = getPassword((byte) 0x29);
            byte[] mm = YConvert.hexStringToByte("9BD20E783288F31400");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
        {
            byte newpass = getPassword((byte) 0x23);
            byte[] mm = YConvert.hexStringToByte("311B42B5746CB7390A267E1C0D33AB6863AB");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
        {
            byte newpass = getPassword((byte) 0xbe);
            byte[] mm = YConvert.hexStringToByte("946835");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
        {
            byte newpass = getPassword((byte) 0x84);
            byte[] mm = YConvert.hexStringToByte("317CCA207DE652CA48D063F9");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
        {
            byte newpass = getPassword((byte) 0xe1);
            byte[] mm = YConvert.hexStringToByte("35D5426A54FC6A9E8A3CB5");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
        {
            byte newpass = getPassword((byte) 0x6c);
            byte[] mm = YConvert.hexStringToByte("98151EC2550250E6FEC67E03831843E7B77407AAE2A39A4DE59DEDAE9C9F43EE46414927C9A009FEFA07CDB429463C473F24AFDEFE080E0031");
            byte[] re = decrypt(mm, newpass);
            // 打印结果
            System.out.println("解密后：" + new String(re));
        }
    }
}
