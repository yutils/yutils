package com.yujing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类说明:身份证合法性校验
 *
 * @author 余静 2018年11月30日12:12:16
 * --15位身份证号码：第7、8位为出生年份(两位数)，第9、10位为出生月份，第11、12位代表出生日期，第15位代表性别，奇数为男，偶数为女。
 * --18位身份证号码
 * ：第7、8、9、10位为出生年份(四位数)，第11、第12位为出生月份，第13、14位代表出生日期，第17位代表性别，奇数为男，偶数为女。
 */
/*用法
String idcard15 = "142431199001145";// 15位
String idcard18 = "511321199206255595";// 18位
YIdcard iv = new YIdcard();
System.out.println(idcard15+":"+iv.isIdcard(idcard15));
System.out.println(idcard18+":"+iv.isIdcard(idcard18));
IdcardInfo ie = (new YIdcard()).new IdcardInfo(idcard18);
System.out.println(ie.toString());
 */
@SuppressWarnings({"unchecked", "unused", "all"})
public class YIdcard {

    /**
     * 省，直辖市代码表： { 11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",
     * 21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",
     * 33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",
     * 42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",
     * 51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",
     * 63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}
     */
    protected String codeAndCity[][] = {{"11", "北京"}, {"12", "天津"},
            {"13", "河北"}, {"14", "山西"}, {"15", "内蒙古"}, {"21", "辽宁"},
            {"22", "吉林"}, {"23", "黑龙江"}, {"31", "上海"}, {"32", "江苏"},
            {"33", "浙江"}, {"34", "安徽"}, {"35", "福建"}, {"36", "江西"},
            {"37", "山东"}, {"41", "河南"}, {"42", "湖北"}, {"43", "湖南"},
            {"44", "广东"}, {"45", "广西"}, {"46", "海南"}, {"50", "重庆"},
            {"51", "四川"}, {"52", "贵州"}, {"53", "云南"}, {"54", "西藏"},
            {"61", "陕西"}, {"62", "甘肃"}, {"63", "青海"}, {"64", "宁夏"},
            {"65", "新疆"}, {"71", "台湾"}, {"81", "香港"}, {"82", "澳门"},
            {"91", "国外"}};

    private String cityCode[] = {"11", "12", "13", "14", "15", "21", "22",
            "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
            "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63",
            "64", "65", "71", "81", "82", "91"};

    // 每位加权因子
    private int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    // 第18位校检码
    private String verifyCode[] = {"1", "0", "X", "9", "8", "7", "6", "5",
            "4", "3", "2"};

    /**
     * 验证所有的身份证的合法性
     *
     * @param idcard 身份证号
     * @return 是否合法
     */
    public boolean isIdcard(String idcard) {
        if (idcard.length() == 15) idcard = this.convertIdcarBy15bit(idcard);
        return this.isValidate18Idcard(idcard);
    }

    /**
     * 判断18位身份证的合法性 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，
     * 由十七位数字本体码和一位数字校验码组成。 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。 顺序码:
     * 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
     * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
     * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
     * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
     * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
     * 2 1 6 3 7 9 10 5 8 4 2 2.将这17位数字和系数相乘的结果相加。 3.用加出来和除以11，看余数是多少？ 4.余数只可能有0
     * 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     *
     * @param idcard
     * @return
     */
    private boolean isValidate18Idcard(String idcard) {
        // 非18位为假
        if (idcard.length() != 18) return false;
        // 获取前17位
        String idcard17 = idcard.substring(0, 17);
        // 获取第18位
        String idcard18Code = idcard.substring(17, 18);
        char c[] = null;
        String checkCode = "";
        // 是否都为数字
        if (!isDigital(idcard17)) return false;
        c = idcard17.toCharArray();
        if (null != c) {
            int bit[] = new int[idcard17.length()];
            bit = converCharToInt(c);
            int sum17 = 0;
            sum17 = getPowerSum(bit);
            // 将和值与11取模得到余数进行校验码判断
            checkCode = getCheckCodeBySum(sum17);
            if (null == checkCode) return false;
            // 将身份证的第18位与算出来的校码进行匹配，不相等就为假
            if (!idcard18Code.equalsIgnoreCase(checkCode)) return false;
        }
        return true;
    }

    /**
     * 验证15位身份证的合法性,该方法验证不准确，最好是将15转为18位后再判断，该类中已提供。
     *
     * @param idcard
     * @return
     */
    private boolean isValidate15Idcard(String idcard) {
        // 非15位为假
        if (idcard.length() != 15) return false;
        // 是否全都为数字
        if (!isDigital(idcard)) return false;
        String provinceid = idcard.substring(0, 2);
        String birthday = idcard.substring(6, 12);
        int year = Integer.parseInt(idcard.substring(6, 8));
        int month = Integer.parseInt(idcard.substring(8, 10));
        int day = Integer.parseInt(idcard.substring(10, 12));
        // 判断是否为合法的省份
        boolean flag = false;
        for (String id : cityCode) {
            if (id.equals(provinceid)) {
                flag = true;
                break;
            }
        }
        if (!flag) return false;
        // 该身份证生出日期在当前日期之后时为假
        Date birthdate = null;
        try {
            birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (birthdate == null || new Date().before(birthdate)) return false;
        // 判断是否为合法的年份
        GregorianCalendar curDay = new GregorianCalendar();
        int curYear = curDay.get(Calendar.YEAR);
        int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));
        // 判断该年份的两位表示法，小于50的和大于当前年份的，为假
        if ((year < 50 && year > year2bit)) return false;
        // 判断是否为合法的月份
        if (month < 1 || month > 12) return false;
        // 判断是否为合法的日期
        boolean mflag = false;
        curDay.setTime(birthdate); // 将该身份证的出生日期赋于对象curDay
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                mflag = (day >= 1 && day <= 31);
                break;
            case 2: // 公历的2月非闰年有28天,闰年的2月是29天。
                if (curDay.isLeapYear(curDay.get(Calendar.YEAR))) {
                    mflag = (day >= 1 && day <= 29);
                } else {
                    mflag = (day >= 1 && day <= 28);
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                mflag = (day >= 1 && day <= 30);
                break;
        }
        if (!mflag) return false;

        return true;
    }

    /**
     * 将15位的身份证转成18位身份证
     *
     * @param idcard idcard
     * @return 18位身份证
     */
    public String convertIdcarBy15bit(String idcard) {
        String idcard17 = null;
        // 非15位身份证
        if (idcard.length() != 15) return null;
        if (!isDigital(idcard)) return null;
        // 获取出生年月日
        String birthday = idcard.substring(6, 12);
        Date birthdate = null;
        try {
            birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cday = Calendar.getInstance();
        cday.setTime(birthdate);
        String year = String.valueOf(cday.get(Calendar.YEAR));
        idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);
        char c[] = idcard17.toCharArray();
        String checkCode = "";
        if (null != c) {
            int bit[] = new int[idcard17.length()];
            // 将字符数组转为整型数组
            bit = converCharToInt(c);
            int sum17 = 0;
            sum17 = getPowerSum(bit);
            // 获取和值与11取模得到余数进行校验码
            checkCode = getCheckCodeBySum(sum17);
            // 获取不到校验位
            if (null == checkCode) return null;
            // 将前17位与第18位校验码拼接
            idcard17 += checkCode;
        }
        return idcard17;
    }

    /**
     * 数字验证
     *
     * @param str str
     * @return 是否是数字
     */
    private boolean isDigital(String str) {
        return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param bit bit
     * @return PowerSum
     */
    private int getPowerSum(int[] bit) {
        int sum = 0;
        if (power.length != bit.length) return sum;
        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) sum = sum + bit[i] * power[j];
            }
        }
        return sum;
    }

    /**
     * 将和值与11取模得到余数进行校验码判断
     *
     * @param sum17 sum17
     * @return 校验位
     */
    private String getCheckCodeBySum(int sum17) {
        return verifyCode[sum17 % 11];
    }

    /**
     * 将字符数组转为整型数组
     *
     * @param c char[]
     * @return int[]
     * @throws NumberFormatException NumberFormatException
     */

    private int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c)
            a[k++] = Integer.parseInt(String.valueOf(temp));
        return a;
    }

    /**
     * 类说明:提取身份证相关信息
     */
    public class IdcardInfo {
        // 省份
        private String province;
        // 城市
        private String city;
        // 区县
        private String region;
        // 年份
        private int year;
        // 月份
        private int month;
        // 日期
        private int day;
        // 性别
        private String gender;
        // 出生日期
        private Date birthday;
        private YIdcard validator = null;
        private Map<String, String> cityCodeMap = new HashMap<String, String>() {
            {
                this.put("11", "北京");
                this.put("12", "天津");
                this.put("13", "河北");
                this.put("14", "山西");
                this.put("15", "内蒙古");
                this.put("21", "辽宁");
                this.put("22", "吉林");
                this.put("23", "黑龙江");
                this.put("31", "上海");
                this.put("32", "江苏");
                this.put("33", "浙江");
                this.put("34", "安徽");
                this.put("35", "福建");
                this.put("36", "江西");
                this.put("37", "山东");
                this.put("41", "河南");
                this.put("42", "湖北");
                this.put("43", "湖南");
                this.put("44", "广东");
                this.put("45", "广西");
                this.put("46", "海南");
                this.put("50", "重庆");
                this.put("51", "四川");
                this.put("52", "贵州");
                this.put("53", "云南");
                this.put("54", "西藏");
                this.put("61", "陕西");
                this.put("62", "甘肃");
                this.put("63", "青海");
                this.put("64", "宁夏");
                this.put("65", "新疆");
                this.put("71", "台湾");
                this.put("81", "香港");
                this.put("82", "澳门");
                this.put("91", "国外");
            }
        };

        /**
         * 通过构造方法初始化各个成员属性
         *
         * @param idCard idCard
         */
        public IdcardInfo(String idCard) {
            try {
                validator = new YIdcard();
                if (validator.isIdcard(idCard)) {
                    if (idCard.length() == 15) {
                        idCard = validator.convertIdcarBy15bit(idCard);
                    }
                    // 获取省份
                    String provinceId = idCard.substring(0, 2);
                    Set<String> key = this.cityCodeMap.keySet();
                    for (String id : key) {
                        if (id.equals(provinceId)) {
                            this.province = this.cityCodeMap.get(id);
                            break;
                        }
                    }
                    // 获取性别
                    String id17 = idCard.substring(16, 17);
                    if (Integer.parseInt(id17) % 2 != 0) {
                        this.gender = "男";
                    } else {
                        this.gender = "女";
                    }
                    // 获取出生日期
                    String birthday = idCard.substring(6, 14);
                    Date birthdate = new SimpleDateFormat("yyyyMMdd")
                            .parse(birthday);
                    this.birthday = birthdate;
                    GregorianCalendar currentDay = new GregorianCalendar();
                    currentDay.setTime(birthdate);
                    this.year = currentDay.get(Calendar.YEAR);
                    this.month = currentDay.get(Calendar.MONTH) + 1;
                    this.day = currentDay.get(Calendar.DAY_OF_MONTH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @return the province
         */
        private String getProvince() {
            return province;
        }

        /**
         * @return the city
         */
        private String getCity() {
            return city;
        }

        /**
         * @return the region
         */
        private String getRegion() {
            return region;
        }

        /**
         * @return the year
         */
        private int getYear() {
            return year;
        }

        /**
         * @return the month
         */
        private int getMonth() {
            return month;
        }

        /**
         * @return the day
         */
        private int getDay() {
            return day;
        }

        /**
         * @return the gender
         */
        private String getGender() {
            return gender;
        }

        /**
         * @return the birthday
         */
        private Date getBirthday() {
            return birthday;
        }

        @Override
        public String toString() {
            return "省份：" + this.province + ",性别：" + this.gender + ",出生日期："
                    + this.birthday;
        }
    }
}