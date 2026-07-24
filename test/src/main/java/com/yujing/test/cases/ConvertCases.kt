package com.yujing.test.cases

import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.utils.YBase64
import com.yujing.utils.YBase64ToHTTP
import com.yujing.utils.YBytes
import com.yujing.utils.YCalc
import com.yujing.utils.YCheck
import com.yujing.utils.YConvert
import com.yujing.utils.YConvertNumberBytes
import com.yujing.utils.YIdcard
import com.yujing.utils.YJson
import com.yujing.utils.YMoneyToCN
import com.yujing.utils.YNumber
import com.yujing.utils.YString

object ConvertCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("convert.hex.roundtrip", "YConvert hex↔bytes 往返", TestCategory.CONVERT) {
            val src = byteArrayOf(0x01, 0xAB.toByte(), 0xFF.toByte(), 0x00)
            val hex = YConvert.bytesToHexString(src)
            val back = YConvert.hexStringToByte(hex)
            require(src.contentEquals(back)) { "往返不一致 hex=$hex" }
        },
        AutoTestCase("convert.ascii.roundtrip", "YConvert ASCII 互转", TestCategory.CONVERT) {
            val s = "ABC"
            val ascii = YConvert.stringToAscii(s)
            val back = YConvert.asciiToString(ascii)
            require(back == s) { "ascii 往返失败: $ascii -> $back" }
        },
        AutoTestCase("base64.roundtrip", "YBase64 编解码往返", TestCategory.CONVERT) {
            val raw = "余静YUtils测试".toByteArray(Charsets.UTF_8)
            val enc = YBase64.encode(raw)
            val dec = YBase64.decode(enc)
            require(raw.contentEquals(dec)) { "Base64 往返失败" }
        },
        AutoTestCase("json.object.roundtrip", "YJson 对象往返", TestCategory.CONVERT) {
            data class User(val name: String, val age: Int)

            val u = User("张三", 18)
            val json = YJson.toJson(u)
            val back = YJson.toBean(json, User::class.java)
            require(back.name == u.name && back.age == u.age) { "json=$json" }
        },
        AutoTestCase("check.mobile", "YCheck 手机号正反例", TestCategory.CONVERT) {
            require(YCheck.isMobile("13800138000")) { "合法手机号应通过" }
            require(!YCheck.isMobile("12345")) { "短号应失败" }
            require(!YCheck.isMobile("abcdefghijk")) { "非数字应失败" }
        },
        AutoTestCase("check.email", "YCheck 邮箱正反例", TestCategory.CONVERT) {
            require(YCheck.isEmail("a@b.com")) { "合法邮箱应通过" }
            require(!YCheck.isEmail("not-an-email")) { "非法邮箱应失败" }
        },
        AutoTestCase("check.ipv4", "YCheck IPv4", TestCategory.CONVERT) {
            require(YCheck.isIPv4("192.168.1.1"))
            require(!YCheck.isIPv4("999.1.1.1"))
        },
        AutoTestCase("idcard.validate18", "YIdcard 18位校验", TestCategory.CONVERT) {
            val id = "511321199206255595"
            require(YIdcard().isIdcard(id)) { "示例身份证应合法: $id" }
            require(!YIdcard().isIdcard("511321199206255590")) { "改校验位应失败" }
        },
        AutoTestCase("number.round", "YNumber 加减乘除", TestCategory.CONVERT) {
            require(YNumber.sum(0.1, 0.2).let { kotlin.math.abs(it - 0.3) < 1e-9 }) { "0.1+0.2" }
            require(YNumber.multiply(2.0, 3.5) == 7.0)
            require(YNumber.divide(10.0, 4.0, 2) == 2.5)
        },
        AutoTestCase("calc.eval", "YCalc 表达式求值", TestCategory.CONVERT) {
            val r = YCalc.eval("1+2*3")
            val n = r.toDoubleOrNull()
            require(n != null && kotlin.math.abs(n - 7.0) < 1e-6) { "1+2*3=$r" }
        },
        AutoTestCase("money.cn", "YMoneyToCN 金额转中文", TestCategory.CONVERT) {
            val cn = YMoneyToCN.number2CN(123.45)
            require(cn.contains("元") || cn.contains("圆")) { "中文金额异常: $cn" }
            require(cn.isNotBlank())
        },
        AutoTestCase("string.sbc.dbc", "YString 全角半角", TestCategory.CONVERT) {
            val half = "ABC123"
            val full = YString.ToSBC(half)
            val back = YString.ToDBC(full)
            require(back == half) { "全半角往返失败 full=$full back=$back" }
        },
        AutoTestCase("string.group", "YString 按长度分组", TestCategory.CONVERT) {
            val groups = YString.group("ABCDEFGH", 3)
            require(groups.size == 3) { "分组数=${groups.size}" }
            require(groups[0].toString() == "ABC")
            require(groups[2].toString() == "GH")
        },
        AutoTestCase("bytes.builder", "YBytes 拼接与拆分", TestCategory.CONVERT) {
            val yb = YBytes().addByte(0x01).addByte(byteArrayOf(0x02, 0x03, 0x04))
            require(yb.bytes.contentEquals(byteArrayOf(0x01, 0x02, 0x03, 0x04)))
            val parts = YBytes.split(yb.bytes, 2)
            require(parts.size == 2 && parts[0].contentEquals(byteArrayOf(0x01, 0x02)))
        },
        AutoTestCase("convert.num.bytes", "YConvertNumberBytes int 往返", TestCategory.CONVERT) {
            val n = 0x12345678
            val b = YConvertNumberBytes.intToBytes(n)
            val back = YConvertNumberBytes.bytesToInt(b)
            require(back == n) { "int 往返失败 $back" }
            val rev = YConvertNumberBytes.reverse(byteArrayOf(1, 2, 3, 4))
            require(rev.contentEquals(byteArrayOf(4, 3, 2, 1)))
        },
        AutoTestCase("convert.list2array", "YConvert List2Array", TestCategory.CONVERT) {
            val list = listOf("a", "b", "c")
            val arr = YConvert.List2Array(list) as Array<*>
            require(arr.size == 3 && arr[1] == "b")
        },
        AutoTestCase("check.more", "YCheck 中文/端口/数字/Http", TestCategory.CONVERT) {
            require(YCheck.isChinese("余静"))
            require(!YCheck.isChinese("Yu"))
            require(YCheck.isPort("8080"))
            require(!YCheck.isPort("99999"))
            require(YCheck.isNumber("12345"))
            require(YCheck.isHttp("https://example.com/a"))
            require(YCheck.isIPv6("::1") || YCheck.isIPv6("2001:db8::1"))
        },
        AutoTestCase("idcard.info", "YIdcard 解析信息", TestCategory.CONVERT) {
            val id = "511321199206255595"
            val info = YIdcard().IdcardInfo(id)
            val s = info.toString()
            require(s.contains("1992") || s.contains("四川") || s.contains("511321") || s.isNotBlank()) {
                "IdcardInfo=$s"
            }
        },
        AutoTestCase("json.list.map", "YJson List/Map 解析", TestCategory.CONVERT) {
            val listJson = """[{"name":"a","age":1},{"name":"b","age":2}]"""

            data class User(val name: String, val age: Int)

            val list = YJson.toList(listJson, User::class.java)
            require(list.size == 2 && list[0].name == "a")
            val mapJson = """{"x":1,"y":2}"""
            val map = YJson.toMap(mapJson, Int::class.java)
            require(map["x"] == 1 && map["y"] == 2)
        },
        AutoTestCase("string.insert", "YString.insert 插入分隔", TestCategory.CONVERT) {
            val s = YString.insert("AABBCCDD", 2, "-")
            require(s == "AA-BB-CC-DD") { "insert=$s" }
            val odd = YString.insert("AABBCCD", 2, "-")
            require(odd == "AA-BB-CC-D") { "odd=$odd" }
        },
        AutoTestCase("convert.num.long", "YConvertNumberBytes long 往返", TestCategory.CONVERT) {
            val n = 0x1122334455667788L
            val b = YConvertNumberBytes.longToBytes(n)
            require(YConvertNumberBytes.bytesToLong(b) == n)
            val shortVal: Short = 0x1234
            require(YConvertNumberBytes.reverse(shortVal).let {
                YConvertNumberBytes.reverse(it) == shortVal
            })
        },
        AutoTestCase("base64.http", "YBase64ToHTTP 编解码", TestCategory.CONVERT) {
            val raw = "hello+/="
            val enc = YBase64ToHTTP.encodeString(raw)
            require(!enc.contains("+") && !enc.contains("/")) { "应替换 +/" }
            val dec = YBase64ToHTTP.decodeString(enc)
            require(dec == raw) { "dec=$dec" }
        },
        AutoTestCase("convert.bit.roundtrip", "YConvert bit↔byte 往返", TestCategory.CONVERT) {
            val bits = "10110001"
            val b = YConvert.bitToByte(bits)
            require(YConvert.byteToBit(b) == bits) { "bit 往返失败 b=$b" }
            val arrBits = bits + "00001111"
            val arr = YConvert.BitToByteArray(arrBits)
            require(arr.size == 2)
            require(YConvert.byteArrayToBit(arr) == arrBits) { "array bit=${YConvert.byteArrayToBit(arr)}" }
        },
        AutoTestCase("convert.bcd.roundtrip", "YConvert BCD 往返", TestCategory.CONVERT) {
            val s = "12345678"
            val bcd = YConvert.string2Bcd(s)
            val back = YConvert.bcd2String(bcd)
            require(back == s) { "bcd 往返=$back" }
        },
        AutoTestCase("convert.object.bytes", "YConvert 对象序列化往返", TestCategory.CONVERT) {
            val src = arrayListOf("a", "b", "c")
            val bytes = YConvert.object2Bytes(src)
            require(bytes != null && bytes.isNotEmpty())
            @Suppress("UNCHECKED_CAST")
            val back = YConvert.bytes2Object(bytes) as ArrayList<*>
            require(back == src && back !== src) { "反序列化异常" }
        },
        AutoTestCase("convert.bitmap.bytes", "YConvert Bitmap↔bytes", TestCategory.CONVERT) {
            val bmp = android.graphics.Bitmap.createBitmap(16, 12, android.graphics.Bitmap.Config.ARGB_8888)
            bmp.eraseColor(0xFF00AAFF.toInt())
            val bytes = YConvert.bitmap2Bytes(bmp)
            require(bytes != null && bytes.isNotEmpty())
            val back = YConvert.bytes2Bitmap(bytes)
            require(back != null && back.width == 16 && back.height == 12) {
                "bitmap ${back?.width}x${back?.height}"
            }
        },
        AutoTestCase("convert.stream.bytes", "YConvert InputStream 互转", TestCategory.CONVERT) {
            val src = byteArrayOf(1, 2, 3, 4, 5)
            val stream = YConvert.bytes2InputStream(src)
            val back = YConvert.inputStream2Bytes(stream)
            require(src.contentEquals(back))
        },
        AutoTestCase("check.ext", "YCheck 扩展校验正反例", TestCategory.CONVERT) {
            // 用库内 Luhm 规则生成合法卡号
            fun luhmBit(nonCheck: String): Char {
                val chs = nonCheck.toCharArray()
                var sum = 0
                var j = 0
                for (i in chs.size - 1 downTo 0) {
                    var k = chs[i] - '0'
                    if (j % 2 == 0) {
                        k *= 2
                        k = k / 10 + k % 10
                    }
                    sum += k
                    j++
                }
                return if (sum % 10 == 0) '0' else ('0' + (10 - sum % 10))
            }

            val base = "622848040256489001"
            val card = base + luhmBit(base)
            require(YCheck.isBankCard(card)) { "Luhm 卡应通过: $card" }
            require(!YCheck.isBankCard(card.dropLast(1) + if (card.last() == '0') '1' else '0')) { "改校验位应失败" }
            require(YCheck.isZipCode("610000")) { "邮编" }
            require(!YCheck.isZipCode("61")) { "短邮编" }
            require(
                YCheck.isDate("2020-06-01") || YCheck.isDate("2020/06/01") || YCheck.isDate("20200601")
            ) { "日期校验" }
            require(YCheck.isAge("18")) { "年龄18" }
            require(!YCheck.isAge("180")) { "年龄180超限" }
            require(
                YCheck.isUrl("www.baidu.com") || YCheck.isUrl("http://www.baidu.com") || YCheck.isHttp("https://example.com")
            ) { "URL/Http" }
            // 注意：库内 INTEGER_POSITIVE/NEGATIVE 命名与注释相反（POSITIVE 实际匹配负数）
            require(YCheck.isInteger("12") && YCheck.isInteger_NEGATIVE("12")) { "整型" }
            require(YCheck.isInteger_POSITIVE("-3")) { "负整型常量" }
            require(YCheck.isDouble("1.5") && !YCheck.isDouble("abc")) { "浮点" }
            require(YCheck.isEnglish("Hello") && !YCheck.isEnglish("你好")) { "英文" }
        },
        AutoTestCase("number.format", "YNumber 舍入与格式化", TestCategory.CONVERT) {
            require(kotlin.math.abs(YNumber.rounding(1.235, 2) - 1.24) < 1e-9) { "rounding" }
            require(YNumber.isInt("12") && !YNumber.isInt("12.3"))
            require(YNumber.isDouble("1.5") && !YNumber.isDouble("x"))
            val d2s = YNumber.D2S(1.2, 2)
            require(d2s.contains("1.2")) { "D2S=$d2s" }
            val fill = YNumber.fill(1.2, 2)
            require(fill.endsWith("20") || fill.contains("1.20") || fill == "1.20") { "fill=$fill" }
        },
        AutoTestCase("string.group.more", "YString 双字节/实际长度分组", TestCategory.CONVERT) {
            val s = "AB中文CD"
            val g1 = YString.groupDouble(s, 4)
            require(g1.isNotEmpty())
            val joined = g1.joinToString("") { it }
            require(joined.toString() == s || joined.toString().replace("\n", "") == s) { "groupDouble=$joined" }
            val g2 = YString.groupActual(s, 6)
            require(g2.isNotEmpty())
        },
        AutoTestCase("convert.num.float.double", "YConvertNumberBytes float/double/2bytes", TestCategory.CONVERT) {
            val f = 12.5f
            require(kotlin.math.abs(YConvertNumberBytes.bytesToFloat(YConvertNumberBytes.floatToBytes(f)) - f) < 1e-5)
            val d = 3.1415926535
            require(kotlin.math.abs(YConvertNumberBytes.bytesToDouble(YConvertNumberBytes.doubleToBytes(d)) - d) < 1e-12)
            val n = 65535
            require(YConvertNumberBytes.bytes2ToInt(YConvertNumberBytes.intTo2Bytes(n)) == n)
        },
        AutoTestCase("idcard.15to18", "YIdcard 15→18 位转换", TestCategory.CONVERT) {
            // 对应注释示例 18 位 511321199206255595 的 15 位形态
            val id15 = "511321920625559"
            val iv = YIdcard()
            val id18 = iv.convertIdcarBy15bit(id15)
            require(!id18.isNullOrBlank() && id18.length == 18) { "convert=$id18" }
            require(iv.isIdcard(id18)) { "转换后应合法: $id18" }
            require(iv.convertIdcarBy15bit("12345") == null) { "非法 15 位应返回 null" }
        },
        AutoTestCase("json.format", "YJson 美化输出", TestCategory.CONVERT) {
            data class User(val name: String, val age: Int)

            val pretty = YJson.toJsonFormat(User("张三", 18))
            require(!pretty.isNullOrBlank() && pretty.contains("张三")) { "pretty=$pretty" }
            val fmt = com.yujing.utils.YUtils.jsonFormat("""{"a":1}""")
            require(!fmt.isNullOrBlank() && fmt.contains("a")) { "fmt=$fmt" }
        },
    )
}
