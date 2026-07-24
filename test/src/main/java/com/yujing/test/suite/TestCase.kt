package com.yujing.test.suite

import androidx.activity.ComponentActivity

/**
 * 测试用例基类
 */
sealed class TestCase {
    abstract val id: String
    abstract val title: String
    abstract val category: TestCategory
    abstract val description: String

    /** 运行时状态（内存），持久化由 CoverageStore 负责 */
    @Volatile
    var status: TestStatus = TestStatus.Untested

    @Volatile
    var message: String? = null

    @Volatile
    var durationMs: Long = 0
}

/**
 * 可自动断言的用例：抛异常即失败
 */
class AutoTestCase(
    override val id: String,
    override val title: String,
    override val category: TestCategory,
    override val description: String = "",
    val block: suspend () -> Unit,
) : TestCase()

/**
 * 需人工/设备交互的用例
 */
class ManualTestCase(
    override val id: String,
    override val title: String,
    override val category: TestCategory,
    override val description: String = "",
    /** 点击后执行；可自行更新 status */
    val onRun: (activity: ComponentActivity, case: ManualTestCase) -> Unit,
) : TestCase()

data class TestResult(
    val id: String,
    val status: TestStatus,
    val message: String? = null,
    val durationMs: Long = 0,
)

data class CoverageSummary(
    val total: Int,
    val passed: Int,
    val failed: Int,
    val untested: Int,
    val running: Int,
    val skipped: Int,
    val autoTotal: Int,
    val autoPassed: Int,
) {
    val tested: Int get() = passed + failed + skipped
    val progressText: String
        get() = "通过 $passed / 失败 $failed / 未测 $untested / 共 $total"
}
