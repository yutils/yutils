package com.yujing.test.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yujing.test.R
import com.yujing.test.suite.TestCategory
import com.yujing.test.suite.TestRegistry

class CategoryListFragment : Fragment() {
    private var category: TestCategory = TestCategory.OVERVIEW
    private var adapter: TestCaseAdapter? = null

    companion object {
        private const val ARG_CAT = "cat"
        fun newInstance(category: TestCategory): CategoryListFragment {
            return CategoryListFragment().apply {
                arguments = Bundle().apply { putString(ARG_CAT, category.name) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = runCatching {
            TestCategory.valueOf(arguments?.getString(ARG_CAT) ?: TestCategory.OVERVIEW.name)
        }.getOrDefault(TestCategory.OVERVIEW)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_test_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        adapter = TestCaseAdapter(TestRegistry.byCategory(category)) { case ->
            (activity as? TestHomeActivity)?.onCaseClick(case)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
    }

    fun refresh() {
        adapter?.submit(TestRegistry.byCategory(category))
    }
}
