package com.yujing.test.ui

import android.content.Intent
import android.view.MenuItem
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.yujing.base.YBaseActivity
import com.yujing.test.R
import com.yujing.test.activity.MainActivity
import com.yujing.test.cases.MediaManualCases
import com.yujing.test.databinding.ActivityTestHomeBinding
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.CoverageGaps
import com.yujing.test.suite.ManualTestCase
import com.yujing.test.suite.TestCase
import com.yujing.test.suite.TestCategory
import com.yujing.test.suite.TestCoverageStore
import com.yujing.test.suite.TestRegistry
import com.yujing.test.suite.TestRunner
import com.yujing.test.suite.TestStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * YUtils 分类测试台
 */
class TestHomeActivity : YBaseActivity<ActivityTestHomeBinding>(null) {
    private val categories = TestCategory.tabs()
    private var runJob: Job? = null
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun initBefore() {
        binding = ActivityTestHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun init() {
        MediaManualCases.ensureSounds()
        TestRegistry.all

        binding.toolbar.title = "YUtils 测试台"
        binding.toolbar.inflateMenu(R.menu.menu_test_home)
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_coverage_help -> {
                    AlertDialog.Builder(this)
                        .setTitle("覆盖说明")
                        .setMessage(CoverageGaps.overviewHint(TestRegistry.all.size, TestRegistry.autos().size))
                        .setPositiveButton("知道了", null)
                        .show()
                    true
                }

                R.id.action_old_version -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                else -> false
            }
        }
        setupPager()
        binding.btnRunAllAuto.setOnClickListener { runAutos(null) }
        binding.btnRunCategoryAuto.setOnClickListener {
            val cat = categories.getOrNull(binding.viewPager.currentItem)
            runAutos(cat)
        }
        binding.btnClear.setOnClickListener {
            TestCoverageStore.clear(TestRegistry.all)
            binding.tvLog.text = "就绪\n"
            refreshAll()
            appendLog("已清空全部用例状态与运行日志")
        }
        refreshSummary()
        appendLog("共 ${TestRegistry.all.size} 条用例，自动 ${TestRegistry.autos().size} 条")
    }

    private fun setupPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = categories.size
            override fun createFragment(position: Int) =
                CategoryListFragment.newInstance(categories[position])
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = categories[pos].title
        }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                refreshSummary()
            }
        })
    }

    fun onCaseClick(case: TestCase) {
        when (case) {
            is AutoTestCase -> {
                if (runJob?.isActive == true) {
                    Toast.makeText(this, "正在跑批量测试，请稍候", Toast.LENGTH_SHORT).show()
                    return
                }
                runJob = lifecycleScope.launch {
                    appendLog("—— 单条 ——")
                    TestRunner.runOneAuto(case) { appendLog(it) }
                    refreshAll()
                }
            }

            is ManualTestCase -> {
                case.status = TestStatus.Running
                refreshAll()
                try {
                    case.onRun(this, case)
                } catch (t: Throwable) {
                    case.status = TestStatus.Failed
                    case.message = t.message
                    TestCoverageStore.persist(case)
                }
                binding.root.postDelayed({ refreshAll() }, 300)
                binding.root.postDelayed({ refreshAll() }, 2000)
            }
        }
    }

    private fun runAutos(category: TestCategory?) {
        if (runJob?.isActive == true) {
            Toast.makeText(this, "已有任务在运行", Toast.LENGTH_SHORT).show()
            return
        }
        val list = TestRegistry.autos(category)
        if (list.isEmpty()) {
            Toast.makeText(this, "本类无自动用例", Toast.LENGTH_SHORT).show()
            return
        }
        val label = if (category == null || category == TestCategory.OVERVIEW) "全部" else category.title
        appendLog("======== 开始跑自动用例：$label (${list.size}) ========")
        runJob = lifecycleScope.launch {
            binding.btnRunAllAuto.isEnabled = false
            binding.btnRunCategoryAuto.isEnabled = false
            try {
                val results = TestRunner.runAuto(
                    list,
                    onEachDone = { runOnUiThread { refreshAll() } },
                    onLog = { msg -> runOnUiThread { appendLog(msg) } },
                )
                val pass = results.count { it.status == TestStatus.Passed }
                val fail = results.count { it.status == TestStatus.Failed }
                appendLog("======== 完成：通过 $pass / 失败 $fail ========")
                Toast.makeText(this@TestHomeActivity, "完成 通过$pass 失败$fail", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnRunAllAuto.isEnabled = true
                binding.btnRunCategoryAuto.isEnabled = true
                refreshAll()
            }
        }
    }

    private fun refreshAll() {
        refreshSummary()
        // ViewPager2 FragmentStateAdapter 默认 tag: f{itemId}
        for (i in categories.indices) {
            (supportFragmentManager.findFragmentByTag("f$i") as? CategoryListFragment)?.refresh()
        }
        supportFragmentManager.fragments.filterIsInstance<CategoryListFragment>().forEach { it.refresh() }
    }

    private fun refreshSummary() {
        val cat = categories.getOrNull(binding.viewPager.currentItem)
        val s = TestRegistry.summary(cat)
        val scope = if (cat == null || cat == TestCategory.OVERVIEW) "全部" else cat.title
        binding.tvSummary.text = "[$scope] ${s.progressText}"
        binding.tvAutoSummary.text = "自动用例：${s.autoPassed}/${s.autoTotal} 通过"
    }

    private fun appendLog(line: String) {
        val t = timeFmt.format(Date())
        binding.tvLog.append("[$t] $line\n")
        binding.scrollLog.post { binding.scrollLog.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
