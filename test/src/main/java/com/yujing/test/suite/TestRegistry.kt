package com.yujing.test.suite

import com.yujing.test.cases.BusCases
import com.yujing.test.cases.ConvertCases
import com.yujing.test.cases.CryptCases
import com.yujing.test.cases.DateCases
import com.yujing.test.cases.DbCases
import com.yujing.test.cases.HardwareManualCases
import com.yujing.test.cases.MediaManualCases
import com.yujing.test.cases.OtherCases
import com.yujing.test.cases.SocketCases
import com.yujing.test.cases.StorageCases
import com.yujing.test.cases.ThreadCases
import com.yujing.test.cases.UiManualCases

/**
 * 全部用例注册表
 */
object TestRegistry {
    val all: List<TestCase> by lazy {
        buildList {
            addAll(ConvertCases.all())
            addAll(DateCases.all())
            addAll(CryptCases.all())
            addAll(StorageCases.all())
            addAll(ThreadCases.all())
            addAll(BusCases.all())
            addAll(SocketCases.all())
            addAll(OtherCases.all())
            addAll(DbCases.all())
            addAll(UiManualCases.all())
            addAll(MediaManualCases.all())
            addAll(HardwareManualCases.all())
        }.also { TestCoverageStore.loadInto(it) }
    }

    fun byCategory(category: TestCategory): List<TestCase> {
        if (category == TestCategory.OVERVIEW) return all
        return all.filter { it.category == category }
    }

    fun autos(category: TestCategory? = null): List<AutoTestCase> {
        val list = if (category == null || category == TestCategory.OVERVIEW) all else byCategory(category)
        return list.filterIsInstance<AutoTestCase>()
    }

    fun find(id: String): TestCase? = all.find { it.id == id }

    fun summary(category: TestCategory? = null): CoverageSummary {
        val list = when {
            category == null || category == TestCategory.OVERVIEW -> all
            else -> byCategory(category)
        }
        val autos = list.filterIsInstance<AutoTestCase>()
        return CoverageSummary(
            total = list.size,
            passed = list.count { it.status == TestStatus.Passed },
            failed = list.count { it.status == TestStatus.Failed },
            untested = list.count { it.status == TestStatus.Untested },
            running = list.count { it.status == TestStatus.Running },
            skipped = list.count { it.status == TestStatus.Skipped },
            autoTotal = autos.size,
            autoPassed = autos.count { it.status == TestStatus.Passed },
        )
    }
}
