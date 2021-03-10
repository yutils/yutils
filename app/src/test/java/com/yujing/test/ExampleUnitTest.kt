package com.yujing.test

import com.yujing.utils.YConvert
import com.yujing.utils.YConvertNumberBytes
import com.yujing.utils.YNumber
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test2() {
        //大端
        println(YConvert.BTHS(YConvertNumberBytes.ITB(123456789)))
        println(YConvertNumberBytes.BTI(YConvert.HSTB("075BCD15")))
        //小端
        println(YConvert.BTHS(YConvertNumberBytes.ITBM(123456789)))
        println(YConvertNumberBytes.BTIM(YConvert.HSTB("15CD5B07")))

        //大端
        println(YConvert.BTHS(YConvertNumberBytes.LTB(123456789987654321)))
        println(YConvertNumberBytes.BTL(YConvert.HSTB("01B69B4BE052FAB1")))
        //小端
        println(YConvert.BTHS(YConvertNumberBytes.LTBM(123456789987654321)))
        println(YConvertNumberBytes.BTLM(YConvert.HSTB("B1FA52E04B9BB601")))

    }
}
