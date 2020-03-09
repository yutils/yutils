package com.yujing.test

import android.app.Application

class App : Application() {
    //标准单列
//    companion object {
//        val instance: App by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {App()}
//    }
    //单列
    companion object {
        private var instance: App? = null
            get() {
                if (field == null) field = App()
                return field
            }

        @Synchronized
        fun get(): App {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}