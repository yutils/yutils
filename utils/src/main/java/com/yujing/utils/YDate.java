package com.yujing.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 日期操作类
 *
 * @author yujing 2020年3月20日11:18:39
 * <p>
 * <p>
 * <p>
 * 新类LocalDate用法，仅支持java8+和api26+
 * LocalDate localDate = LocalDate.of(2018, 2, 6);
 * int year = localDate.getYear();
 * Month month = localDate.getMonth();
 * int day = localDate.getDayOfMonth();
 * DayOfWeek dow = localDate.getDayOfWeek();
 * int len = localDate.lengthOfMonth();
 * boolean bool = localDate.isLeapYear();
 * LocalDate today = LocalDate.now();
 * System.out.println(year);    // 2018
 * System.out.println(month);   // FEBRUARY
 * System.out.println(day);     // 6
 * System.out.println(dow);     // TUESDAY
 * System.out.println(len);     // 28
 * System.out.println(bool);    // false
 * System.out.println(today);   // 2018-02-06
 * <p>
 * <p>
 * <p>
 * LocalDate localDate = LocalDate.of(2018, 2, 6);
 * String str1 = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
 * String str2 = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
 * LocalDate date1 = LocalDate.parse("20180206", DateTimeFormatter.BASIC_ISO_DATE);
 * LocalDate date2 = LocalDate.parse("2018-02-06", DateTimeFormatter.ISO_LOCAL_DATE);
 * System.out.println(str1);   //20180206
 * System.out.println(str2);   //2018-02-06
 * System.out.println(date1);  //2018-02-06
 * System.out.println(date2);  //2018-02-06
 * <p>
 * <p>
 * <p>
 * LocalDate localDate = LocalDate.of(2018, 2, 6);
 * LocalDate localDate1 = localDate.withYear(2019);                        //年份修改为2019
 * LocalDate localDate2 = localDate1.withDayOfMonth(25);                   //日改为25
 * LocalDate localDate3 = localDate2.with(ChronoField.MONTH_OF_YEAR, 9);   //月份改为9
 * LocalDate localDate4 = localDate3.plusWeeks(1);                         //此时的日期是2019-09-25，在此基础上增加一周是2019-10-02
 * LocalDate localDate5 = localDate4.minusYears(3);                        //减去三年 2016-10-02
 * LocalDate localDate6 = localDate5.plus(6, ChronoUnit.MONTHS);           //加上六个月2017-04-02
 * //获取以2017-04-02为基准，第一个符合指定星期几要求的日期，2017-04-02就是星期日，程序会直接返回该对象
 * LocalDate localDate7 = localDate6.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
 * //获取2017-04-02所处月份的最后一天，同步取值还有lastDayOfNextMonth/firstDayOfMonth/firstDayOfNextMonth，等等
 * LocalDate localDate8 = localDate7.with(TemporalAdjusters.lastDayOfMonth());
 * System.out.println(localDate);  //2018-02-06
 * System.out.println(localDate1); //2019-02-06
 * System.out.println(localDate2); //2019-02-25
 * System.out.println(localDate3); //2019-09-25
 * System.out.println(localDate4); //2019-10-02
 * System.out.println(localDate5); //2019-10-02
 * System.out.println(localDate6); //2017-04-02
 * System.out.println(localDate7); //2017-04-02
 * System.out.println(localDate8); //2017-04-30
 */
public class YDate {
    /**
     * strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * * HH时mm分ss秒，
     * * strTime的时间格式必须要与formatType的时间格式相同
     *
     * @param strTime    需要转换的时间字符串
     * @param formatType format
     * @return Date
     */
    public static Date string2Date(String strTime, String formatType) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        try {
            return formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把date转换成指定格式字符串
     *
     * @param date       时间
     * @param formatType 格式
     * @return 格式化后的数据
     */
    public static String date2String(Date date, String formatType) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.format(date);
    }

    /**
     * 时间格式转换
     *
     * @param oldDateString 旧的时间字符串
     * @param oldDateFormat 旧的格式
     * @param newDateFormat 新的格式
     * @return 新的时间字符串
     */
    public static String dateConvert(String oldDateString, String oldDateFormat, String newDateFormat) {
        String newDateString;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatOld = new SimpleDateFormat(oldDateFormat);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatNew = new SimpleDateFormat(newDateFormat);
        try {
            Date date = dateFormatOld.parse(oldDateString);
            newDateString = dateFormatNew.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            newDateString = "";
        }
        return newDateString;
    }

    /**
     * 根据日期获取星期 （2019-05-06 ——> 星期一）
     *
     * @return 星期几
     */
    public static String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    /**
     * 获取详细时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 获取详细时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDateChinese() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 获取年月日
     *
     * @return 年月日
     */
    public static String getStringDateShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    /**
     * 获取年月日
     *
     * @return 年月日
     */
    public static String getStringDateShortChinese() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        return formatter.format(new Date());
    }

    /**
     * 获取时分秒
     *
     * @return 时分秒
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date());
    }

    /**
     * 计算本月第一天
     *
     * @return 日期
     */
    public static Date getFirstDay() {
        Calendar firstDay = Calendar.getInstance();//获取当前时间
        firstDay.set(Calendar.DAY_OF_MONTH, 1);//日期设置为一号，就是第一天了
        return firstDay.getTime();
    }

    /**
     * 计算本月最后一天
     *
     * @return 日期
     */
    public static Date getLastDay() {
        Calendar lastDay = Calendar.getInstance();//获取当前时间
        lastDay.add(Calendar.MONTH, 1);//月份设置为下个月
        lastDay.set(Calendar.DAY_OF_MONTH, 1);//日期设置为1号
        lastDay.add(Calendar.DAY_OF_MONTH, -1);//倒回到前一天
        return lastDay.getTime();
    }
}
