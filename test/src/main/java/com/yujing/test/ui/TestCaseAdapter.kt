package com.yujing.test.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yujing.test.R
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.ManualTestCase
import com.yujing.test.suite.TestCase
import com.yujing.test.suite.TestStatus

class TestCaseAdapter(
    private var items: List<TestCase>,
    private val onClick: (TestCase) -> Unit,
) : RecyclerView.Adapter<TestCaseAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val statusDot: View = view.findViewById(R.id.statusDot)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvMeta: TextView = view.findViewById(R.id.tvMeta)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)
    }

    fun submit(list: List<TestCase>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_test_case, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        val type = when (item) {
            is AutoTestCase -> "自动"
            is ManualTestCase -> "人工"
        }
        val dur = if (item.durationMs > 0) " · ${item.durationMs}ms" else ""
        holder.tvMeta.text = "$type · ${item.status.displayName}$dur · ${item.id}"
        holder.tvBadge.text = type
        holder.tvBadge.setBackgroundColor(if (item is AutoTestCase) 0xFF41B9FA.toInt() else 0xFF7E57C2.toInt())

        val color = when (item.status) {
            TestStatus.Untested -> 0xFFB0BEC5.toInt()
            TestStatus.Running -> 0xFF1E88E5.toInt()
            TestStatus.Passed -> 0xFF43A047.toInt()
            TestStatus.Failed -> 0xFFE53935.toInt()
            TestStatus.Skipped -> 0xFFFFA726.toInt()
        }
        holder.statusDot.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }

        val msg = item.message
        if (!msg.isNullOrBlank() && item.status != TestStatus.Untested) {
            holder.tvMessage.visibility = View.VISIBLE
            holder.tvMessage.text = msg
            holder.tvMessage.setTextColor(
                if (item.status == TestStatus.Failed) 0xFFE53935.toInt() else 0xFF667788.toInt()
            )
        } else {
            holder.tvMessage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setBackgroundColor(Color.WHITE)
    }
}
