package com.stalmate.user.view.authentication


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentsignupBinding
import com.stalmate.user.databinding.TermandconditionpopupBinding
import com.stalmate.user.utilities.ValidationHelper
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class FragmentSignUp : BaseFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentsignupBinding
    private lateinit var bindingpopup: TermandconditionpopupBinding
    var PASSWORDPATTERN = "\"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$\""
    private var GANDER: String = ""
    val c = Calendar.getInstance()
    var dates: String = ""
    var month: String = ""
    var currentYear: String = ""
    var year: String = ""
    var spinnerArrayFeb = arrayOf("Feb")
    var spinnerArrayFullTwentyEhight = arrayOf("Jan", "Mar", "May", "July", "Aug", "Oct", "Dec")
    var spinnerArrayFullSemihalf = arrayOf("Apr", "Jun", "Sep", "Nov")
    var spinnerArrayFullhalf = arrayOf("jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var spinnerArrayBlank = arrayOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragmentsignup, container, false)
        binding = DataBindingUtil.bind(view)!!

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val views = layoutInflater.inflate(R.layout.termandconditionpopup, null)
        bindingpopup = DataBindingUtil.bind(views)!!



        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerArrayFullhalf
        )

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spMonth.setAdapter(dataAdapter);

//        binding.spDate.setBackgroundColor()


        var cal = Calendar.getInstance()

//        Log.d("ajhcbjahc", cal.get(Calendar.YEAR).toString())
        currentYear = (cal.get(Calendar.YEAR) - 13).toString()


        /*val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.etDOB.text = sdf.format(cal.time)

            }


        // Display Selected date in textbox
        binding.etDOB.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }*/


        with(binding) {


            /*   // Display Selected date in textbox
            etDOB.setOnClickListener {
                DatePickerDialog(
                    requireContext(), dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }*/

            binding.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    dates = p0!!.getItemAtPosition(position).toString()


                    Log.d("jcaujc", dates)
                    if (dates == "31") {

                        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            spinnerArrayFullTwentyEhight
                        )

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        binding.spMonth.setAdapter(dataAdapter);
                    } else if (dates == "30") {
                        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            spinnerArrayFullTwentyEhight
                        )

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        binding.spMonth.setAdapter(dataAdapter);
                    } else if (dates == "28") {
                        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            spinnerArrayFullhalf
                        )

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        binding.spMonth.setAdapter(dataAdapter);
                    } else if (dates != "30" || dates != "31" || dates != "28") {
                        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            spinnerArrayFullhalf
                        )

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        binding.spMonth.setAdapter(dataAdapter);


                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

            binding.tmcondition.setOnClickListener {
                builder.setView(views)
                builder.setCanceledOnTouchOutside(false)
                builder.show()
            }

            bindingpopup.appCompatImageView15.setOnClickListener {

                builder.setView(views)
                builder.setCanceledOnTouchOutside(false)
                builder.dismiss()
            }


            binding.spMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    month = p0!!.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }


            binding.spYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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


            binding.etEmail.addTextChangedListener(object : TextWatcher {
                @SuppressLint("ResourceAsColor")
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    if (isValidEmail(binding.etEmail.text.toString())) {
                        binding.appCompatImageView12.visibility = View.VISIBLE
                    } else {
                        binding.appCompatImageView12.visibility = View.GONE
                    }

                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun afterTextChanged(s: Editable) {

                }
            })

//            binding.etName.addTextChangedListener(object : TextWatcher {
//                @SuppressLint("ResourceAsColor")
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                    if (binding.etName.text!!.isEmpty()) {
//                        binding.appCompatImageView14.visibility = View.GONE
//                    } else {
//
//                        binding.appCompatImageView14.visibility = View.VISIBLE
//                    }
//
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun afterTextChanged(s: Editable) {
//
//                }
//            })

//            binding.etLastName.addTextChangedListener(object : TextWatcher {
//                @SuppressLint("ResourceAsColor")
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//
//                    if (binding.etLastName.text!!.isEmpty()) {
//                        binding.appCompatImageView13.visibility = View.GONE
//                    } else {
//
//                        binding.appCompatImageView13.visibility = View.VISIBLE
//                    }
//
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun afterTextChanged(s: Editable) {
//
//                }
//            })


//            binding.etschoolcollege.addTextChangedListener(object : TextWatcher {
//                @SuppressLint("ResourceAsColor")
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                    if (binding.etschoolcollege.text!!.isEmpty()) {
//                        binding.appCompatImageView16.visibility = View.GONE
//                    } else {
//
//                        binding.appCompatImageView16.visibility = View.VISIBLE
//                    }
//
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun afterTextChanged(s: Editable) {
//
//                }
//            })

//            binding.etPassword.addTextChangedListener(object : TextWatcher {
//                @SuppressLint("ResourceAsColor")
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                    if (!binding.etPassword.text!!.isEmpty() && isValidPassword(binding.etPassword.text.toString().trim())) {
//
//                        binding.appCompatImageView17.visibility = View.VISIBLE
//                    } else {
//                        binding.appCompatImageView17.visibility = View.GONE
//                    }
//
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun afterTextChanged(s: Editable) {
//
//                }
//            })
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Common ToolBar SetUp*/
        toolbarSetUp()

        binding.rdmale.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "1"
                binding.rdmale.setChecked(true)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(false)
            }
        }

        binding.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "2"
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(true)
                binding.rdOthers.setChecked(false)
            }
        }


        binding.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "3"
                binding.rdmale.setChecked(false)
                binding.rdFamel.setChecked(false)
                binding.rdOthers.setChecked(true)
            }
        }


        binding.btnCrateAccount.setOnClickListener {
            if (isValid()) {

                if (currentYear < year) {
                    makeToast("Your age should be 13 years or more")
                }else {
                    createAccountApiCall()
                }
            }
//            findNavController().navigate(R.id.fragmentOTPEnter)
        }
    }


    fun isValid(): Boolean {

        if (ValidationHelper.isNull(binding.etName.text.toString())) {

            makeToast(getString(R.string.first_name_toast))
            return false
        } else if (ValidationHelper.isNull(binding.etLastName.text.toString())) {
            makeToast(getString(R.string.last_name_toast))
            return false
        } else if (ValidationHelper.isNull(binding.etEmail.text.toString())) {
            makeToast(getString(R.string.email_error_toast))
            return false
        } else if (!isValidEmail(binding.etEmail.text.toString())) {
            makeToast(getString(R.string.please_enter_valid_email))
            return false;
        } else if (!binding.rdmale.isChecked && !binding.rdFamel.isChecked && !binding.rdOthers.isChecked) {
            makeToast(getString(R.string.select_gendar_error))
            return false
        } else /*if (ValidationHelper.isNull(binding.etschoolcollege.text.toString())) {
            makeToast(getString(R.string.pleaseenterschoolcollegename))
            return false
        } else*/ if (ValidationHelper.isNull(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        } else  if (!isValidPassword(binding.etPassword.text.toString().trim())) {
                makeToast("Password Must Include Atleast: 1 uppercase,\n1 Lowercase,\n1 Number & 1 Spaecial Character")
                return false
            } else if (!binding.tmcheckbox.isChecked) {
                makeToast(getString(R.string.accept_tnc))
                return false

        }
        return true
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }


    private fun createAccountApiCall() {
        var gander_name: String = ""
        if (GANDER == "1") {
            gander_name = "Male"
        } else if (GANDER == "2") {
            gander_name = "Female"
        } else if (GANDER == "3") {
            gander_name = "Others"
        }



        val bundle = Bundle()
        bundle.putString("email", binding.etEmail.text.toString())
        bundle.putString("password", binding.etPassword.text.toString())
        bundle.putString("first_name", binding.etName.text.toString())
        bundle.putString("last_name", binding.etLastName.text.toString())
        bundle.putString("gender",  gander_name)
//        bundle.putString("schoolandcollege", binding.etschoolcollege.text.toString())
        bundle.putString("dob",  year+"-"+month+"-"+dates)
        bundle.putString("year",  year)
        bundle.putString("month", month)
        bundle.putString("date",  dates)
        bundle.putString("device_token", App.getInstance().firebaseToken.toString())
        bundle.putString("device_type", "android")
        bundle.putString("layout", "SignUp")
        findNavController().navigate(R.id.fragmentOTPEnter, bundle)
        Log.d("kacajhshc", bundle.toString())

       /* val hashMap = HashMap<String, String>()
        hashMap["email"] = binding.etEmail.text.toString()
        hashMap["password"] = binding.etPassword.text.toString()
        hashMap["first_name"] = binding.etName.text.toString()
        hashMap["last_name"] = binding.etLastName.text.toString()
        hashMap["gender"] = gander_name
        hashMap["schoolandcollege"] = binding.etschoolcollege.text.toString()
        hashMap["dob"] = year+"-"+month+"-"+dates
        hashMap["device_id"] = ""
        hashMap["device_token"] = App.getInstance().firebaseToken.toString()
        hashMap["device_type"] = "android"
        binding.progressBar.visibility = View.VISIBLE
        networkViewModel.registration(hashMap)
        networkViewModel.registerData.observe(requireActivity()) {

            it?.let {
                val message = it.message

                if (it.status == true) {

                    PrefManager.getInstance(requireContext())!!.userDetail = it
                    val bundle = Bundle()
                    bundle.putString("email", binding.etEmail.text.toString())
                    bundle.putString("password", binding.etPassword.text.toString())
                    bundle.putString("first_name", binding.etName.text.toString())
                    bundle.putString("last_name", binding.etLastName.text.toString())
                    bundle.putString("gender",  gander_name)
                    bundle.putString("schoolandcollege", binding.etschoolcollege.text.toString())
                    bundle.putString("dob",  year+"-"+month+"-"+dates)
                    bundle.putString("device_token", App.getInstance().firebaseToken.toString())
                    bundle.putString("device_type", "android")
                    bundle.putString("layout", "SignUp")
                    findNavController().navigate(R.id.fragmentOTPEnter, bundle)
                    Log.d("kacajhshc", bundle.toString())
                    makeToast(message)
                } else {
                    makeToast(message)
                }
            }
            binding.progressBar.visibility = View.GONE
        }*/
    }

    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text = getString(R.string.sign_up)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.visibility = View.GONE
        binding.toolbar.menuChat.visibility = View.VISIBLE
        binding.toolbar.menuChat.setImageDrawable(getResources().getDrawable(R.drawable.ic_signup_top_logo));

        binding.toolbar.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}