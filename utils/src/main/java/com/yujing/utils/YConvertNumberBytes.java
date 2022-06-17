package com.yujing.utils;

/**
 * Byte转换类，常用类型的转换
 *
 * @author 余静 2022年6月17日15:33:32
 * 大端小端模式，小端模式后缀Min
 */
@SuppressWarnings("unused")
public class YConvertNumberBytes {
    /**
     * 将byte按二进制位倒序
     *
     * @param i byte
     * @return byte
     */
    public static byte reverse(byte i) {
        // 0000 0001  --> 1000 0000
        i = (byte) ((i & 0x55) << 1 | (i >>> 1) & 0x55);
        i = (byte) ((i & 0x33) << 2 | (i >>> 2) & 0x33);
        i = (byte) ((i & 0x0f) << 4 | (i >>> 4) & 0x0f);
        return i;
    }

    /**
     * 将byte数组中的元素倒序排列
     *
     * @param b byte[]
     * @return byte[]
     */
    public static byte[] reverse(byte[] b) {
        //0102030405FF  -->  FF0504030201
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            result[b.length - i - 1] = b[i];
        }
        return result;
    }

    /**
     * 将int类型的值转换为字节序颠倒过来对应的int值
     *
     * @param i int
     * @return int
     */
    public static int reverse(int i) {
        //5  -->  83886080
        return YConvertNumberBytes.bytesToIntMin(YConvertNumberBytes.intToBytes(i));
    }

    /**
     * 将short类型的值转换为字节序颠倒过来对应的short值
     *
     * @param s short
     * @return short
     */
    public static short reverse(short s) {
        //5  -->  1280
        return YConvertNumberBytes.bytesToShortMin(YConvertNumberBytes.shortToBytes(s));
    }

    /**
     * 将float类型的值转换为字节序颠倒过来对应的float值
     *
     * @param f float
     * @return float
     */
    public static float reverse(float f) {
        return YConvertNumberBytes.bytesToFloat(YConvertNumberBytes.floatToBytes(f));
    }

    /**
     * 将byte数组中的元素按二进制位倒序排列
     *
     * @param b byte[]
     * @return byte[]
     */
    public static byte[] reverseOfBit(byte[] b) {
        //0102030405FF  -->  FFA020C04080
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            result[b.length - i - 1] = reverse(b[i]);
        }
        return result;
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param n 需要转换的long
     * @return 转换后的byte数组
     */
    public static byte[] longToBytes(long n) {
        //123456789  -->  00000000075BCD15
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param array long的byte数组
     * @return 值
     */
    public static long bytesToLong(byte[] array) {
        //00000000075BCD15  -->  123456789
        return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24) | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff)));
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param n 需要转换的long
     * @return 转换后的byte数组
     */
    public static byte[] longToBytesMin(long n) {
        //123456789  -->  15CD5B0700000000
        byte[] b = new byte[8];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        b[4] = (byte) (n >> 32 & 0xff);
        b[5] = (byte) (n >> 40 & 0xff);
        b[6] = (byte) (n >> 48 & 0xff);
        b[7] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param array long的byte数组
     * @return 值
     */
    public static long bytesToLongMin(byte[] array) {
        //15CD5B0700000000  -->  123456789
        return ((((long) array[7] & 0xff) << 56) | (((long) array[6] & 0xff) << 48) | (((long) array[5] & 0xff) << 40)
                | (((long) array[4] & 0xff) << 32) | (((long) array[3] & 0xff) << 24) | (((long) array[2] & 0xff) << 16)
                | (((long) array[1] & 0xff) << 8) | (((long) array[0] & 0xff)));
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param n      需要转换的long
     * @param array  目标数组
     * @param offset 目标数组的offset位置
     */
    public static byte[] longToBytes(long n, byte[] array, int offset) {
        byte[] value = longToBytes(n);
        System.arraycopy(value, 0, array, offset, 8);
        return value;
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param array  long的byte数组
     * @param offset long的byte数组，第offset位开始转换
     * @return 值
     */
    public static long bytesToLong(byte[] array, int offset) {
        return ((((long) array[offset] & 0xff) << 56) | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40) | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24) | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8) | (((long) array[offset + 7] & 0xff)));
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param n      需要转换的long
     * @param array  目标数组
     * @param offset 目标数组的offset位置
     */
    public static byte[] longToBytesMin(long n, byte[] array, int offset) {
        byte[] value = longToBytesMin(n);
        System.arraycopy(value, 0, array, offset, 8);
        return value;
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param array  long的byte数组
     * @param offset long的byte数组，第offset位开始转换
     * @return 值
     */
    public static long bytesToLongMin(byte[] array, int offset) {
        return ((((long) array[offset + 7] & 0xff) << 56) | (((long) array[offset + 6] & 0xff) << 48)
                | (((long) array[offset + 5] & 0xff) << 40) | (((long) array[offset + 4] & 0xff) << 32)
                | (((long) array[offset + 3] & 0xff) << 24) | (((long) array[offset + 2] & 0xff) << 16)
                | (((long) array[offset + 1] & 0xff) << 8) | (((long) array[offset] & 0xff)));
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n int
     * @return 转换后的byte数组
     */
    public static byte[] intToBytes(int n) {
        //123  -->  0000007B
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b int的byte数组
     * @return 值
     */
    public static int bytesToInt(byte[] b) {
        //0000007B  -->  123
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] intToBytesMin(int n, byte[] array, int offset) {
        byte[] value = intToBytesMin(n);
        System.arraycopy(value, 0, array, offset, 4);
        return value;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param b      byte数组
     * @param offset byte数组的第offset位开始转换
     * @return 值
     */
    public static int bytesToIntMin(byte[] b, int offset) {
        return b[offset] & 0xff | (b[offset + 1] & 0xff) << 8 | (b[offset + 2] & 0xff) << 16
                | (b[offset + 3] & 0xff) << 24;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] intToBytes(int n, byte[] array, int offset) {
        byte[] value = intToBytes(n);
        System.arraycopy(value, 0, array, offset, 4);
        return value;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b      byte数组
     * @param offset byte数组的第offset位开始转换
     * @return 值
     */
    public static int bytesToInt(byte[] b, int offset) {
        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8 | (b[offset + 1] & 0xff) << 16
                | (b[offset] & 0xff) << 24;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param n int
     * @return 转换后的byte数组
     */
    public static byte[] intToBytesMin(int n) {
        //123  -->  7B000000
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param b int的byte数组
     * @return 值
     */
    public static int bytesToIntMin(byte[] b) {
        //7B000000  -->  123
        return b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16 | (b[3] & 0xff) << 24;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     * 大端 如 F001 = F0*16 + 01  = 61441
     *
     * @param n int
     * @return 转换后的byte数组
     */
    public static byte[] intTo2Bytes(int n) {// 只能是0到65535之间的数不能为负数
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     * 大端 如 F001 = F0*16 + 01  = 61441
     *
     * @param b int的byte数组
     * @return 值
     */
    public static int bytes2ToInt(byte[] b) {
        return b[1] & 0xff | (b[0] & 0xff) << 8;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param n int
     * @return 转换后的byte数组
     */
    public static byte[] intTo2BytesMin(int n) {// 只能是0到65535之间的数不能为负数
        //123  -->  7B00
        byte[] b = new byte[2];
        b[1] = (byte) (n >> 8 & 0xff);
        b[0] = (byte) (n & 0xff);
        return b;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 小端模式，是指数据的高字节保存在内存的低地址中，右边要乘以指数
     * 小端 如 F001= F0 + 01*16 = 271
     *
     * @param b int的byte数组
     * @return 值
     */
    public static int bytes2ToIntMin(byte[] b) {
        //7B00  -->  123
        return b[0] & 0xff | (b[1] & 0xff) << 8;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 大端
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] intTo2Bytes(int n, byte[] array, int offset) {// 只能是0到65535之间的数不能为负数
        byte[] value = intTo2Bytes(n);
        System.arraycopy(value, 0, array, offset, 2);
        return value;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 大端
     *
     * @param b      byte数组
     * @param offset byte数组的第offset位开始转换
     * @return 值
     */
    public static int bytes2ToInt(byte[] b, int offset) {
        return b[offset + 1] & 0xff | (b[offset] & 0xff) << 8;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 小端
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] intTo2BytesMin(int n, byte[] array, int offset) {// 只能是0到65535之间的数不能为负数
        byte[] value = intTo2BytesMin(n);
        System.arraycopy(value, 0, array, offset, 2);
        return value;
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 2位byte
     * 小端
     *
     * @param b      byte数组
     * @param offset byte数组的第offset位开始转换
     * @return 值
     */
    public static int bytes2ToIntMin(byte[] b, int offset) {
        return b[offset] & 0xff | (b[offset + 1] & 0xff) << 8;
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n short
     * @return 转换后的byte数组
     */
    public static byte[] shortToBytes(short n) {
        //1234  -->  04D2
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b short的byte数组
     * @return 值
     */
    public static short bytesToShort(byte[] b) {
        //04D2  -->  1234
        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param n short
     * @return 转换后的byte数组
     */
    public static byte[] shortToBytesMin(short n) {
        //1234  -->  D204
        byte[] b = new byte[2];
        b[1] = (byte) ((n >> 8) & 0xff);
        b[0] = (byte) (n & 0xff);
        return b;
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param b short的byte数组
     * @return 值
     */
    public static short bytesToShortMin(byte[] b) {
        //D204  -->  1234
        return (short) (b[0] & 0xff | (b[1] & 0xff) << 8);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n      short
     * @param array  byte数组
     * @param offset byte数组第offset位开始转换
     */
    public static byte[] shortToBytes(short n, byte[] array, int offset) {
        byte[] value = shortToBytes(n);
        System.arraycopy(value, 0, array, offset, 2);
        return value;
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b      short的byte数组
     * @param offset byte数组第offset位开始转换
     * @return 值
     */
    public static short bytesToShort(byte[] b, int offset) {
        return (short) (b[offset + 1] & 0xff | (b[offset] & 0xff) << 8);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n      short
     * @param array  byte数组
     * @param offset byte数组第offset位开始转换
     */
    public static byte[] shortToBytesMin(short n, byte[] array, int offset) {
        byte[] value = shortToBytesMin(n);
        System.arraycopy(value, 0, array, offset, 2);
        return value;
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b      short的byte数组
     * @param offset byte数组第offset位开始转换
     * @return 值
     */
    public static short bytesToShortMin(byte[] b, int offset) {
        return (short) (b[offset] & 0xff | (b[offset + 1] & 0xff) << 8);
    }

    /**
     * double2Bytes
     *
     * @param d d
     * @return bytes
     */
    public static byte[] doubleToBytes(double d) {
        //123.45  -->  CDCCCCCCCCDC5E40
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    /**
     * bytes转Double
     *
     * @param bytes bytes
     * @return double
     */
    public static double bytesToDouble(byte[] bytes) {
        //CDCCCCCCCCDC5E40  -->  123.45
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    /**
     * double2Bytes
     * 小端
     *
     * @param d d
     * @return bytes
     */
    public static byte[] doubleToBytesMin(double d) {
        //123.45  -->  405EDCCCCCCCCCCD
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[7 - i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    /**
     * bytes转Double
     * 小端
     *
     * @param bytes bytes
     * @return double
     */
    public static double bytesToDoubleMin(byte[] bytes) {
        //405EDCCCCCCCCCCD  -->  123.45
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[7 - i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    /**
     * double 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param d      double
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] doubleToBytes(double d, byte[] array, int offset) {
        byte[] value = doubleToBytes(d);
        System.arraycopy(value, 0, array, offset, 8);
        return value;
    }

    /**
     * bytes转Double
     *
     * @param bytes 字节（至少4个字节）
     * @param index 开始位置
     * @return float
     */
    public static double bytesToDouble(byte[] bytes, int index) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[i + index] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    /**
     * double 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param d      double
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] doubleToBytesMin(double d, byte[] array, int offset) {
        byte[] value = doubleToBytesMin(d);
        System.arraycopy(value, 0, array, offset, 8);
        return value;
    }

    /**
     * bytes转Double
     * 小端
     *
     * @param bytes 字节（至少4个字节）
     * @param index 开始位置
     * @return float
     */
    public static double bytesToDoubleMin(byte[] bytes, int index) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[7 - i + index] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    /**
     * 浮点转换为字节
     *
     * @param f f
     * @return bytes
     */
    public static byte[] floatToBytes(float f) {
        //55.2 -->  CDCC5C42
        // 把float转换为byte[]
        int fBit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fBit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    /**
     * 字节转换为浮点
     *
     * @param bytes bytes
     * @return float
     */
    public static float bytesToFloat(byte[] bytes) {
        //CDCC5C42  -->  55.2
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((bytes[i] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }

    /**
     * 浮点转换为字节
     * 小端
     *
     * @param f f
     * @return bytes
     */
    public static byte[] floatToBytesMin(float f) {
        //55.2 -->  425CCCCD
        byte[] bytes = floatToBytes(f);
        return reverse(bytes);
    }

    /**
     * 字节转换为浮点
     * 小端
     *
     * @param bytes bytes
     * @return float
     */
    public static float bytesToFloatMin(byte[] bytes) {
        //425CCCCD  -->  55.2
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((bytes[3 - i] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }

    /**
     * float 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param f      float
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] floatToBytes(float f, byte[] array, int offset) {
        byte[] value = floatToBytes(f);
        System.arraycopy(value, 0, array, offset, 4);
        return value;
    }

    /**
     * 字节转换为浮点
     *
     * @param bytes 字节（至少4个字节）
     * @param index 开始位置
     * @return float
     */
    public static float bytesToFloat(byte[] bytes, int index) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((bytes[i + index] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }

    /**
     * float 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param f      float
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static byte[] floatToBytesMin(float f, byte[] array, int offset) {
        byte[] value = floatToBytesMin(f);
        System.arraycopy(value, 0, array, offset, 4);
        return value;
    }

    /**
     * 字节转换为浮点
     * 小端
     *
     * @param bytes 字节（至少4个字节）
     * @param index 开始位置
     * @return float
     */
    public static float bytesToFloatMin(byte[] bytes, int index) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((bytes[3 - i + index] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }
}