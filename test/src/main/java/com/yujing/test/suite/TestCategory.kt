package com.yujing.test.suite

/**
 * 测试分类（对应 Tab）
 */
enum class TestCategory(val title: String, val order: Int) {
    OVERVIEW("总览", 0),
    CONVERT("转换校验", 1),
    DATE("日期", 2),
    CRYPT("加解密", 3),
    STORAGE("存储", 4),
    THREAD("线程异步", 5),
    BUS("总线", 6),
    NETWORK("网络", 7),
    UI("UI交互", 8),
    MEDIA("多媒体", 9),
    HARDWARE("硬件", 10),
    OTHER("其它", 11);

    companion object {
        fun tabs(): List<TestCategory> = entries.sortedBy { it.order }
    }
}
