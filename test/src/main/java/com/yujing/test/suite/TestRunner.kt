package com.yujing.test.suite

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 串行执行 Auto 用例
 */
object TestRunner {

    suspend fun runAuto(
        cases: List<TestCase>,
        onEachStart: (TestCase) -> Unit = {},
        onEachDone: (TestCase) -> Unit = {},
        onLog: (String) -> Unit = {},
    ): List<TestResult> {
        val autos = cases.filterIsInstance<AutoTestCase>()
        val results = mutableListOf<TestResult>()
        for (c in autos) {
            c.status = TestStatus.Running
            c.message = null
            onEachStart(c)
            onLog("▶ ${c.title}")
            val start = System.currentTimeMillis()
            val result = try {
                withContext(Dispatchers.Default) { c.block() }
                c.status = TestStatus.Passed
                c.message = null
                onLog("✓ ${c.title}")
                TestResult(c.id, TestStatus.Passed, null, System.currentTimeMillis() - start)
            } catch (t: Throwable) {
                val msg = t.message ?: t.javaClass.simpleName
                c.status = TestStatus.Failed
                c.message = msg
                onLog("✗ ${c.title}: $msg")
                TestResult(c.id, TestStatus.Failed, msg, System.currentTimeMillis() - start)
            }
            c.durationMs = result.durationMs
            TestCoverageStore.persist(c)
            results.add(result)
            onEachDone(c)
        }
        return results
    }

    suspend fun runOneAuto(
        case: AutoTestCase,
        onLog: (String) -> Unit = {},
    ): TestResult {
        return runAuto(listOf(case), onLog = onLog).first()
    }
}
