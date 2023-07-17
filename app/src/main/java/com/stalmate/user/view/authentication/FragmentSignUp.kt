package com.stalmate.user.view.authentication


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.databinding.FragmentsignupBinding
import com.stalmate.user.databinding.TermandconditionpopupBinding
import com.stalmate.user.utilities.CustumEditText
import com.stalmate.user.utilities.PriceFormatter
import com.stalmate.user.utilities.ValidationHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class FragmentSignUp : BaseFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentsignupBinding
    private lateinit var bindingpopup: TermandconditionpopupBinding
    var PASSWORDPATTERN =
        "\"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$\""
    private var GANDER: String = ""
    var currentYear = ""
    val SPLASH_DURATION: Long = 1000

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
        setupSpinnerListener()
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
        val views = layoutInflater.inflate(R.layout.termandconditionpopup, null)
        bindingpopup = DataBindingUtil.bind(views)!!

        val cal = Calendar.getInstance()
        currentYear = (cal.get(Calendar.YEAR) - 13).toString()

        with(binding) {

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


            binding.etEmail.addTextChangedListener(object : TextWatcher {
                @SuppressLint("ResourceAsColor")
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (isValidEmail(binding.etEmail.text.toString())) {
                        hitApiCheckOldEmail()
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
                binding.rdmale.isChecked = true
                binding.rdFamel.isChecked = false
                binding.rdOthers.isChecked = false
            }
        }

        binding.rdFamel.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "2"
                binding.rdmale.isChecked = false
                binding.rdFamel.isChecked = true
                binding.rdOthers.isChecked = false
            }
        }


        binding.rdOthers.setOnCheckedChangeListener { compoundButton, ischeck ->
            if (ischeck) {
                GANDER = "3"
                binding.rdmale.isChecked = false
                binding.rdFamel.isChecked = false
                binding.rdOthers.isChecked = true
            }
        }


        binding.btnCrateAccount.setOnClickListener {
            if (isValid()) {
                if (currentYear < selectedYear) {
                    makeToast("Your age should be 13 years or more")
                } else {
                    if (!isUsedEmail || (binding.appCompatImageView12.visibility == View.VISIBLE)) {
                        createAccountApiCall()
                    } else {
                        makeToast("Email Already Used")
                    }
                }
            }
        }

        CustumEditText.setup(binding.filledTextEmail, binding.etEmail)
        CustumEditText.setup(binding.filledTextPassword, binding.etPassword)
    }

    var isUsedEmail = false

    fun isValid(): Boolean {
        lateinit var successdialogBuilder: AlertDialog
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
            return false
        } else if (!binding.rdmale.isChecked && !binding.rdFamel.isChecked && !binding.rdOthers.isChecked) {
            makeToast(getString(R.string.select_gendar_error))
            return false
        } else if (ValidationHelper.isNull(binding.etPassword.text.toString())) {
            makeToast(getString(R.string.password_error_toast))
            return false
        } else if (!isValidPassword(binding.etPassword.text.toString().trim())) {
            Handler(Looper.getMainLooper()).postDelayed({
                successdialogBuilder =
                    AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).create()
                val view = layoutInflater.inflate(R.layout.password_validation_error_popup, null)
                successdialogBuilder.setView(view)
                successdialogBuilder.setCanceledOnTouchOutside(true)
                successdialogBuilder.show()
            }, SPLASH_DURATION)
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

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }


    private fun createAccountApiCall() {
        var gander_name: String = ""
        when (GANDER) {
            "1" -> {
                gander_name = "Male"
            }
            "2" -> {
                gander_name = "Female"
            }
            "3" -> {
                gander_name = "Others"
            }
        }


        val bundle = Bundle()
        bundle.putString("email", binding.etEmail.text.toString())
        bundle.putString("password", binding.etPassword.text.toString())
        bundle.putString("first_name", binding.etName.text.toString())
        bundle.putString("last_name", binding.etLastName.text.toString())
        bundle.putString("gender", gander_name)
//        bundle.putString("schoolandcollege", binding.etschoolcollege.text.toString())
        bundle.putString("dob", "$selectedDay-$selectedMonth-$selectedYear")
        bundle.putString("device_token", App.getInstance().firebaseToken.toString())
        bundle.putString("device_type", "android")
        bundle.putString("layout", "SignUp")
        findNavController().navigate(R.id.fragmentOTPEnter, bundle)
        Log.d("kacajhshc", bundle.toString())
    }


    fun hitApiCheckOldEmail() {
        val hashMap = HashMap<String, String>()
        hashMap["email"] = binding.etEmail.text.toString()
        hashMap["number"] = ""
        networkViewModel.checkIfOldEmail(hashMap)
        networkViewModel.checkIfOldEmailLiveData.observe(requireActivity()) {
            it?.let {
                if (it.status) {
                    binding.appCompatImageView12.visibility = View.VISIBLE
                    isUsedEmail = false
                } else {
                    isUsedEmail = true
                    binding.appCompatImageView12.visibility = View.GONE
                }
            }
        }
    }


    private fun toolbarSetUp() {
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.toolBarCenterText.text = getString(R.string.sign_up)
        binding.toolbar.toolBarCenterText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.visibility = View.GONE
        binding.toolbar.menuChat.visibility = View.VISIBLE
        //binding.toolbar.menuChat.setImageDrawable(resources.getDrawable(R.drawable.ic_signup_top_logo))
        binding.toolbar.menuChat.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_signup_top_logo
            )
        )

        binding.toolbar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    var selectedDay = ""
    var selectedMonth = ""
    var selectedYear = ""
    fun fetchDOB() {
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        selectedMonth = SimpleDateFormat("MMMM").format(Calendar.getInstance().timeInMillis)
        selectedYear = Calendar.getInstance().get(Calendar.YEAR).toString()

        val selectedYearIndex = resources.getStringArray(R.array.year).indexOf(selectedYear)
        val selectedMonthIndex = resources.getStringArray(R.array.month).indexOf(selectedMonth)
        val selectedDayIndex = resources.getStringArray(R.array.date).indexOf(selectedDay)
        binding.spYear.setSelection(selectedYearIndex)
        binding.spMonth.setSelection(selectedMonthIndex)
        binding.spDate.setSelection(selectedDayIndex)
    }


    fun setupSpinnerListener() {
        binding.spDate.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.date)
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spMonth.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.month)
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spYear.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.year)
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }


        binding.spDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                selectedDay = p0!!.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    selectedMonth = p0!!.getItemAtPosition(position).toString()
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
                selectedYear = p0!!.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        fetchDOB()
    }
}