package com.yujing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 农历日期计算，农历节日获取，公历节日获取，星期获取，年属相获取
 *
 * @author 余静 2020年7月25日10:09:59
 * 如：20200601 转换后   2020年 闰四月 初十 星期一（鼠年)
 */
/*用法
YDataLunar lunarCalendar = new YDataLunar();
lunarCalendar.setDate(2020, 6, 1);
String s = "" + lunarCalendar.getLunarYear() + "\n";//2020
s += "" + lunarCalendar.getLunarMonthString() + "\n";//闰四月
s += "" + lunarCalendar.getLunarDayString() + "\n";//初十
s += "" + lunarCalendar.getFestival() + "节\n";//儿童节
s += "" + lunarCalendar.getWeek() + "\n";//1
s += lunarCalendar.getWeekString() + "\n";//星期一
s += lunarCalendar.toLunarString() + "\n";//2020年 闰四月 初十 星期一（鼠年)
System.out.println(s);
 */
public class YDataLunar {
    //月，日大写
    final static String chineseNumber[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    //星期
    final static String[] weekDays = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    //日期格式
    final static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    // 农历部分假日
    final static String[] lunarHoliday = new String[]{"0101 春节", "0115 元宵", "0505 端午", "0707 情人", "0715 中元", "0815 中秋", "0909 重阳", "1208 腊八", "1224 小年", "0100 除夕"};
    // 公历部分节假日
    final static String[] solarHoliday = new String[]{"0101 元旦", "0214 情人", "0308 妇女", "0312 植树", "0401 愚人", "0501 劳动", "0504 青年", "0512 护士", "0601 儿童", "0701 建党", "0801 建军", "0808 父亲", "0910 教师", "1001 国庆", "1006 老人", "1225 圣诞",};
    //农历年份天数1900起，数字&8000&800&80+348
    final static long[] lunarInfo = new long[]{
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, //
            0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, //
            0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, //
            0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, //
            0x186e3, 0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, //
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355, 0x04da0, //
            0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, //
            0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, //
            0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0, //
            0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, //
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, //
            0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, //
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, //
            0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, //
            0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, //
            0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, //
            0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    private int lunarYear; // 农历的年份
    private int lunarMonth;// 农历的月份
    private int lunarDay;// 农历的日
    private String lunarMonthString; // 农历的月份
    public int leapMonth = 0; // 闰的是哪个月
    boolean leap = false;//是否是闰月
    private int week;//星期
    private String weekString;//星期


    // ====== 传回农历 y年的总天数
    private static int yearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    // ====== 传回农历 y年闰月的天数
    private static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else
            return 0;
    }

    // ====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
    private static int leapMonth(int y) {
        int result = (int) (lunarInfo[y - 1900] & 0xf);
        return result;
    }

    // ====== 传回农历 y年m月的总天数
    private static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else
            return 30;
    }

    // ====== 传回农历 y年的生肖
    final public String animalsYear(int year) {
        final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
        return Animals[(year - 4) % 12];
    }

    // ====== 传入 月日的offset 传回干支, 0=甲子
    private static String cyclicalm(int num) {
        final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    // ====== 传入 offset 传回干支, 0=甲子
    final public String cyclical(int year) {
        int num = year - 1900 + 36;
        return (cyclicalm(num));
    }

    public static String getChinaDayString(int day) {
        String chineseTen[] = {"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30)
            return "";
        if (day == 10)
            return "初十";
        else
            return chineseTen[day / 10] + chineseNumber[n];
    }

    /**
     * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
     * dayCyl5:与1900年1月31日相差的天数,再加40 ?
     * <p>
     * isday: 这个参数为false---日期为节假日时，阴历日期就返回节假日 ，true---不管日期是否为节假日依然返回这天对应的阴历日期
     *
     * @return
     */
    private String getLunarDate(int year_log, int month_log, int day_log, boolean isday) {
        // @SuppressWarnings("unused")
        int yearCyl, monCyl, dayCyl;
        // int leapMonth = 0;
        String nowadays;
        Date baseDate = null;
        Date nowaday = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
        } catch (ParseException e) {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
        }

        nowadays = year_log + "年" + month_log + "月" + day_log + "日";
        try {
            nowaday = chineseDateFormat.parse(nowadays);
        } catch (ParseException e) {
            e.printStackTrace(); // To change body of catch statement use
            // Options | File Templates.
        }
        weekString = dateToWeek(nowaday);

        // 求出和1900年1月31日相差的天数
        int offset = (int) ((nowaday.getTime() - baseDate.getTime()) / 86400000L);
        dayCyl = offset + 40;
        monCyl = 14;

        // 用offset减去每农历年的天数
        // 计算当天是农历第几天
        // i最终结果是农历的年份
        // offset是当年的第几天
        int iYear, daysOfYear = 0;
        for (iYear = 1900; iYear < 10000 && offset > 0; iYear++) {
            daysOfYear = yearDays(iYear);
            offset -= daysOfYear;
            monCyl += 12;
        }
        if (offset < 0) {
            offset += daysOfYear;
            iYear--;
            monCyl -= 12;
        }
        // 农历年份
        lunarYear = iYear;
        setLunarYear(lunarYear); // 设置公历对应的农历年份

        yearCyl = iYear - 1864;
        leapMonth = leapMonth(iYear); // 闰哪个月,1-12
        leap = false;

        // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
        int iMonth, daysOfMonth = 0;
        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
            // 闰月
            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                --iMonth;
                leap = true;
                daysOfMonth = leapDays(lunarYear);
            } else
                daysOfMonth = monthDays(lunarYear, iMonth);

            offset -= daysOfMonth;
            // 解除闰月
            if (leap && iMonth == (leapMonth + 1))
                leap = false;
            if (!leap)
                monCyl++;
        }
        // offset为0时，并且刚才计算的月份是闰月，要校正
        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
            if (leap) {
                leap = false;
            } else {
                leap = true;
                --iMonth;
                --monCyl;
            }
        }
        // offset小于0时，也要校正
        if (offset < 0) {
            offset += daysOfMonth;
            --iMonth;
            --monCyl;
        }
        lunarMonth = iMonth;
        setLunarMonthString((leap ? "闰" : "") + chineseNumber[lunarMonth - 1] + "月"); // 设置对应的阴历月份
        lunarDay = offset + 1;

        if (!isday) {
            // 如果日期为节假日则阴历日期则返回节假日
            // setLeapMonth(leapMonth);
            for (int i = 0; i < solarHoliday.length; i++) {
                // 返回公历节假日名称
                String sd = solarHoliday[i].split(" ")[0]; // 节假日的日期
                String sdv = solarHoliday[i].split(" ")[1]; // 节假日的名称
                String smonth_v = month_log + "";
                String sday_v = day_log + "";
                String smd;
                if (month_log < 10) {
                    smonth_v = "0" + month_log;
                }
                if (day_log < 10) {
                    sday_v = "0" + day_log;
                }
                smd = smonth_v + sday_v;
                if (sd.trim().equals(smd.trim())) {
                    return sdv;
                }
            }

            for (int i = 0; i < lunarHoliday.length; i++) {
                // 返回农历节假日名称
                String ld = lunarHoliday[i].split(" ")[0]; // 节假日的日期
                String ldv = lunarHoliday[i].split(" ")[1]; // 节假日的名称
                String lmonth_v = lunarMonth + "";
                String lday_v = lunarDay + "";
                String lmd = "";
                if (lunarMonth < 10) {
                    lmonth_v = "0" + lunarMonth;
                }
                if (lunarDay < 10) {
                    lday_v = "0" + lunarDay;
                }
                lmd = lmonth_v + lday_v;
                if ("12".equals(lmonth_v)) { // 除夕夜需要特殊处理
                    if ((daysOfMonth == 29 && lunarDay == 29) || (daysOfMonth == 30 && lunarDay == 30)) {
                        return ldv;
                    }
                }
                if (ld.trim().equals(lmd.trim())) {
                    return ldv;
                }
            }
        }
        if (lunarDay == 1)
            return chineseNumber[lunarMonth - 1] + "月";
        else
            return getChinaDayString(lunarDay);

    }

    private String dateToWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return dateToWeek(cal);
    }

    private String dateToWeek(Calendar calendar) {
        int w = calendar.get(7) - 1;
        if (w < 0) {
            w = 0;
        }
        week = w;
        if (week == 0) week = 7;
        return weekDays[w];
    }


    /*
        返回农历年月日
     */
    public String toLunarString() {
        return "" + getLunarYear() + "年 " + getLunarMonthString() + " " + getLunarDayString() + " " + getWeekString() + "（" + animalsYear(getLunarYear()) + "年)";
    }

    public String toString() {
        if (chineseNumber[lunarMonth - 1] == "一" && getChinaDayString(lunarDay) == "初一")
            return "农历" + lunarYear + "年";
        else if (getChinaDayString(lunarDay) == "初一")
            return chineseNumber[lunarMonth - 1] + "月";
        else
            return getChinaDayString(lunarDay);
    }

    //获取闰月月份
    public int getLeapMonth() {
        return leapMonth;
    }

    public void setLeapMonth(int leapMonth) {
        this.leapMonth = leapMonth;
    }


    public int getLunarYear() {
        return lunarYear;
    }

    public void setLunarYear(int lunarYear) {
        this.lunarYear = lunarYear;
    }


    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public String getLunarMonthString() {
        return lunarMonthString;
    }

    public void setLunarMonthString(String lunarMonthString) {
        this.lunarMonthString = lunarMonthString;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public String getLunarDayString() {
        return getChinaDayString(lunarDay);
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public int getWeek() {
        return week;
    }

    public String getWeekString() {
        return weekString;
    }

    /**
     * 设置阳历日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public void setDate(int year, int month, int day) {
        //只返回农历日期
        String date = getLunarDate(year, month, day, true);
        //false如果是节日就返回节日，否则返回农历日期
        festival = getLunarDate(year, month, day, false);
        if (festival.equals(date)) {
            festival = null;
        }
    }

    String festival = null;

    public String getFestival() {
        return festival;
    }
}