package com.yujing.crypt;

/**
 * 一位密码加密算法,对称加密算法，给Bytes加密，密码为一位byte，密码有256中可能，为(-128~127)之间任意数字
 *
 * @author 余静 2019年8月27日16:57:19
 */
@SuppressWarnings("unused")
public class YOneBitCrypt {
    /**
     * 一位加密，第一步奇数偶数位置互换，比如原始byte数组为[1,2,3,4,5,6,7,8],第一步过后数组为[2,1,4,3,5,6,8,7]。
     * 第二步，每位加上一个值，值=(密码+当前位数)+((当前位数+1)*2)+(3*(密码+2))。值为byte，值在-128~127之间的一个数。
     * 比如当前密码为 byte=1; 加密byte数组为[1,2,3,4,5,6,7,8]， 加密后=[14, 16, 22, 24, 30, 32,
     * 37, 41] 这样加密后完全没有规律可寻，及时密码错一位，解密结果也看不出任何意义
     *
     * @param bytes    数据
     * @param password 一位的密码
     * @return 结果
     */
    public static byte[] encrypt(byte[] bytes, byte password) {
        byte temp;
        for (int i = 0; i < bytes.length - 1; i += 2) {
            temp = bytes[i];
            bytes[i] = bytes[i + 1];
            bytes[i + 1] = temp;
        }
        password = getPassword(password);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] += 67 * i + i * i * password;
        }
        return bytes;
    }

    /**
     * 机密算法，与上面加密算法反之
     *
     * @param bytes    数据
     * @param password 一位密码
     * @return 结果
     */
    public static byte[] decrypt(byte[] bytes, byte password) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] -= 67 * i + i * i * password;
        }
        byte temp;
        for (int i = 0; i < bytes.length - 1; i += 2) {
            temp = bytes[i];
            bytes[i] = bytes[i + 1];
            bytes[i + 1] = temp;
        }
        return bytes;
    }

    /**
     * 计算对称密码
     *
     * @param password 原始密码
     * @return 对称后密码
     */
    public static byte getPassword(byte password) {
        return (byte) (password + ((0xFF >> 1) + 1));
    }
}
