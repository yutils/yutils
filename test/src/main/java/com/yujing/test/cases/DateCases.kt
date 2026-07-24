package com.yujing.test.cases

import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.utils.YDataLunar
import com.yujing.utils.YDate
import java.util.Calendar
import java.util.Date

object DateCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("date.format.parse", "YDate format/parse 往返", TestCategory.DATE) {
            val fmt = "yyyy-MM-dd HH:mm:ss"
            val now = java.util.Date()
            val s = YDate.date2String(now, fmt)
            val back = YDate.string2Date(s, fmt)
            require(back != null) { "parse 失败: $s" }
            require(kotlin.math.abs(back!!.time - now.time) < 2000) { "误差过大" }
        },
        AutoTestCase("date.week.monday", "本周周一 firstDayOfWeek", TestCategory.DATE) {
            // 构造一个明确的周日（相对今天回退到最近周日）
            val sunday = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.MONDAY
                while (get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    add(Calendar.DAY_OF_MONTH, -1)
                }
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val first = YDate.getFirstDayOfWeek(sunday)
            require(first.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                "应返回周一，实际=${first.get(Calendar.DAY_OF_WEEK)}"
            }
            // 周日时周一应不晚于当天（应是本周刚过去的周一，不是下一周）
            require(first.timeInMillis <= sunday.timeInMillis) {
                "周日获取的周一晚于当天，周界错误"
            }
            val last = YDate.getLastDayOfWeek(sunday)
            require(last.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                "周末应是周日"
            }
            require(last.timeInMillis >= sunday.timeInMillis - 1000) {
                "周末应覆盖当天"
            }
        },
        AutoTestCase("date.ymd", "YDate getStringDateShort", TestCategory.DATE) {
            val s = YDate.getStringDateShort()
            require(s.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) { "格式异常: $s" }
        },
        AutoTestCase("date.day.bounds", "YDate 当天起止", TestCategory.DATE) {
            val start = YDate.getStartTimeOfDay()
            val end = YDate.getEndTimeOfDay()
            require(start.timeInMillis < end.timeInMillis)
            require(start.get(Calendar.HOUR_OF_DAY) == 0)
            require(end.get(Calendar.HOUR_OF_DAY) == 23)
        },
        AutoTestCase("date.lunar", "YDataLunar 公历转农历", TestCategory.DATE) {
            val lunar = YDataLunar()
            lunar.setDate(2020, 6, 1)
            require(lunar.lunarYear == 2020) { "年=${lunar.lunarYear}" }
            require(lunar.lunarDayString.isNotBlank()) { "农历日空" }
            require(lunar.toLunarString().isNotBlank())
            // 2020-06-01 示例注释为儿童节；不强依赖 festival 文案
            require(lunar.weekString.isNotBlank())
        },
        AutoTestCase("date.convert", "YDate dateConvert/周几", TestCategory.DATE) {
            val out = YDate.dateConvert("2020-06-01", "yyyy-MM-dd", "yyyy/MM/dd")
            require(out == "2020/06/01") { "convert=$out" }
            val week = YDate.dateToWeek(YDate.string2Date("2020-06-01", "yyyy-MM-dd"))
            require(week.isNotBlank()) { "week=$week" }
        },
        AutoTestCase("date.month.year", "YDate 月/年起止", TestCategory.DATE) {
            val cal = Calendar.getInstance().apply { set(2024, Calendar.FEBRUARY, 15, 12, 0, 0) }
            val firstM = YDate.getFirstDayOfMonth(cal)
            val lastM = YDate.getLastDayOfMonth(cal)
            require(firstM.get(Calendar.DAY_OF_MONTH) == 1)
            require(lastM.get(Calendar.DAY_OF_MONTH) == 29) { "2024-02 应有29天" }
            val firstY = YDate.getFirstDayOfYear(cal)
            val lastY = YDate.getLastDayOfYear(cal)
            require(firstY.get(Calendar.DAY_OF_YEAR) == 1)
            require(lastY.get(Calendar.MONTH) == Calendar.DECEMBER)
        },
        AutoTestCase("date.week.bounds", "YDate 周起止与 Calendar 互转", TestCategory.DATE) {
            val start = YDate.getStartTimeOfWeek()
            val end = YDate.getEndTimeOfWeek()
            require(start.timeInMillis <= end.timeInMillis)
            val d = Date()
            val c = YDate.dateToCalendar(d)
            val back = YDate.calendarToDate(c)
            require(kotlin.math.abs(back.time - d.time) < 1000) { "Calendar 互转偏差过大" }
            val short = YDate.getTimeShort()
            require(short.matches(Regex("""\d{1,2}:\d{2}:\d{2}"""))) { "timeShort=$short" }
        },
        AutoTestCase("date.lunar.extra", "YDataLunar 节日/闰月可读", TestCategory.DATE) {
            val lunar = YDataLunar()
            lunar.setDate(2020, 6, 1)
            // 不强制节日文案，只要求 API 可调用
            lunar.getFestival()
            lunar.getLeapMonth()
            require(lunar.toString().isNotBlank())
        },
    )
}
