package com.yujing.crypt.test;

import com.yujing.crypt.YDes;
import com.yujing.utils.YConvert;

import java.util.Scanner;

/**
 * 通过银行卡卡号计算keyB
 *
 * @author Yujing 2017年8月8日10:30:02
 * KB计算过程 （1）例如卡号为6221 5165 7000 0000 047 （2）取最后16位转为ASCII码为 CardCode =
 * 31 35 31 36 35 37 30 30 30 30 30 30 30 30 34 37 （3）原始密钥使用12 D6 C5 C0
 * B8 27 E6 0A 9F 34 4D B9 4B CF 20
 * 39对CardCode进行3DES运算，ECB模式，算法短字节填充方式为不填充，计算得到 SESKEY = 30 3D F7 C3 7D
 * 7B 3B F0 28 6C A0 34 E0 09 E9 BA （4）使用SESKEY 对字节数组 11 22 33 44 55 66
 * 77 88进行3DES运算，ECB模式，算法短字节填充方式为不填充，计算得到 CardKey = D2 44 F9 41 F3 E9 2D
 * 4B （5）取最后6字节作为 KeyB = F9 41 F3 E9 2D 4B
 */
public class CalcKeyB {

    private static final byte[] key = YConvert.hexStringToByte("12D6C5C0B827E60A9F344DB94BCF203912D6C5C0B827E60A");
    private static final String min = "1122334455667788";
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                String KeyB = encode(arg);
                System.out.println(KeyB);
            }
        } else {
            System.out.println("KeyB密码计算器");
            while (true) {
                System.out.println("请输入银行卡号,输入0退出！");
                @SuppressWarnings("resource")
                String card = new Scanner(System.in).nextLine();
                if (card.equals("0")) {
                    System.out.println("退出程序");
                    break;
                } else {
                    if (card.length() < 16 || card.length() > 19) {
                        System.out.println("输入错误！");
                    } else {
                        System.out.println("KEYB的值为:" + encode(card));
                    }
                }
            }
            System.exit(0);
        }
    }

    public static String encode(String card) {
        if (card.length() < 16)
            return null;
        card = card.substring(card.length() - 16);
        byte[] mm = YDes.encode(card.getBytes(), key);
        byte[] key2 = new byte[24];
        for (int i = 0; i < key2.length; i++) {
            key2[i] = mm[i % mm.length];
        }
        byte[] mm2 = YDes.encode(YConvert.hexStringToByte(min), key2);
        String str = YConvert.bytesToHexString(mm2);
        return str.substring(str.length() - 12);
    }
}
