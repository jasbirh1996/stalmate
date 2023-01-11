package com.stalmate.user.view.dashboard.funtime

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.callback.FilePickerCallback
import com.anggrayudi.storage.file.*
import com.google.android.exoplayer2.util.Log
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityReportUserBinding
import com.stalmate.user.model.ModelCustumSpinner
import com.stalmate.user.modules.reels.utils.VideoUtil
import com.stalmate.user.utilities.PathUtil
import com.wedguruphotographer.adapter.CustumSpinAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class ActivityReportUser : BaseActivity() {
    lateinit var binding: ActivityReportUserBinding
    private lateinit var categoryAdapter: CustumSpinAdapter

    var selectedCategory: ModelCustumSpinner? = null
    val categoryList: ArrayList<ModelCustumSpinner> = ArrayList<ModelCustumSpinner>()
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_user)


        var category = ModelCustumSpinner(id = "0", name = "Select category")
        categoryList.add(category)
        categoryList.add(ModelCustumSpinner(id = "0", name = "I just don't like it."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "It's spam."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "Nudity or sexual activity."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "Hate speech or symbols."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "Scam or fraud."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "False information."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "Bullying or harassment."))
        categoryList.add(
            ModelCustumSpinner(
                id = "0",
                name = "Violence or dangerous organisations."
            )
        )
        categoryList.add(ModelCustumSpinner(id = "0", name = "Eating disorders."))
        categoryList.add(ModelCustumSpinner(id = "0", name = "Something else."))
        categoryAdapter = CustumSpinAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryList, true
        )
        binding.spinnerCategory.setAdapter(categoryAdapter)



        binding.layoutPick.setOnClickListener {
          //  pickFile()
        }
        binding.spinnerCategory.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedCategory = parent!!.getItemAtPosition(position) as ModelCustumSpinner
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        binding.buttonDone.setOnClickListener {
            report()
        }
        binding.toolbar.tvhead.text="File a Report"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            finish()
        }

    }





    lateinit var file: File


    private fun report() {
        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), file!!)

        val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
            "file",
            file!!.name,
            thumbnailBody
        ) //image[] for multiple image

        Log.d("kgfjuy", intent.getStringExtra("id").toString())
        networkViewModel.reportFuntime(
            profile_image1,
            getRequestBody(intent.getStringExtra("id")),
            getRequestBody(selectedCategory!!.name),
            getRequestBody(binding.spinnerReportReason.text.toString()),
            getRequestBody(binding.etDetailedReason.text.toString())
        ).observe(this, Observer {
            it.let {

                if (it!!.status!!) {
                    makeToast(it.message)
                    finish()


                }
            }
        })
    }

}





