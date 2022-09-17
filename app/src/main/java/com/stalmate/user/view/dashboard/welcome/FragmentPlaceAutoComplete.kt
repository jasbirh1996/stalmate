package com.stalmate.user.view.dashboard.welcome


import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.stalmate.user.R
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.base.callbacks.AddressCallbacks
import com.stalmate.user.databinding.FragmentAutoPlaceCompleteBinding
import com.stalmate.user.model.Category
import com.stalmate.user.model.SelectedList
import com.stalmate.user.view.adapter.AdapterCategory


class FragmentPlaceAutoComplete(var type:TypeFilter) : BaseFragment(), AdapterCategory.Callbackk,
    PlacesAutoCompleteAdapter.ClickListener, AddressCallbacks {
    lateinit var addressManager:AddressManager
    lateinit var binding: FragmentAutoPlaceCompleteBinding
    var datass = ""
    var list = ArrayList<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.bind<FragmentAutoPlaceCompleteBinding>(
            inflater.inflate(
                R.layout.fragment_auto_place_complete,
                container,
                false
            )
        )!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addressManager= AddressManager()
        addressManager.setCallbacks(this)
        Places.initialize(
            requireActivity(),
            resources.getString(R.string.google_direction_api_key)
        )

        autoComplete()


    }

    override fun onClickIntrastedItem(data: ArrayList<Category>) {

        datass = data.toString()

    }
    var mAutoCompleteAdapter: PlacesAutoCompleteAdapter? = null
    private fun autoComplete() {
        mAutoCompleteAdapter = PlacesAutoCompleteAdapter(requireContext(),type)
        mAutoCompleteAdapter!!.setClickListener(this)
        binding.rvList.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        binding.rvList.setAdapter(mAutoCompleteAdapter)
        mAutoCompleteAdapter!!.notifyDataSetChanged()
        searchListener()
    }

    fun searchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString() != "") {
                    mAutoCompleteAdapter!!.getFilter().filter(s.toString())
                  
                } else {
                    
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }







    override fun clickPlaces(place: Place?, v: View?) {


        addressManager.findAddress(place!!.latLng,true)

    }

    override fun onPickUpAddressFound(pickAddress: String?) {

    }

    override fun onDropAddressFound(dropAddress: String?) {

    }

    override fun onPlaceFoundByAddressManager(address: Address?) {
        var bundle= Intent()
        bundle.putExtra("city",address!!.subAdminArea)
        bundle.putExtra("country",address.countryName)
        requireActivity().setResult(Activity.RESULT_OK,bundle)
        requireActivity().finish()
    }



}


