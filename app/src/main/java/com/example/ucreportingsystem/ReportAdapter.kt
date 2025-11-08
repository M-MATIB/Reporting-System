package com.example.ucreportingsystem

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ucreportingsystem.databinding.ListItemReportHistoryBinding


class ReportAdapter(
    private var reportList: List<Report>,
    private val onViewDetailsClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    fun updateList(newList: List<Report>) {
        reportList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ListItemReportHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = reportList.size

    inner class ReportViewHolder(private val binding: ListItemReportHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context: Context = itemView.context

        fun bind(report: Report) {
            binding.tvReportId.text = report.id
            binding.tvReportType.text = report.type
            binding.tvReportLocation.text = report.location
            binding.tvReportDate.text = report.date
            binding.tvReportStatusTag.text = report.status.name.replace("_", " ")

            binding.btnViewDetails.setOnClickListener {
                onViewDetailsClick(report)
            }

            when (report.status) {
                ReportStatus.PENDING -> {
                    val color = ContextCompat.getColor(context, R.color.pending)
                    binding.tvReportStatusTag.setTextColor(color)
                    binding.ivStatusIcon.setColorFilter(color)
                    binding.llStatusTag.background = ContextCompat.getDrawable(context, R.drawable.status_pending_bg)
                    binding.ivStatusIcon.setImageResource(R.drawable.pending_icon)

                    val cardColor = ContextCompat.getColor(context, R.color.pending_card)
                    binding.root.setCardBackgroundColor(cardColor)
                }
                ReportStatus.IN_PROGRESS -> {
                    val color = ContextCompat.getColor(context, R.color.in_progress)
                    binding.tvReportStatusTag.setTextColor(color)
                    binding.ivStatusIcon.setColorFilter(color)
                    binding.llStatusTag.background = ContextCompat.getDrawable(context, R.drawable.status_in_progress_bg)
                    binding.ivStatusIcon.setImageResource(R.drawable.in_progress_icon)

                    val cardColor = ContextCompat.getColor(context, R.color.in_progress_card)
                    binding.root.setCardBackgroundColor(cardColor)
                }
                ReportStatus.RESOLVED -> {
                    val color = ContextCompat.getColor(context, R.color.resolved)
                    binding.tvReportStatusTag.setTextColor(color)
                    binding.ivStatusIcon.setColorFilter(color)
                    binding.llStatusTag.background = ContextCompat.getDrawable(context, R.drawable.status_resolved_bg)
                    binding.ivStatusIcon.setImageResource(R.drawable.resolved_icon)

                    val cardColor = ContextCompat.getColor(context, R.color.resolved_card)
                    binding.root.setCardBackgroundColor(cardColor)
                }
            }
        }
    }
}