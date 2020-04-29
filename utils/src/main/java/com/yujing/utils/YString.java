package com.yujing.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 对String的一些处理方法
 *
 * @author yujing  2019年4月2日10:27:01
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);//获取每一个字
            sb.append(c);
            if (i % digit == digit - 1) {//如果是digit的倍数就换行
                strings.add(sb);
                sb = new StringBuilder();
            }
        }
        if (sb.length() > 0)
            strings.add(sb);
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
     *
     * @param str     字符串
     * @param digit   位
     * @param charset 编码
     * @return 拆分后的字符串
     */
    public static List<StringBuilder> groupActual(String str, int digit, Charset charset) {
        if (digit < 3) return group(str, digit);
        List<StringBuilder> strings = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);//获取每一个字
            if (charset != null)
                index += String.valueOf(c).getBytes(charset).length;
            else
                index += String.valueOf(c).getBytes().length;

            //如果大于就换行
            if (index > digit) {
                index = 0;
                strings.add(sb);
                sb = new StringBuilder();
                i--;
                continue;
            }
            //连接字符串
            sb.append(c);
            if (index == digit) {//如果大于就换行
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
     * 字符串每隔digit位添加一个符号
     *
     * @param str          字符串
     * @param digit        每隔digit位添加一个符号
     * @param insertString 添加的符号
     * @return 结果
     */
    @SuppressWarnings("Annotator")
    public static String insert(String str, int digit, String insertString) {
        String regex = "(.{" + digit + "})";
        return str.replaceAll(regex, "$1" + insertString);
    }
}
