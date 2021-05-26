package com.yujing.utils;

/**
 * Byte转换类，常用类型的转换
 *
 * @author 余静 2019年12月12日11:48:48
 * char、int、float、double和 byte[] 数组之间的转换关系还需继续研究实现。
 * 大端小端模式，小端模式后缀Min
 */
@SuppressWarnings("unused")
public class YConvertNumberBytes {
    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param n 需要转换的long
     * @return 转换后的byte数组
     */
    public static byte[] longToBytes(long n) {
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

    public static byte[] LTB(long n) {
        return longToBytes(n);
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param array long的byte数组
     * @return 值
     */
    public static long bytesToLong(byte[] array) {
        return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24) | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff)));
    }

    public static long BTL(byte[] array) {
        return bytesToLong(array);
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

    public static byte[] LTBM(long n) {
        return longToBytesMin(n);
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
        return ((((long) array[7] & 0xff) << 56) | (((long) array[6] & 0xff) << 48) | (((long) array[5] & 0xff) << 40)
                | (((long) array[4] & 0xff) << 32) | (((long) array[3] & 0xff) << 24) | (((long) array[2] & 0xff) << 16)
                | (((long) array[1] & 0xff) << 8) | (((long) array[0] & 0xff)));
    }

    public static long BTLM(byte[] array) {
        return bytesToLongMin(array);
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 大端模式，是指数据的高字节保存在内存的高地址中，左边要乘以指数
     *
     * @param n      需要转换的long
     * @param array  目标数组
     * @param offset 目标数组的offset位置
     */
    public static void longToBytes(long n, byte[] array, int offset) {
        byte[] value = longToBytes(n);
        System.arraycopy(value, 0, array, offset, 8);
    }

    public static void LTB(long n, byte[] array, int offset) {
        longToBytes(n, array, offset);
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

    public static long BTL(byte[] array, int offset) {
        return bytesToLong(array, offset);
    }

    /**
     * long 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param n      需要转换的long
     * @param array  目标数组
     * @param offset 目标数组的offset位置
     */
    public static void longToBytesMin(long n, byte[] array, int offset) {
        byte[] value = longToBytesMin(n);
        System.arraycopy(value, 0, array, offset, 8);
    }

    public static void LTBM(long n, byte[] array, int offset) {
        longToBytesMin(n, array, offset);
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

    public static long BTLM(byte[] array, int offset) {
        return bytesToLongMin(array, offset);
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n int
     * @return 转换后的byte数组
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte[] ITB(int n) {
        return intToBytes(n);
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b int的byte数组
     * @return 值
     */
    public static int bytesToInt(byte[] b) {
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }

    public static int BTI(byte[] b) {
        return bytesToInt(b);
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     * 小端
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static void intToBytesMin(int n, byte[] array, int offset) {
        byte[] value = intToBytesMin(n);
        System.arraycopy(value, 0, array, offset, 4);
    }

    public static void ITBM(int n, byte[] array, int offset) {
        intToBytesMin(n, array, offset);
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

    public static int BTIM(byte[] b, int offset) {
        return bytesToIntMin(b, offset);
    }

    /**
     * int 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n      int
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static void intToBytes(int n, byte[] array, int offset) {
        byte[] value = intToBytes(n);
        System.arraycopy(value, 0, array, offset, 4);
    }

    public static void ITB(int n, byte[] array, int offset) {
        intToBytes(n, array, offset);
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

    public static int BTI(byte[] b, int offset) {
        return bytesToInt(b, offset);
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
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte[] ITBM(int n) {
        return intToBytesMin(n);
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
        return b[0] & 0xff | (b[1] & 0xff) << 8 | (b[2] & 0xff) << 16 | (b[3] & 0xff) << 24;
    }

    public static int BTIM(byte[] b) {
        return bytesToIntMin(b);
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

    public static byte[] IT2B(int n) {
        return intTo2Bytes(n);
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

    public static int B2TI(byte[] b) {
        return bytes2ToInt(b);
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
        byte[] b = new byte[2];
        b[1] = (byte) (n >> 8 & 0xff);
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static byte[] IT2BM(int n) {
        return intTo2BytesMin(n);
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
        return b[0] & 0xff | (b[1] & 0xff) << 8;
    }

    public static int B2TIM(byte[] b) {
        return bytes2ToIntMin(b);
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
    public static void intTo2Bytes(int n, byte[] array, int offset) {// 只能是0到65535之间的数不能为负数
        byte[] value = intTo2Bytes(n);
        System.arraycopy(value, 0, array, offset, 2);
    }

    public static void IT2B(int n, byte[] array, int offset) {
        intTo2Bytes(n, array, offset);
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

    public static int B2TI(byte[] b, int offset) {
        return bytes2ToInt(b, offset);
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
    public static void intTo2BytesMin(int n, byte[] array, int offset) {// 只能是0到65535之间的数不能为负数
        byte[] value = intTo2BytesMin(n);
        System.arraycopy(value, 0, array, offset, 2);
    }

    public static void IT2BM(int n, byte[] array, int offset) {
        intTo2BytesMin(n, array, offset);
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

    public static int B2TIM(byte[] b, int offset) {
        return bytes2ToIntMin(b, offset);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n short
     * @return 转换后的byte数组
     */
    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static byte[] STB(short n) {
        return shortToBytes(n);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param b short的byte数组
     * @return 值
     */
    public static short bytesToShort(byte[] b) {
        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

    public static short BTS(byte[] b) {
        return bytesToShort(b);
    }

    /**
     * short 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n      short
     * @param array  byte数组
     * @param offset byte数组第offset位开始转换
     */
    public static void shortToBytes(short n, byte[] array, int offset) {
        byte[] value = shortToBytes(n);
        System.arraycopy(value, 0, array, offset, 2);
    }

    public static void STB(short n, byte[] array, int offset) {
        shortToBytes(n, array, offset);
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

    public static short BTS(byte[] b, int offset) {
        return bytesToShort(b, offset);
    }

    /**
     * double2Bytes
     *
     * @param d d
     * @return bytes
     */
    public static byte[] doubleToBytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static byte[] DTB(double d) {
        return doubleToBytes(d);
    }

    /**
     * bytes转Double
     *
     * @param bytes bytes
     * @return double
     */
    public static double bytesToDouble(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static double BTD(byte[] bytes) {
        return bytesToDouble(bytes);
    }

    /**
     * double 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param d      double
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static void doubleToBytes(double d, byte[] array, int offset) {
        byte[] value = doubleToBytes(d);
        System.arraycopy(value, 0, array, offset, 8);
    }

    public static void DTB(double d, byte[] array, int offset) {
        doubleToBytes(d, array, offset);
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

    public static double BTD(byte[] bytes, int index) {
        return bytesToDouble(bytes, index);
    }

    /**
     * 浮点转换为字节
     *
     * @param f f
     * @return bytes
     */
    public static byte[] floatToBytes(float f) {
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

    public static byte[] FTB(float f) {
        return floatToBytes(f);
    }

    /**
     * 字节转换为浮点
     *
     * @param bytes bytes
     * @return float
     */
    public static float bytesToFloat(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((bytes[i] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }

    public static float BTF(byte[] bytes) {
        return bytesToFloat(bytes);
    }

    /**
     * float 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param f      float
     * @param array  byte数组
     * @param offset int数组的第offset位
     */
    public static void floatToBytes(float f, byte[] array, int offset) {
        byte[] value = floatToBytes(f);
        System.arraycopy(value, 0, array, offset, 4);
    }

    public static void FTB(float f, byte[] array, int offset) {
        floatToBytes(f, array, offset);
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

    public static float BTF(byte[] bytes, int index) {
        return bytesToFloat(bytes, index);
    }
}