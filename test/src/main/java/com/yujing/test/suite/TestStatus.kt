package com.yujing.test.suite

/**
 * 用例执行状态
 */
enum class TestStatus {
    Untested,
    Running,
    Passed,
    Failed,
    Skipped;

    val displayName: String
        get() = when (this) {
            Untested -> "未测"
            Running -> "运行中"
            Passed -> "通过"
            Failed -> "失败"
            Skipped -> "跳过"
        }
}
