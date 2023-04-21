package com.stalmate.user.view.dashboard.funtime

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityReportListingBinding
import com.stalmate.user.databinding.ItemReportBinding
import com.stalmate.user.model.ReportsListingResponse

class ActivityReportListing : BaseActivity() {

    var binding: ActivityReportListingBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_listing)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        showLoader()
        networkViewModel.getReportList(prefManager?.access_token.toString())
    }

    private fun initViews() {
        binding?.apply {
            toolbar.topAppBar.setOnClickListener { onBackPressed() }
            toolbar.tvhead.text = "Reports"
            ivAddReport.setOnClickListener {
                startActivity(Intent(this@ActivityReportListing, ActivityReportUser::class.java))
            }
        }
        networkViewModel.getReportListLiveData.observe(this) {
            dismissLoader()
            if (!it?.reponse.isNullOrEmpty()) {
                binding?.rvReports?.visibility = View.VISIBLE
                binding?.tvNoData?.visibility = View.GONE
                binding?.rvReports?.apply {
                    adapter = ReportListingAdapter(it?.reponse)
                }
            } else {
                binding?.rvReports?.visibility = View.GONE
                binding?.tvNoData?.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(viewId: Int, view: View?) {

    }
}

class ReportListingAdapter(val response: ArrayList<ReportsListingResponse.Reponse?>?) :
    RecyclerView.Adapter<ReportListingAdapter.ReportViewHolder>() {

    class ReportViewHolder(var view: ItemReportBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(get: ReportsListingResponse.Reponse?) {
            view.tvTicketId.text = "Ticket ID : #${get?.ticket_id}"
            view.tvTime.text = "22 May 2021"
            view.tvDesc.text = "${get?.detailed_reason}"
            view.tvViewRes.setOnClickListener {
                it.context.startActivity(
                    Intent(
                        it.context,
                        ActivityReportDetails::class.java
                    ).apply {
                        putExtra("getReport", get)
                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(DataBindingUtil.bind<ItemReportBinding>(view)!!)
    }

    override fun getItemCount(): Int = (response?.size ?: 0)

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(response?.get(position))
    }
}
