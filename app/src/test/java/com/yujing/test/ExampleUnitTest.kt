package com.yujing.test

import com.yujing.utils.YNumber
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)

        //循环调用abc方法每1000毫秒，abc不能为private，不能有参数
//        YLoop.start(this,"abc",1000);
        println(YNumber.fill(39.5))
        println(YNumber.fill(39.9849))
        println(YNumber.fill(39.985))
        println(YNumber.fill(39.9850))
        println(YNumber.fill(39.98500))
        println(YNumber.fill(39.98501))
        println(YNumber.fill(39.9851))


        println(YNumber.D2D(39.5))
        println(YNumber.D2D(39.9849))
        println(YNumber.D2D(39.985))
        println(YNumber.D2D(39.9850))
        println(YNumber.D2D(39.98500))
        println(YNumber.D2D(39.98501))
        println(YNumber.D2D(39.9851))
    }

    @Test
    fun test2() {

    }
}
