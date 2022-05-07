package com.yujing.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * YBase64 非标准base64，+换成了-，base64DecodeChars的第43位换成了45位
 * (意思是：Encode中的62,本来是+，对应的Decode中的位置是43，但是现在变成了-，所以62位置就放在了45位置上)
 * 以为在http传输中+会被转义导致结果变化
 *
 * @author 余静
 * 2017年3月13日 下午17:49:30
 */
@SuppressWarnings("unused")
public class YBase64ToHTTP {
    private static final char[] ENCODE = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',// 0-9
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',// 10-19
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',// 20-29
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',// 30-39
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',// 40-49
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',// 50-59
            '8', '9', '-', '/'};// 60-63
    //表明了byte0-128中，对应上图的位置。比如base64DecodeChars[65]=0,0对应base64EncodeChars[0]=A
    private static final byte[] DECODE = new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,//0-9
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,//10-19
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,//20-29
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,//30-39
            -1, -1, -1, -1, -1, 62, -1, 63, 52, 53,//40-49
            54, 55, 56, 57, 58, 59, 60, 61, -1, -1,//50-59
            -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,//60-69
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14,//70-79
            15, 16, 17, 18, 19, 20, 21, 22, 23,//80-89
            24, 25, -1, -1, -1, -1, -1, -1, 26,//90-99
            27, 28, 29, 30, 31, 32, 33, 34, 35,//100-109
            36, 37, 38, 39, 40, 41, 42, 43, 44,//110-119
            45, 46, 47, 48, 49, 50, 51, -1, -1,//120-129
            -1, -1, -1};

    public static String encodeString(String data) {
        return encode(data.getBytes());
    }

    /**
     * 编码成base64
     */
    @SuppressWarnings("DuplicateExpressions")
    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(ENCODE[b1 >>> 2]);
                sb.append(ENCODE[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(ENCODE[b1 >>> 2]);
                sb.append(ENCODE[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(ENCODE[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(ENCODE[b1 >>> 2]);
            sb.append(ENCODE[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(ENCODE[((b2 & 0x0f) << 2)
                    | ((b3 & 0xc0) >>> 6)]);
            sb.append(ENCODE[b3 & 0x3f]);
        }

        return sb.toString();
    }

    public static String decodeString(String str) {
        return new String(Objects.requireNonNull(decode(str)));
    }

    /**
     * 解码base64
     */
    public static byte[] decode(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] data;
        data = str.getBytes(StandardCharsets.US_ASCII);
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            /* b1 */
            do {
                b1 = DECODE[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1)
                break;
            /* b2 */
            do {
                b2 = DECODE[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
            /* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61)
                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                b3 = DECODE[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
            /* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61)
                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                b4 = DECODE[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
