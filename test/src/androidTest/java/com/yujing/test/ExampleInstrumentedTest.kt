package com.yujing.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yujing.utils.YLog
import com.yujing.utils.YRunOnceOfTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.yujing.test", appContext.packageName)
        YLog.i("包名：" + appContext.packageName)
    }

    @Test
    fun test(){
//        Thread {
//            YRunOnceOfTime.run(1000," 哈哈哈"){
//                println("111")
//            }
//        }.start()
//        Thread {
//            YRunOnceOfTime.run(1000," 哈哈哈"){
//                println("222")
//            }
//        }.start()
//        Thread {
//            YRunOnceOfTime.run(1000," 哈哈哈"){
//                println("333")
//            }
//        }.start()
//        Thread.sleep(3000)
//        println("方法完毕")
    }
}
