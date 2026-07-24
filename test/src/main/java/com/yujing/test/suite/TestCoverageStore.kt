package com.yujing.test.suite

import com.yujing.utils.YApp
import com.yujing.utils.YSave

/**
 * 用 YSave 持久化用例状态，重启后仍可看到覆盖进度
 */
object TestCoverageStore {
    private const val PATH = "YTestCoverage"
    private const val KEY_PREFIX = "case_"

    private fun save(): YSave = YSave.create(YApp.get(), PATH, ".json")

    fun loadInto(cases: List<TestCase>) {
        val s = save()
        for (c in cases) {
            val raw = s.read(KEY_PREFIX + c.id, String::class.java) ?: continue
            val parts = raw.split("|", limit = 3)
            if (parts.isEmpty()) continue
            c.status = runCatching { TestStatus.valueOf(parts[0]) }.getOrDefault(TestStatus.Untested)
            if (c.status == TestStatus.Running) c.status = TestStatus.Untested
            c.message = parts.getOrNull(1)?.takeIf { it.isNotEmpty() }
            c.durationMs = parts.getOrNull(2)?.toLongOrNull() ?: 0
        }
    }

    fun persist(case: TestCase) {
        val msg = case.message?.replace("|", " ") ?: ""
        save().write(KEY_PREFIX + case.id, "${case.status.name}|$msg|${case.durationMs}")
    }

    fun persistAll(cases: List<TestCase>) {
        cases.forEach { persist(it) }
    }

    fun clear(cases: List<TestCase>) {
        val s = save()
        cases.forEach {
            s.remove(KEY_PREFIX + it.id)
            it.status = TestStatus.Untested
            it.message = null
            it.durationMs = 0
        }
    }
}
