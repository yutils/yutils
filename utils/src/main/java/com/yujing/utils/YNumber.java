package com.yujing.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * 数字处理类
 *
 * @author 余静 2018年5月15日19:04:31
 * BigDecimal.ROUND_CEILING;//如果大于0就和ROUND_UP一样，小于0就和ROUND_DOWN一样
 * BigDecimal.ROUND_DOWN;//超过自定位数就舍
 * BigDecimal.ROUND_FLOOR;//如果大于0就和ROUND_DOWN一样，小于0就和ROUND_UP一样
 * BigDecimal.ROUND_HALF_DOWN;//四舍五入
 * BigDecimal.ROUND_HALF_EVEN;//偶数舍奇数入
 * BigDecimal.ROUND_HALF_UP;//四舍五入
 * BigDecimal.ROUND_UNNECESSARY;//精确模式不需要舍入
 * BigDecimal.ROUND_UP;//超过自定位数就入
 */
@SuppressWarnings("unused")
public class YNumber {
    public static final String INTEGER = "^-?(([1-9]\\d*$)|0)";
    public static final String DOUBLE = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";

    //int类型判断
    public static boolean isInt(String value) {
        return Pattern.matches(INTEGER, value);
    }

    //double类型判断
    public static boolean isDouble(String decimals) {
        return Pattern.matches(DOUBLE, decimals);
    }

    //保留小数
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        return bd.doubleValue();
    }

    //加
    public static double sum(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }

    //减
    public static double sub(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    //乘
    public static double multiply(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }

    //除
    public static double divide(double d1, double d2, int scale) {
        if (d2 == 0) {
            System.err.println("分母是0");
            return 0d;
        }
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //四舍五入
    public static double rounding(double d, int scale) {
        return new BigDecimal(String.valueOf(d)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //四舍五入
    public static float rounding(float f, int scale) {
        return new BigDecimal(String.valueOf(f)).setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    //2位小数
    public static String D2S(double d) {
        return D2S(d, 2);
    }

    //2位小数
    public static String S2S(String s) {
        return S2S(s, 2);
    }

    //2位小数
    public static double D2D(double d) {
        return D2D(d, 2);
    }

    //2位小数
    public static double S2D(String s) {
        return S2D(s, 2);
    }

    //scale位小数
    public static String D2S(double d, int scale) {
        return showNumber(d, scale);
    }

    //scale位小数
    public static String S2S(String s, int scale) {
        if (s == null || s.isEmpty() || (isDouble(s) && isInt(s)))
            return "0";
        return D2S(Double.parseDouble(s), scale);
    }

    //scale位小数
    public static double D2D(double d, int scale) {
        return Double.parseDouble(D2S(d, scale));
    }

    //scale位小数
    public static double S2D(String s, int scale) {
        return Double.parseDouble(S2S(s,scale));
    }

    //不显示科学计数，默认2位小数
    public static String showNumber(double d) {
        return showNumber(d, 2);
    }

    //不显示科学计数
    public static String showNumber(double d, int scale) {
        StringBuilder mat = new StringBuilder("#");
        if (scale > 0) {
            mat.append(".");
            for (int i = 0; i < scale; i++)
                mat.append("#");
        }
        DecimalFormat df = new DecimalFormat(mat.toString());
        return df.format(d);
    }

    //不显示科学计数，默认2位小数，不够的填充0
    public static String fill(double d) {
        return fill(d, 2);
    }

    //不显示科学计数，不够的填充0
    public static String fill(double d, int scale) {
        StringBuilder mat = new StringBuilder("0");
        if (scale > 0) {
            mat.append(".");
            for (int i = 0; i < scale; i++)
                mat.append("0");
        }
        DecimalFormat df = new DecimalFormat(mat.toString());
        return df.format(d);
    }
}
