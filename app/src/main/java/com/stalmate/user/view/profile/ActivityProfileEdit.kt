package com.stalmate.user.view.profile

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.igalata.bubblepicker.model.Color
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityProfileEditBinding
import com.stalmate.user.model.AboutProfileLine
import com.stalmate.user.model.Education
import com.stalmate.user.model.Profession
import com.stalmate.user.model.User
import com.stalmate.user.utilities.ImageLoaderHelperGlide
import com.stalmate.user.view.adapter.ProfileAboutAdapter
import com.stalmate.user.view.photoalbum.AlbumAdapter
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.HashMap

class ActivityProfileEdit : BaseActivity(), EducationListAdapter.Callbackk, ProfessionListAdapter.Callbackk{

    private lateinit var binding: ActivityProfileEditBinding
    val PICK_IMAGE_PROFILE = 2
    val PICK_IMAGE_COVER = 1
    var WRITE_REQUEST_CODE = 100
    private var GANDER: String = ""
    var dates: String = ""
    var month: String = ""
    var year: String = ""
    var merriage: String = ""
    var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    val requiredPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    lateinit var userData: User
    var spinnerArrayFeb = arrayOf("Feb")
    var spinnerArrayFull = arrayOf("Jan", "Mar", "May", "July", "Aug", "Oct", "Dec")
    var spinnerArrayFullSemihalf = arrayOf("Apr", "Jun", "Sep", "Nov")
    var spinnerArrayFullhalf =
        arrayOf("jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var spinnerArrayBlank = arrayOf("")
    var imageFile: File? = null
    var isCoverImage = false
    private lateinit var educationAdapter: EducationListAdapter
    private lateinit var professionListAdapter: ProfessionListAdapter

    override fun onClick(viewId: Int, view: View?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        getUserProfileData()
        requestPermissions(permissions, WRITE_REQUEST_CODE)
        binding.layout.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "1"
                binding.layout.rdmale.setChecked(true)
                binding.layout.rdFamel.setChecked(false)
                binding.layout.rdOthers.setChecked(false)
            }
        }

        binding.layout.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "2"
                binding.layout.rdmale.setChecked(false)
                binding.layout.rdFamel.setChecked(true)
                binding.layout.rdOthers.setChecked(false)
            }
        }


        binding.layout.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "3"
                binding.layout.rdmale.setChecked(false)
                binding.layout.rdFamel.setChecked(false)
                binding.layout.rdOthers.setChecked(true)
            }
        }



        binding.layout.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                dates = p0!!.getItemAtPosition(position).toString()

                Log.d("jcaujc", dates)
                if (dates == "31") {

                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(this@ActivityProfileEdit, android.R.layout.simple_spinner_item,
                        spinnerArrayFull
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates == "30") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFullSemihalf
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates == "28") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFeb
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter);
                } else if (dates != "30" || dates != "31" || dates != "28") {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        this@ActivityProfileEdit,
                        android.R.layout.simple_spinner_item,
                        spinnerArrayFullhalf
                    )

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    binding.layout.spMonth.setAdapter(dataAdapter)
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.layout.spMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    month = p0!!.getItemAtPosition(position).toString()
                    Log.d("jcaujc", month)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }


        binding.layout.spYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                year = p0!!.getItemAtPosition(position).toString()
                Log.d("jcaujc", year)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        clickLister()
    }

    private fun clickLister() {

        binding.idCoverPhoto.setOnClickListener {

            isCoverImage=true
            startCrop()
        }

        binding.ivUserProfileImage.setOnClickListener {
            isCoverImage=false
            startCrop()
        }


        binding.btnCrateAccount.setOnClickListener {
            updateProfileApiHit()
        }

        binding.layout.tvAddMore.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialouge_add_education, viewGroup, false)

            builder.setView(dialogView)
            val alertDialog = builder.create()

            alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));

            var btnSave  = dialogView.findViewById<TextView>(R.id.btnSave)
            var btnClose  = dialogView.findViewById<ImageView>(R.id.ivClose)

            btnSave.setOnClickListener {

                var graduation = dialogView.findViewById<EditText>(R.id.etGraduation)
                var bachlore = dialogView.findViewById<EditText>(R.id.etBachlore)
                var bachloreType = dialogView.findViewById<EditText>(R.id.etBachloreType)

                if (graduation.text.isEmpty()){
                    makeToast("Please Enter College And University Name")
                }else if (bachlore.text.isEmpty()){
                    makeToast("Please Enter Education Type")
                }else if (bachloreType.text.isEmpty()){
                    makeToast("Please Enter Subject Type")
                }else{

                    val hashMap = HashMap<String, String>()

                    hashMap["sehool"] =graduation.text.toString()
                    hashMap["branch"] =bachlore.text.toString()
                    hashMap["course"] = bachloreType.text.toString()

                    networkViewModel.educationData(hashMap)
                    networkViewModel.educationData.observe(this){
                        it?.let {
                            if (it.status){
                                makeToast(it.message)
                                getUserProfileData()
                                alertDialog.dismiss()

                            }
                        }
                    }

                }

            }

            btnClose.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
            alertDialog.setCancelable(true)
        }

        binding.layout.tvAddMoreProfession.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialouge_add_profession, viewGroup, false)
            var curretlyWorkingStatus = "No"

            builder.setView(dialogView)
            val alertDialog = builder.create()

            alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));

            var btnSave  = dialogView.findViewById<TextView>(R.id.btnSave)
            var btnClose  = dialogView.findViewById<ImageView>(R.id.ivClose)
            var from = dialogView.findViewById<TextView>(R.id.tvCdFrom)
            var to = dialogView.findViewById<TextView>(R.id.tvCdTo)

            var cal = Calendar.getInstance()

            var companyName = dialogView.findViewById<EditText>(R.id.etCompany)
            var designation = dialogView.findViewById<EditText>(R.id.etDesignation)

            var radio = dialogView.findViewById<RadioButton>(R.id.radioButtonCurrentWork)



            // Display Selected date in textbox
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd-MM-yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    from.text = sdf.format(cal.time)

                }

            from.setOnClickListener {
                DatePickerDialog(
                    this, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // Display Selected End date in textbox
            val dateEndSetListener =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd-MM-yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    to.text = sdf.format(cal.time)

                }

            to.setOnClickListener {
                DatePickerDialog(
                    this, dateEndSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            btnSave.setOnClickListener {

                if (companyName.text.isEmpty()){
                    makeToast("Please Enter Company Name")
                }else if (designation.text.isEmpty()){
                    makeToast("Please Enter Desigantion")
                }else if (from.text.isEmpty()){
                    makeToast("Please Enter Starting Date")
                }else if (!radio.isChecked) {
                           if (to.text.isEmpty()) {
                               makeToast("Please Enter End Date")
                           }
                }else {

                        val hashMap = HashMap<String, String>()

                        hashMap["company_name"] =companyName.text.toString()
                        hashMap["currently_working_here"] =curretlyWorkingStatus
                        hashMap["to"] = to.text.toString()
                        hashMap["from"] = from.text.toString()
                        hashMap["designation"] = designation.text.toString()

                        networkViewModel.professionData(hashMap)
                        networkViewModel.professionData.observe(this){
                            it?.let {
                                if (it.status == true){
                                    makeToast(it.message)
                                    getUserProfileData()
                                    alertDialog.dismiss()

                                }
                            }
                        }

                    }
            }

            btnClose.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
            alertDialog.setCancelable(true)
        }


        binding.layout.tvMarriage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    merriage = p0!!.getItemAtPosition(position).toString()
                    Log.d("jcaujc", year)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

    }


    private fun updateProfileApiHit() {

        fun getRequestBody(str: String?): RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())


        networkViewModel.etsProfileApi(
            getRequestBody(binding.layout.etName.text.toString()),
            getRequestBody(binding.layout.etLastName.text.toString()),
            getRequestBody(binding.layout.bio.text.toString()),
            getRequestBody(binding.layout.etNumber.text.toString()),
            getRequestBody(year + "-" + month + "-" + dates),
            getRequestBody(merriage),
            getRequestBody(binding.layout.etHowTown.text.toString()),
            getRequestBody(binding.layout.etCurrentCity.text.toString()),
            getRequestBody(""),
           /* getRequestBody(binding.layout.etCompany.text.toString()),*/
            getRequestBody(GANDER),
        )

        networkViewModel.UpdateProfileLiveData.observe(this, Observer {

            it.let {


                makeToast(it!!.message)
            }
        })
    }


    private fun updateProfileImageApiHit() {

        fun getRequestBody(str: String?): RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), str.toString())

        val thumbnailBody: RequestBody =
            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)

        fun getMultipart(str: File): MultipartBody.Part = MultipartBody.Part.createFormData(
            "cover_img".takeIf { isCoverImage } ?: "profile_img",
            str.name,
            thumbnailBody
        )


        networkViewModel.etsProfileApi(getMultipart(imageFile!!),)

    }



    override fun onDestroy() {
        super.onDestroy()
    }


    /*Cover Image Picker */
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            var uriFilePath = result.getUriFilePath(this) // optional usage
            imageFile = File(result.getUriFilePath(this, true)!!)
            Log.d("imageUrl======", uriContent.toString())
            Log.d("imageUrl======", uriFilePath.toString())

            if (isCoverImage){
                Glide.with(this).load(uriContent).into(binding.ivBackground)
            }else{
                Glide.with(this).load(uriContent).into(binding.ivUserThumb)
            }
           updateProfileImageApiHit()

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



    fun getUserProfileData() {
        var hashMap = HashMap<String, String>()
        networkViewModel.getProfileData( hashMap)
        networkViewModel.profileLiveData.observe(this, Observer {
            it.let {
                userData = it!!.results
                setUpAboutUI()

                if (it.results.profile_data[0].education.isNotEmpty()){
                    binding.layout.rvEducation.visibility = View.VISIBLE
                }

                if (it.results.profile_data[0].profession.isNotEmpty()){
                    binding.layout.rvProfession.visibility = View.VISIBLE
                }

            }
        })
    }


    fun setUpAboutUI() {
        binding.layout.etName.setText(userData.first_name)
        binding.layout.etLastName.setText(userData.last_name)
        binding.layout.bio.setText(userData.about)
        binding.layout.etEmail.setText(userData.email)
        binding.layout.etNumber.setText(userData.number)
        ImageLoaderHelperGlide.setGlide(this,binding.ivBackground,userData.img_url+userData.cover_img1)
        ImageLoaderHelperGlide.setGlide(this,binding.ivUserThumb,userData.img_url+userData.profile_img1)


        educationAdapter = EducationListAdapter(networkViewModel,this, this)
        binding.layout.rvEducation.adapter=educationAdapter
        binding.layout.rvEducation.layoutManager= LinearLayoutManager(this)

        educationAdapter.submitList(userData.profile_data.get(0).education)


        professionListAdapter = ProfessionListAdapter(networkViewModel,this, this)
        binding.layout.rvProfession.adapter=professionListAdapter
        binding.layout.rvProfession.layoutManager= LinearLayoutManager(this)

        professionListAdapter.submitList(userData.profile_data.get(0).profession)


    }

    override fun onClickItemDelete(position: Int) {
        val hashMap = HashMap<String, String>()

        hashMap["id"] = position.toString()
        hashMap["is_delete"] = "1"

        networkViewModel.educationData(hashMap)
        networkViewModel.educationData.observe(this){
            it?.let {
                if (it.status){
                    makeToast(it.message)
                }
            }
        }
    }

    override fun onClickItemEdit(position: Int) {

        var list = ArrayList<Education>()

        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialouge_add_education, viewGroup, false)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));

        var btnSave  = dialogView.findViewById<TextView>(R.id.btnSave)
        var btnClose  = dialogView.findViewById<ImageView>(R.id.ivClose)

        btnSave.setOnClickListener {

            var graduation = dialogView.findViewById<EditText>(R.id.etGraduation)
            var bachlore = dialogView.findViewById<EditText>(R.id.etBachlore)
            var bachloreType = dialogView.findViewById<EditText>(R.id.etBachloreType)


            graduation.setText(list.get(position).sehool)
            bachlore.setText(list.get(position).branch)
            bachloreType.setText(list.get(position).course)


            if (graduation.text.isEmpty()){
                makeToast("Please Enter College And University Name")
            }else if (bachlore.text.isEmpty()){
                makeToast("Please Enter Education Type")
            }else if (bachloreType.text.isEmpty()){
                makeToast("Please Enter Subject Type")
            }else{

                val hashMap = HashMap<String, String>()

                hashMap["sehool"] =graduation.text.toString()
                hashMap["branch"] =bachlore.text.toString()
                hashMap["course"] = bachloreType.text.toString()
                hashMap["id"] = position.toString()

                networkViewModel.educationData(hashMap)
                networkViewModel.educationData.observe(this){
                    it?.let {
                        if (it.status){
                            makeToast(it.message)
                            alertDialog.dismiss()
                        }
                    }
                }

            }

        }

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        alertDialog.setCancelable(true)


    }

    override fun onClickItemProfessionDelete(position: Int) {

        val hashMap = HashMap<String, String>()

        hashMap["id"] = position.toString()
        hashMap["is_delete"] = "1"

        networkViewModel.professionData(hashMap)
        networkViewModel.professionData.observe(this){
            it?.let {
                if (it.status == true){
                    makeToast(it.message)
                }
            }
        }

    }

    override fun onClickItemProfessionEdit(position: Int) {

        var list = ArrayList<Profession>()

        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialouge_add_profession, viewGroup, false)
        var curretlyWorkingStatus = "No"

        builder.setView(dialogView)
        val alertDialog = builder.create()

        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));

        var btnSave  = dialogView.findViewById<TextView>(R.id.btnSave)
        var btnClose  = dialogView.findViewById<ImageView>(R.id.ivClose)
        var from = dialogView.findViewById<TextView>(R.id.tvCdFrom)
        var to = dialogView.findViewById<TextView>(R.id.tvCdTo)

        var cal = Calendar.getInstance()

        var companyName = dialogView.findViewById<EditText>(R.id.etCompany)
        var designation = dialogView.findViewById<EditText>(R.id.etDesignation)

        var radio = dialogView.findViewById<RadioButton>(R.id.radioButtonCurrentWork)


        companyName.setText(list.get(position).company_name)
        designation.setText(list.get(position).designation)
        from.setText(list.get(position).from)
        to.setText(list.get(position).to)


        // Display Selected date in textbox
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                from.text = sdf.format(cal.time)

            }

        from.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Display Selected End date in textbox
        val dateEndSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                to.text = sdf.format(cal.time)

            }

        to.setOnClickListener {
            DatePickerDialog(
                this, dateEndSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSave.setOnClickListener {

            if (companyName.text.isEmpty()){
                makeToast("Please Enter Company Name")
            }else if (designation.text.isEmpty()){
                makeToast("Please Enter Desigantion")
            }else if (from.text.isEmpty()){
                makeToast("Please Enter Starting Date")
            }else if (!radio.isChecked) {
                if (to.text.isEmpty()) {
                    makeToast("Please Enter End Date")
                }
            }else {

                val hashMap = HashMap<String, String>()

                hashMap["company_name"] =companyName.text.toString()
                hashMap["currently_working_here"] =curretlyWorkingStatus
                hashMap["to"] = to.text.toString()
                hashMap["from"] = from.text.toString()
                hashMap["designation"] = designation.text.toString()

                networkViewModel.professionData(hashMap)
                networkViewModel.professionData.observe(this){
                    it?.let {
                        if (it.status == true){
                            makeToast(it.message)
                        }
                    }
                }

            }
        }

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        alertDialog.setCancelable(true)
    }


}