package com.example.nammanala

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nammanala.databinding.ItemReportBinding

class ReportAdapter(private val reports: List<ReportModel>) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.binding.tvItemDescription.text = report.title
        holder.binding.tvItemVillage.text = "Village: ${report.village}"
        holder.binding.tvItemDescriptionFull.text = report.description
        holder.binding.tvItemLocation.text = "Lat: ${report.latitude}, Lon: ${report.longitude}"
        holder.binding.tvItemDate.text = "Status: ${report.status} • ${report.timestamp} (${report.timeAgo})"
    }

    override fun getItemCount(): Int = reports.size
}
