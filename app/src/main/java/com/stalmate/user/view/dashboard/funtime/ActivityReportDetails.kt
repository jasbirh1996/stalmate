package com.stalmate.user.view.dashboard.funtime

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityReportDetailsBinding
import com.stalmate.user.databinding.ItemReportBinding
import com.stalmate.user.databinding.ItemReportResponsesBinding
import com.stalmate.user.model.ReportsListingResponse

class ActivityReportDetails : BaseActivity() {

    var binding: ActivityReportDetailsBinding? = null
    private val getReport: ReportsListingResponse.Reponse?
        get() = intent.getParcelableExtra<ReportsListingResponse.Reponse?>("getReport")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_details)
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            toolbar.topAppBar.setOnClickListener { onBackPressed() }
            getReport?.let {
                toolbar.tvhead.text = "Responses"
                tvTicketId.text = "Ticket ID : #${it?.ticket_id}"
                tvTime.text = "22 May 2021"
                tvDesc.text = it.detailed_reason
                Glide.with(this@ActivityReportDetails).load(it.report_image)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.image).into(ivReport)
                tvReportCategoryDetails.text = it.report_category
                tvReportReasonDetails.text = it.report_reason
                tvDetailedReasonDetails.text = it.detailed_reason
                rvAdminResponses.apply {
                    adapter = ReportDetailsAdapter()
                }
            }
        }
    }

    override fun onClick(viewId: Int, view: View?) {

    }
}

class ReportDetailsAdapter : RecyclerView.Adapter<ReportDetailsAdapter.ReportDetailsViewHolder>() {

    class ReportDetailsViewHolder(var view: ItemReportResponsesBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind() {
            view.tvDesc.text =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt"
            view.tvTime.text = "4:15 A.M. 17/09/2021"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_responses, parent, false)
        return ReportDetailsViewHolder(DataBindingUtil.bind<ItemReportResponsesBinding>(view)!!)
    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: ReportDetailsViewHolder, position: Int) {
        holder.bind()
    }
}