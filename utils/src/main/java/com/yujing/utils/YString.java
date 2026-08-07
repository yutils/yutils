package com.yujing.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 对String的一些处理方法
 *
 * @author 余静  2019年4月2日10:27:01
 */
/*用法
// 每隔 digit 位插入分隔符（末尾不多余分隔符）
YString.insert("AABBCCDD", 2, "-"); // → "AA-BB-CC-DD"
YString.insert("AABBCCD", 2, "-");  // → "AA-BB-CC-D"
YString.ToSBC("ABC");
YString.ToDBC("ＡＢＣ");
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class YString {
    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 字符串分组，每digit位字符拆分一次字符串，中文英文都算一个字符
     *
     * @param str   字符串
     * @param digit 位
     * @return 拆分后的字符串
     */
    public static List<StringBuilder> group(String str, int digit) {
        List<StringBuilder> strings = new ArrayList<>();
        if (str.length() < digit) {
            strings.add(new StringBuilder(str));
            return strings;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);//获取每一个字
            sb.append(c);
            if (i % digit == digit - 1) {//如果是digit的倍数就换行
                strings.add(sb);
                sb = new StringBuilder();
            }
        }
        if (sb.length() > 0) strings.add(sb);
        return strings;
    }


    /**
     * 字符串分组，每digit位字符拆分一次字符串，英文算一个字符，中文算两个字符
     *
     * @param str   字符串
     * @param digit 位
     * @return 拆分后的字符串
     */
    public static List<StringBuilder> groupDouble(String str, int digit) {
        if (str.length() < digit / 2) {
            List<StringBuilder> strings = new ArrayList<>();
            strings.add(new StringBuilder(str));
            return strings;
        }
        List<StringBuilder> strings = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);//获取每一个字
            index = (c >= 1 && c <= 127) ? index + 1 : index + 2;
            if (index > digit) {//如果大于就换行
                index = 0;
                strings.add(sb);
                sb = new StringBuilder();
                i--;
                continue;
            }
            sb.append(c);
            if (index >= digit) {//如果大于2倍就换行
                index = 0;
                strings.add(sb);
                sb = new StringBuilder();
            }
        }
        if (sb.length() > 0)
            strings.add(sb);
        return strings;
    }

    /**
     * 字符串分组，真实长度，每digit位字符拆分一次字符串，英文算一个字符，中文算两个或者3字符
     * <p>
     * 默认按 UTF-8 字节长度切分（无逐字 getBytes 分配）。
     *
     * @param str   字符串
     * @param digit 位
     * @return 拆分后的字符串
     */
    public static List<StringBuilder> groupActual(String str, int digit) {
        return groupActual(str, digit, null);
    }

    /**
     * 字符串分组，指定字符串编码
     * <p>
     * charset 为 null 或 UTF-8 时走无分配快路径；其它编码用 CharsetEncoder 复用缓冲，避免逐字 new String/getBytes。
     *
     * @param str     字符串
     * @param digit   位
     * @param charset 编码，null 视为 UTF-8
     * @return 拆分后的字符串
     */
    public static List<StringBuilder> groupActual(String str, int digit, Charset charset) {
        if (digit < 3) return group(str, digit);
        if (str.length() < digit / 3) {
            List<StringBuilder> strings = new ArrayList<>();
            strings.add(new StringBuilder(str));
            return strings;
        }
        if (charset == null || StandardCharsets.UTF_8.equals(charset)) {
            return groupActualUtf8(str, digit);
        }
        return groupActualByCharset(str, digit, charset);
    }

    /**
     * UTF-8 字节长度切分：按码点计算字节数，不分配临时 byte[]。
     */
    private static List<StringBuilder> groupActualUtf8(String str, int digit) {
        List<StringBuilder> strings = new ArrayList<>();
        int length = str.length();
        StringBuilder sb = new StringBuilder(Math.min(length, digit));
        int index = 0;
        for (int i = 0; i < length; ) {
            int cp = str.codePointAt(i);
            int charCount = Character.charCount(cp);
            int bytes = utf8ByteLength(cp);
            if (index > 0 && index + bytes > digit) {
                strings.add(sb);
                sb = new StringBuilder(Math.min(length - i, digit));
                index = 0;
                continue;
            }
            sb.appendCodePoint(cp);
            index += bytes;
            i += charCount;
            if (index >= digit) {
                strings.add(sb);
                sb = new StringBuilder(Math.min(length - i, digit));
                index = 0;
            }
        }
        if (sb.length() > 0) strings.add(sb);
        return strings;
    }

    private static int utf8ByteLength(int codePoint) {
        if (codePoint <= 0x7F) return 1;
        if (codePoint <= 0x7FF) return 2;
        if (codePoint <= 0xFFFF) return 3;
        return 4;
    }

    /**
     * 非 UTF-8：用复用的 CharsetEncoder 计算码点字节长度，避免逐字 String.valueOf().getBytes()。
     */
    private static List<StringBuilder> groupActualByCharset(String str, int digit, Charset charset) {
        CharsetEncoder encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        CharBuffer cb = CharBuffer.allocate(2);
        int maxBytes = Math.max(8, (int) Math.ceil(encoder.maxBytesPerChar() * 2) + 4);
        ByteBuffer bb = ByteBuffer.allocate(maxBytes);
        List<StringBuilder> strings = new ArrayList<>();
        int length = str.length();
        StringBuilder sb = new StringBuilder(Math.min(length, digit));
        int index = 0;
        for (int i = 0; i < length; ) {
            int cp = str.codePointAt(i);
            int charCount = Character.charCount(cp);
            int bytes = encodedByteLength(encoder, cb, bb, cp, charCount);
            if (index > 0 && index + bytes > digit) {
                strings.add(sb);
                sb = new StringBuilder(Math.min(length - i, digit));
                index = 0;
                continue;
            }
            sb.appendCodePoint(cp);
            index += bytes;
            i += charCount;
            if (index >= digit) {
                strings.add(sb);
                sb = new StringBuilder(Math.min(length - i, digit));
                index = 0;
            }
        }
        if (sb.length() > 0) strings.add(sb);
        return strings;
    }

    private static int encodedByteLength(CharsetEncoder encoder, CharBuffer cb, ByteBuffer bb, int cp, int charCount) {
        cb.clear();
        if (charCount == 1) {
            cb.put((char) cp);
        } else {
            cb.put(Character.highSurrogate(cp));
            cb.put(Character.lowSurrogate(cp));
        }
        cb.flip();
        bb.clear();
        encoder.reset();
        CoderResult cr = encoder.encode(cb, bb, true);
        if (cr.isOverflow()) {
            // 极端编码：回退到整码点编码长度
            return new String(Character.toChars(cp)).getBytes(encoder.charset()).length;
        }
        encoder.flush(bb);
        int bytes = bb.position();
        return bytes > 0 ? bytes : 1;
    }

    /**
     * 字符串每隔digit位添加一个符号（末尾不再追加多余分隔符）
     *
     * @param str          字符串
     * @param digit        每隔digit位添加一个符号
     * @param insertString 添加的符号
     * @return 结果，如 insert("AABBCCDD", 2, "-") → "AA-BB-CC-DD"
     */
    @SuppressWarnings("Annotator")
    public static String insert(String str, int digit, String insertString) {
        if (str == null || insertString == null || digit <= 0 || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length() + str.length() / digit * insertString.length());
        for (int i = 0; i < str.length(); i++) {
            if (i > 0 && i % digit == 0) {
                sb.append(insertString);
            }
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }
}
