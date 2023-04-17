package com.stalmate.user.view.dashboard.funtime

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.anggrayudi.storage.file.*
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.exoplayer2.util.Log
import com.jaiselrahman.filepicker.utils.FileUtils
import com.stalmate.user.Helper.IntentHelper
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityReportUserBinding
import com.stalmate.user.model.ModelCustumSpinner
import com.stalmate.user.view.dialogs.CommonConfirmationDialog
import com.stalmate.user.view.dialogs.SuccessDialog
import com.wedguruphotographer.adapter.CustumSpinAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ActivityReportUser : BaseActivity(), DialogFilePicker.Callback {
    lateinit var binding: ActivityReportUserBinding
    private lateinit var categoryAdapter: CustumSpinAdapter

    var selectedCategory: ModelCustumSpinner? = null
    val categoryList: ArrayList<ModelCustumSpinner> = ArrayList<ModelCustumSpinner>()
    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_user)

        val category = ModelCustumSpinner(id = "0", name = "Select the category")
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
            categoryList, false
        )
        binding.spinnerCategory.setAdapter(categoryAdapter)



        binding.layoutPick.setOnClickListener {

            var dialog = DialogFilePicker(this, isFile = false)
            val manager: FragmentManager = getSupportFragmentManager()
            dialog.show(manager, "asdasd")


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
            if (this::file.isInitialized) {

                report()
            } else {
                makeToast("Please Upload a File First...")
            }

        }
        binding.toolbar.tvhead.text = "File a Report"
        binding.toolbar.topAppBar.setNavigationOnClickListener {
            finish()
        }

    }

    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(this) // optional usage
            file = File(uriFilePath)
            try {
                Glide.with(this).load(result.getBitmap(this)).into(binding.ivReport)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // an error occurred
            val exception = result.error
        }
    }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    fun pickFile() {
        val i = Intent()
        i.type = "*/*"
        i.action = Intent.ACTION_GET_CONTENT
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(i, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            file = File(
                com.stalmate.user.modules.reels.audioVideoTrimmer.utils.FileUtils.getPath(
                    this,
                    data!!.data
                )
            )
            try {
                Glide.with(this).load(file).into(binding.ivReport)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    lateinit var file: File

    private fun report() {
        val successDialog = SuccessDialog(
            context = this,
            heading = "Success",
            message = "Your report has been successfully submitted\n\nWe will get back to you Shortly",
            buttonPrimary = "Okay",
            callback = object : SuccessDialog.Callback {
                override fun onDialogResult(isPermissionGranted: Boolean) {
                    finish()
                }
            },
            icon = R.drawable.baseline_check_circle_24
        )
        showLoader()
        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), file)

        if (intent.getStringExtra("id").isNullOrEmpty()) {
            val report_image: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
                "report_image",
                file.name,
                thumbnailBody
            ) //image[] for multiple image

            networkViewModel.reportProblem(
                access_token = prefManager?.access_token.toString(),
                report_image = report_image,
                report_category = getRequestBody(selectedCategory?.name),
                report_reason = getRequestBody(binding.spinnerReportReason.text.toString()),
                detailed_reason = getRequestBody(binding.etDetailedReason.text.toString())
            ).observe(this, Observer {
                dismissLoader()
                successDialog.show()
            })
        } else {
            val profile_image1: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
                "file",
                file.name,
                thumbnailBody
            ) //image[] for multiple image

            networkViewModel.reportFuntime(
                profile_image1,
                getRequestBody(intent.getStringExtra("id")),
                getRequestBody(selectedCategory?.name),
                getRequestBody(binding.spinnerReportReason.text.toString()),
                getRequestBody(binding.etDetailedReason.text.toString())
            ).observe(this, Observer {
                dismissLoader()
                successDialog.show()
            })
        }
    }

    override fun onClickOnFilePicker(isFilePicker: Boolean) {
        if (isFilePicker) {
            pickFile()
        } else {
            startCrop()
        }
    }
}





