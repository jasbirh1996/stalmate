package com.stalmate.user.view.dashboard.welcome


import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.gson.Gson
import com.stalmate.user.R
import com.stalmate.user.base.App
import com.stalmate.user.base.App.Companion.getInstance
import com.stalmate.user.base.BaseFragment
import com.stalmate.user.base.callbacks.AddressCallbacks
import com.stalmate.user.databinding.FragmentAutoPlaceCompleteBinding
import com.stalmate.user.model.Category
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
    }







    override fun clickPlaces(place: Place?, v: View?) {


      //  addressManager.findAddress(place!!.latLng,true)

        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = App.Companion.getInstance().getGeoCoder()!!

        addresses = geocoder.getFromLocation(
            place!!.latLng.latitude,
            place!!.latLng.longitude,
            1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName

        var bundle= Intent()

        Log.d("alkjdsad",city)
        Log.d("alkjdsad",state)
        Log.d("alkjdsad",country)
       bundle.putExtra("city",city)
        bundle.putExtra("country",country)
        requireActivity().setResult(Activity.RESULT_OK,bundle)
        requireActivity().finish()


    }

    override fun onPickUpAddressFound(pickAddress: String?) {

    }

    override fun onDropAddressFound(dropAddress: String?) {

    }

    override fun onPlaceFoundByAddressManager(address: Address?) {
        var bundle= Intent()
        Log.d("alkjdsad",Gson().toJson(address))
        Log.d("alkjdsad",address!!.adminArea)
        Log.d("alkjdsad",address!!.subAdminArea)
        Log.d("alkjdsad",address!!.locality)
        Log.d("alkjdsad",address!!.subLocality)
        bundle.putExtra("city",address!!.subAdminArea)
        bundle.putExtra("country",address.countryName)
        requireActivity().setResult(Activity.RESULT_OK,bundle)
        requireActivity().finish()
    }

    fun search(searchData:String){
        if (searchData != "") {
            mAutoCompleteAdapter!!.getFilter().filter(searchData.toString())

        } else {
        }
    }



}


