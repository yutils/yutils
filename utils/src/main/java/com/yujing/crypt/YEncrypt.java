package com.yujing.crypt;

import com.yujing.utils.YBase64;

/**
 * 加密解密算法
 *
 * @author 余静 2017年3月29日 下午6:16:57
 */
@SuppressWarnings("unused")
@Deprecated
public class YEncrypt {

    public YEncrypt() {
    }

    public String encode(String str) {
        return YBase64.encode(encode(str.getBytes()));
    }

    public String decode(String psw) {
        return new String(decode(YBase64.decode(psw)));
    }

    public String encode(String str, String passWord) {
        return YBase64.encode(encode(str.getBytes(), passWord));
    }

    public String decode(String psw, String passWord) {
        return new String(decode(YBase64.decode(psw), passWord));
    }

    private byte[] intToByte(int int65535) {// 只能是0到65535之间的数不能为负数
        return new byte[]{(byte) (int65535 >> 8), (byte) (int65535 % 256)};
    }

    private int byteToInt(byte[] byte65535) {
        return ((byte65535[0] & 0xFF) << 8) + (byte65535[1] & 0xFF);// byte65535[0]&0xFF把byte转0-255
    }

    // 加密
    public byte[] encode(byte[] byteArray) {
        int key1 = (int) (Math.random() * 65535);
        int key2 = (int) (Math.random() * 65535);
        byte[] bytes = new byte[byteArray.length + 6];
        bytes[2] = intToByte(key1)[0];
        bytes[3] = intToByte(key1)[1];
        bytes[4] = intToByte(key2)[0];
        bytes[5] = intToByte(key2)[1];
        byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
        byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
        long xy = key1 + key2;// 校验位
        for (int i = 0; i < byteArray.length; i++) {
            xy += byteArray[i];
            bytes[i + 6] = (byte) ((int) byteArray[i] + (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
        }
        xy = Math.abs(xy);
        bytes[0] = intToByte((int) xy % 65535)[0];
        bytes[1] = intToByte((int) xy % 65535)[1];
        return bytes;
    }

    // 加密
    public byte[] encode(byte[] byteArray, String passWord) {
        byte[] p = passWord.getBytes();
        int key1 = (int) (Math.random() * 65535);
        int key2 = (int) (Math.random() * 65535);
        byte[] bytes = new byte[byteArray.length + 6];
        bytes[2] = intToByte(key1)[0];
        bytes[3] = intToByte(key1)[1];
        bytes[4] = intToByte(key2)[0];
        bytes[5] = intToByte(key2)[1];
        byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
        byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
        long xy = key1 + key2;// 校验位
        for (int i = 0; i < byteArray.length; i++) {
            xy += byteArray[i];
            bytes[i + 6] = (byte) ((int) byteArray[i] + p[i % p.length] + (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
        }
        xy = Math.abs(xy);
        bytes[0] = intToByte((int) xy % 65535)[0];
        bytes[1] = intToByte((int) xy % 65535)[1];
        return bytes;
    }

    // 解密
    public byte[] decode(byte[] byteArray) {
        int xy = byteToInt(new byte[]{byteArray[0], byteArray[1]});// 校验位
        int key1 = byteToInt(new byte[]{byteArray[2], byteArray[3]});
        int key2 = byteToInt(new byte[]{byteArray[4], byteArray[5]});
        byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
        byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
        byte[] bytes = new byte[byteArray.length - 6];
        long xy1 = key1 + key2;// 校验位
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((int) byteArray[i + 6] - (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
            xy1 += bytes[i];
        }
        xy1 = Math.abs(xy1);
        if ((xy1 % 65535) == xy) {
            return bytes;
        } else {
            return new byte[]{-23, -108, -103, -24, -81, -81, -17, -68, -127, -27, -83, -105, -25, -84, -90, -28, -72, -78, -24, -94, -85, -28, -65, -82, -26, -108, -71, -24, -65, -121, -17, -68, -127};
        }
    }

    // 解密
    public byte[] decode(byte[] byteArray, String passWord) {
        byte[] p = passWord.getBytes();
        int xy = byteToInt(new byte[]{byteArray[0], byteArray[1]});// 校验位
        int key1 = byteToInt(new byte[]{byteArray[2], byteArray[3]});
        int key2 = byteToInt(new byte[]{byteArray[4], byteArray[5]});
        byte[] key3 = Double.toString(Math.sqrt(key2 / (key1 + 0.1))).getBytes();
        byte[] key4 = Double.toString(Math.sqrt(key1 / (key2 + 0.1))).getBytes();
        byte[] bytes = new byte[byteArray.length - 6];
        long xy1 = key1 + key2;// 校验位
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((int) byteArray[i + 6] - p[i % p.length] - (key3[i % key3.length] * key4[i % key4.length] + key3[(i + 1) % key3.length] + key3[(i + 3) % key3.length] + key3[(i + 5) % key3.length] + key3[(i + 7) % key3.length] + key4[(i + 2) % key4.length] + key4[(i + 4) % key4.length] + key4[(i + 6) % key4.length] + key4[(i + 8) % key4.length]));
            xy1 += bytes[i];
        }
        xy1 = Math.abs(xy1);
        if ((xy1 % 65535) == xy) {
            return bytes;
        } else {//密码错误或字符串被修改过！
            return new byte[]{-27, -81, -122, -25, -96, -127, -23, -108, -103, -24, -81, -81, -26, -120, -106, -27, -83, -105, -25, -84, -90, -28, -72, -78, -24, -94, -85, -28, -65, -82, -26, -108, -71, -24, -65, -121, -17, -68, -127};
        }
    }

    // 100个随机数字
    private byte[] m = new byte[]{0, 8, 5, 1, 6, 1, 4, 4, 1, 0, 1, 9, 9, 0, 0, 5, 2, 5, 0, 7, 3, 3, 7, 3, 2, 1, 7, 0, 0, 4, 0, 1, 0, 2, 0, 8, 0, 1, 6, 2, 0, 4, 2, 0, 2, 0, 2, 5, 0, 6, 9, 0, 8, 3, 0, 3, 4, 8, 4, 3, 0, 9, 5, 0, 4, 3, 4, 0, 0, 0, 8, 0, 0, 5, 2, 5, 0, 5, 5, 0, 0, 9, 6, 2, 0, 4, 6, 0, 6, 0, 8, 7, 0, 1, 1, 9, 0, 7, 0, 7};

    public byte[] encodeFast(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] += m[i % (m.length)];
        }
        return byteArray;
    }

    public byte[] decodeFast(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] -= m[i % (m.length)];
        }
        return byteArray;
    }
}