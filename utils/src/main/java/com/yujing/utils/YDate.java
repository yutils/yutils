package com.yujing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 日期操作类
 *
 * @author 余静 2020年3月20日11:18:39
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
@SuppressWarnings({"unused"})
public class YDate {
    /**
     * strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss //yyyy年MM月dd日
     * * HH时mm分ss秒，
     * * strTime的时间格式必须要与formatType的时间格式相同
     *
     * @param strTime    需要转换的时间字符串
     * @param formatType format
     * @return Date
     */
    public static Date string2Date(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType, Locale.getDefault());
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
        SimpleDateFormat formatter = new SimpleDateFormat(formatType, Locale.getDefault());
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
        String newDateString = "";
        SimpleDateFormat dateFormatOld = new SimpleDateFormat(oldDateFormat, Locale.getDefault());
        SimpleDateFormat dateFormatNew = new SimpleDateFormat(newDateFormat, Locale.getDefault());
        try {
            Date date = dateFormatOld.parse(oldDateString);
            newDateString = dateFormatNew.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDateString;
    }

    /**
     * 根据日期获取星期 （2019-05-06 ——> 星期一）
     *
     * @param date date
     * @return 星期几
     */
    public static String dateToWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateToWeek(cal);
    }

    /**
     * 根据日期获取星期 （2019-05-06 ——> 星期一）
     *
     * @param calendar calendar
     * @return 星期几
     */
    public static String dateToWeek(Calendar calendar) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        //一周的第几天
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return weekDays[w];
    }

    /**
     * 获取详细时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        return getStringDate(new Date());
    }

    /**
     * 获取详细时间
     *
     * @param date date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * 获取详细时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDateChinese() {
        return getStringDateChinese(new Date());
    }

    /**
     * 获取详细时间
     *
     * @param date date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDateChinese(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * 获取年月日
     *
     * @return 年月日
     */
    public static String getStringDateShort() {
        return getStringDateShort(new Date());
    }

    /**
     * 获取年月日
     *
     * @param date date
     * @return 年月日
     */
    public static String getStringDateShort(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * 获取年月日
     *
     * @return 年月日
     */
    public static String getStringDateShortChinese() {
        return getStringDateShortChinese(new Date());
    }

    /**
     * 获取年月日
     *
     * @param date date
     * @return 年月日
     */
    public static String getStringDateShortChinese(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * 获取时分秒
     *
     * @return 时分秒
     */
    public static String getTimeShort() {
        return getTimeShort(new Date());
    }

    /**
     * 获取时分秒
     *
     * @param date date
     * @return 时分秒
     */
    public static String getTimeShort(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(date);
    }


    /**
     * 获取当年第一天
     */
    public static Calendar getFirstDayOfYear() {
        return getFirstDayOfYear(Calendar.getInstance());
    }

    /**
     * 获取一年第一天
     */
    public static Calendar getFirstDayOfYear(Calendar c) {
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取当年最后一天
     */
    public static Calendar getLastDayOfYear() {
        return getLastDayOfYear(Calendar.getInstance());
    }

    /**
     * 获取一年最后一天
     */
    public static Calendar getLastDayOfYear(Calendar c) {
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.add(Calendar.DAY_OF_YEAR, -1);
        c.add(Calendar.YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }

    /**
     * 获取当月第一天
     */
    public static Calendar getFirstDayOfMonth() {
        return getFirstDayOfMonth(Calendar.getInstance());
    }

    /**
     * 获取一个月第一天
     */
    public static Calendar getFirstDayOfMonth(Calendar c) {
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取当月最后一天
     */
    public static Calendar getLastDayOfMonth() {
        return getLastDayOfMonth(Calendar.getInstance());
    }

    /**
     * 获取一个月最后一天
     */
    public static Calendar getLastDayOfMonth(Calendar c) {
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_YEAR, -1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }

    /**
     * 获取当周第一天
     */
    public static Calendar getFirstDayOfWeek() {
        return getFirstDayOfWeek(Calendar.getInstance());
    }

    /**
     * 获取一周第一天
     */
    public static Calendar getFirstDayOfWeek(Calendar c) {
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//星期一，中国人星期一是开始
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    /**
     * 获取当周最后一天
     */
    public static Calendar getLastDayOfWeek() {
        return getLastDayOfWeek(Calendar.getInstance());
    }

    /**
     * 获取一周最后一天
     */
    public static Calendar getLastDayOfWeek(Calendar c) {
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DAY_OF_YEAR,6);//加6天，中国人星期日是结束
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }

    /**
     * 获取一年第一天
     */
    public static Date getFirstDayOfYear(Date date) {
        return getFirstDayOfYear(dateToCalendar(date)).getTime();
    }

    /**
     * 获取一年最后一天
     */
    public static Date getLastDayOfYear(Date date) {
        return getLastDayOfYear(dateToCalendar(date)).getTime();
    }

    /**
     * 获取一个月第一天
     */
    public static Date getFirstDayOfMonth(Date date) {
        return getFirstDayOfMonth(dateToCalendar(date)).getTime();
    }

    /**
     * 获取一个月最后一天
     */
    public static Date getLastDayOfMonth(Date date) {
        return getLastDayOfMonth(dateToCalendar(date)).getTime();
    }

    /**
     * 获取一周第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        return getFirstDayOfWeek(dateToCalendar(date)).getTime();
    }

    /**
     * 获取一周最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        return getLastDayOfWeek(dateToCalendar(date)).getTime();
    }


    /**
     * 获取当天的开始时间
     */
    /*
     System.out.println("获取当天的开始时间");
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     System.out.println(sdf.format(getStartTimeOfDay().getTime()));
     */
    public static Calendar getStartTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取当天的结束时间
     */
    public static Calendar getEndTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999); // 设置为999毫秒，尽可能接近最后一秒
        return calendar;
    }

    /**
     * 获取当周的开始时间
     */
    public static Calendar getStartTimeOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//星期一，中国人星期一是开始
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取当周的结束时间
     */
    public static Calendar getEndTimeOfWeek() {
        Calendar calendar = Calendar.getInstance();
        // 设置为本周的最后一天
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.DAY_OF_YEAR,6);//加6天，中国人星期日是结束
        // 将时间设置为23点59分59秒，即本周结束的时刻
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    /**
     * 获取当月的开始时间
     */
    public static Calendar getStartTimeOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取当月的结束时间
     */
    public static Calendar getEndTimeOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 将日期设置为下月的第一天
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 将时间设置为0点0分0秒，即下月第一天的开始时刻
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 回退一毫秒，得到本月的最后一毫秒
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar;
    }

    /**
     * 获取当年的开始时间
     */
    public static Calendar getStartTimeOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 获取当年的结束时间
     */
    public static Calendar getEndTimeOfYear() {
        Calendar calendar = Calendar.getInstance();
        // 将日期设置为下一年的第一天
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        // 将时间设置为0点0分0秒，即下一年第一天的开始时刻
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 回退一毫秒，得到本年的最后一毫秒
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar;
    }

    /**
     * Calendar转化为Date
     *
     * @param calendar calendar
     * @return date
     */
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

    /**
     * Date转化为Calendar
     *
     * @param date date
     * @return Calendar
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
