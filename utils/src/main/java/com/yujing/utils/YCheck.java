package com.yujing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 各种检查验证数据是否合法
 */
@SuppressWarnings("unused")
public class YCheck {

    /**
     * Email正则表达式="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
     */
    public static final String EMAIL = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
    /**
     * 手机号码正则表达式=^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$,扩充太多，11位判断。还有9开头的手机号
     */
    public static final String MOBILE = "\\d{11}";
    /**
     * Integer正则表达式 ^-?(([1-9]\d*$)|0)
     */
    public static final String INTEGER = "^-?(([1-9]\\d*$)|0)";
    /**
     * 正整数正则表达式 大于=0 ^[1-9]\d*|0$
     */
    public static final String INTEGER_NEGATIVE = "^[1-9]\\d*|0$";
    /**
     * 负整数正则表达式 小于=0 ^-[1-9]\d*|0$
     */
    public static final String INTEGER_POSITIVE = "^-[1-9]\\d*|0$";
    /**
     * Double正则表达式 ^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$
     */
    public static final String DOUBLE = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
    /**
     * 正Double正则表达式 大于=0  ^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0$
     */
    public static final String DOUBLE_NEGATIVE = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$";
    /**
     * 负Double正则表达式 小于= 0  ^(-([1-9]\d*\.\d*|0\.\d*[1-9]\d*))|0?\.0+|0$
     */
    public static final String DOUBLE_POSITIVE = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$";
    /**
     * 年龄正则表达式 ^(?:[1-9][0-9]?|1[01][0-9]|120)$ 匹配0-120岁
     */
    public static final String AGE = "^(?:[1-9][0-9]?|1[01][0-9]|120)$";
    /**
     * 邮编正则表达式  [0-9]\d{5}(?!\d) 国内6位邮编
     */
    public static final String ZIP_CODE = "[0-9]\\d{5}(?!\\d)";
    /**
     * 匹配由数字、26个英文字母或者下划线组成的字符串 ^\w+$
     */
    public static final String STR_ENG_NUM_ = "^\\w+$";
    /**
     * 匹配由数字和26个英文字母组成的字符串 ^[A-Za-z0-9]+$
     */
    public static final String STR_ENG_NUM = "^[A-Za-z0-9]+";
    /**
     * 匹配由26个英文字母组成的字符串  ^[A-Za-z]+$
     */
    public static final String STR_ENG = "^[A-Za-z]+$";
    /***
     * 日期正则 支持：
     *  YYYY-MM-DD
     *  YYYY/MM/DD
     *  YYYY_MM_DD
     *  YYYYMMDD
     *  YYYY.MM.DD的形式
     */
    public static final String DATE_ALL = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(10|12|0?[13578])([-\\/\\._]?)(3[01]|[12][0-9]|0?[1-9])$)" +
            "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(11|0?[469])([-\\/\\._]?)(30|[12][0-9]|0?[1-9])$)" +
            "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(0?2)([-\\/\\._]?)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([3579][26]00)" +
            "([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)" +
            "|(^([1][89][0][48])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][0][48])([-\\/\\._]?)" +
            "(0?2)([-\\/\\._]?)(29)$)" +
            "|(^([1][89][2468][048])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._]?)(0?2)" +
            "([-\\/\\._]?)(29)$)|(^([1][89][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|" +
            "(^([2-9][0-9][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$))";
    /**
     * URL正则表达式
     * 匹配 http www ftp
     */
    public static final String URL = "^(http|https|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?" +
            "(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*" +
            "(\\w*:)*(\\w*\\+)*(\\w*\\.)*" +
            "(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
    /**
     * 匹配数字组成的字符串  ^[0-9]+$
     */
    public static final String STR_NUM = "^[0-9]+$";
    /**
     * 校验ipv4
     */
    public static final String IPV4 = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
    /**
     * 校验ipv6
     */
    public static final String IPV6 = "^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:)|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){1,6})|(:(:[0-9A-Fa-f]{1,4}){1,7})|(([0-9A-Fa-f]{1,4}:){6}(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){0,4}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})|(:(:[0-9A-Fa-f]{1,4}){0,5}:(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}))$";
    /**
     * 校验端口
     */
    public static final String PORT = "([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])";
    /**
     * 校验是中文字符
     */
    public static final String CHINESE = "^[\\u4E00-\\u9FA5]+$";
    /**
     * 校验http或https
     */
    public static final String HTTP = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";

    /**
     * 校验ipv4
     *
     * @param str str
     * @return 是否通过
     */

    public static boolean isIPv4(String str) {
        return Regular(str, IPV4);
    }

    /**
     * 校验ipv6
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isIPv6(String str) {
        return Regular(str, IPV6);
    }

    /**
     * 校验端口
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isPort(String str) {
        return Regular(str, PORT);
    }

    /***
     * 校验中文
     *         @param str str
     *       @return 是否通过
     */
    public static boolean isChinese(String str) {
        return Regular(str, CHINESE);
    }

    /***
     * 校验http
     *         @param str str
     *       @return 是否通过
     */
    public static boolean isHttp(String str) {
        return Regular(str, HTTP);
    }

    /**
     * 判断字段是否为Email 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isEmail(String str) {
        return Regular(str, EMAIL);
    }

    /**
     * 判断是否为手机号码 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isMobile(String str) {
        return Regular(str, MOBILE);
    }

    /**
     * 判断是否为Url 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isUrl(String str) {
        return Regular(str, URL);
    }

    /**
     * 判断字段是否为数字 正负整数 正负浮点数 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isNumber(String str) {
        return isDouble(str) || isInteger(str);
    }

    /**
     * 判断字段是否为INTEGER  符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isInteger(String str) {
        return Regular(str, INTEGER);
    }

    /**
     * 判断字段是否为正整数正则表达式 大于=0 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isInteger_NEGATIVE(String str) {
        return Regular(str, INTEGER_NEGATIVE);
    }

    /**
     * 判断字段是否为负整数正则表达式 小于=0 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isInteger_POSITIVE(String str) {
        return Regular(str, INTEGER_POSITIVE);
    }

    /**
     * 判断字段是否为DOUBLE 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isDouble(String str) {
        return Regular(str, DOUBLE);
    }

    /**
     * 判断字段是否为正浮点数正则表达式 大于=0 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isDouble_NEGATIVE(String str) {
        return Regular(str, DOUBLE_NEGATIVE);
    }

    /**
     * 判断字段是否为负浮点数正则表达式 小于=0 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isDouble_POSITIVE(String str) {
        return Regular(str, DOUBLE_POSITIVE);
    }

    /**
     * 判断字段是否为日期 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isDate(String str) {
        return Regular(str, DATE_ALL);
    }

    /**
     * 判断字段是否为年龄 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isAge(String str) {
        return Regular(str, AGE);
    }

    /**
     * 判断字段是否为邮编 符合返回ture
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isZipCode(String str) {
        return Regular(str, ZIP_CODE);
    }

    /**
     * 判断字符串是不是全部是英文字母
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isEnglish(String str) {
        return Regular(str, STR_ENG);
    }

    /**
     * 判断字符串是不是全部是英文字母+数字
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isENG_NUM(String str) {
        return Regular(str, STR_ENG_NUM);
    }

    /**
     * 判断字符串是不是全部是英文字母+数字+下划线
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isENG_NUM_(String str) {
        return Regular(str, STR_ENG_NUM_);
    }

    /**
     * 判断字符串是不是数字组成
     *
     * @param str str
     * @return 是否通过
     */
    public static boolean isSTR_NUM(String str) {
        return Regular(str, STR_NUM);
    }

    /**
     * 匹配是否符合正则表达式pattern 匹配返回true
     *
     * @param str     匹配的字符串
     * @param pattern 匹配模式
     * @return 是否通过
     */

    private static boolean Regular(String str, String pattern) {
        if (null == str || str.trim().length() <= 0)
            return false;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isBankCard(String str) {
        return BankCard.isBankCard(str);
    }
    //---------------------------------------下面是内部类--------------------------------------------


    /**
     * 银行卡号判断类
     */
    public static class BankCard {
        /**
         * 校验银行卡卡号
         *
         * @param bankCard 银行卡卡号
         * @return 是否通过
         */
        public static boolean isBankCard(String bankCard) {
            if (bankCard.length() < 15 || bankCard.length() > 19) {
                return false;
            }
            char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
            return bit != 'N' && bankCard.charAt(bankCard.length() - 1) == bit;
        }

        /**
         * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
         *
         * @param nonCheckCodeBankCard 不含校验位的银行卡卡号
         * @return 是否通过
         */
        private static char getBankCardCheckCode(String nonCheckCodeBankCard) {
            if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                    || !nonCheckCodeBankCard.matches("\\d+")) {
                // 如果传的不是数据返回N
                return 'N';
            }
            char[] chs = nonCheckCodeBankCard.trim().toCharArray();
            int luhmSum = 0;
            for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
                int k = chs[i] - '0';
                if (j % 2 == 0) {
                    k *= 2;
                    k = k / 10 + k % 10;
                }
                luhmSum += k;
            }
            return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
        }
    }

}
