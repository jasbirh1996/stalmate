package com.example.cabuser.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.TaskStackBuilder.create
import android.content.Context
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.LocationRequest

import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat.create
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

import com.google.android.gms.tasks.*
import java.util.*


/**created by Vaibhav Nayak on 23 June 2021*/

class DeviceLocationManager(var context: Activity, var callbackk: Callbackk) {
    private var REQUEST_CODE_MULTIPLE_PERMISSIONS: Int = 100
    private var REQUEST_CHECK_SETTINGSGPS: Int = 109
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    var mLocationRequest: LocationRequest? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationCallback: LocationCallback? = null
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    init {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        setUpLocatinRequest()
        checkGPS()
        // we first check Permissions
    }

    private fun setUpLocatinRequest() {
       /* mLocationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }*/
    }

    fun checkingPermissions() {
        if (arePermissionGranted(permissions)) {
            //  setUpLocationManagerByInterval()
            requestCurrentLocationLocation()
        }
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocationn() {


       /* if (arePermissionGranted(permissions)) {
            mFusedLocationClient!!.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun isCancellationRequested(): Boolean {
                        return false;
                    }

                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        var cancellationToken = CancellationTokenSource()
                        return cancellationToken!!.token
                    }

                }).addOnSuccessListener {
                if (it != null) {
                    Log.d("CurrentLocation", it.toString());
                    callbackk.onGettingCurrentLocation(it.latitude, it.longitude)
                }
            }.addOnFailureListener({

            })

        }*/

    }


    @SuppressLint("MissingPermission")
    public fun requestCurrentLocationLocation() {
        getCurrentLocationn()
    }

    @SuppressLint("MissingPermission")
    fun setUpLocationManagerByInterval() {


       /* val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.size > 0) {
                    //The last location in the list is the newest
                    val location = locationList[locationList.size - 1]
                    Log.i(
                        "MapsActivity",
                        "Location: " + location.latitude + " " + location.longitude

                    )
                    callbackk.onGettingCurrentLocation(location.latitude, location.longitude)
                }
            }
        }
        this.mLocationCallback = mLocationCallback
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallback,
            Looper.myLooper()!!
        )*/


    }


    fun arePermissionGranted(permissions: Array<String>): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(context, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {// need for permission
            ActivityCompat.requestPermissions(
                context,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_CODE_MULTIPLE_PERMISSIONS
            );
            return false
        }
        return true
    }



    public  fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("asdasd", "asdasd")
        when (requestCode) {
            REQUEST_CODE_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        requestCurrentLocationLocation()
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                context, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            showDialogOK("Location Services Permission required for this app",
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkingPermissions()
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                            }
                                        }
                                    }
                                })
                        } else {
                            Toast.makeText(
                                context,
                                "Go to settings and enable permissions",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    public interface Callbackk {
        fun onGettingCurrentLocation(latitude: Double, longitude: Double)
    }


    private fun hasGPSDevice(context: Context): Boolean {
        val mgr: LocationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
        val providers: List<String> = mgr.getAllProviders() ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }


    private fun checkGPS(){

//        if (!NetworkUtils.isGPSEnabled(context)) {
//            enableGPS()
//        }else{
//            checkingPermissions()
//        }


    }

//    private fun enableGPS(){
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(mLocationRequest!!)
//        builder.setAlwaysShow(true)
//        mLocationSettingsRequest = builder.build()
//
//
//        val task: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
//
//        task.addOnCompleteListener { task ->
//            try {
//                val response: LocationSettingsResponse = task.getResult(ApiException::class.java)!!
//                // All location settings are satisfied. The client can initialize location
//                // requests here.
//
//                getCurrentLocationn()
//            } catch (exception: ApiException) {
//                when (exception.statusCode) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                             // Location settings are not satisfied. But could be fixed by showing the
//                        // user a dialog.
//                        try {
//                            // Cast to a resolvable exception.
//                            val resolvable = exception as ResolvableApiException
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            resolvable.startResolutionForResult(
//                                context,
//                                REQUEST_CHECK_SETTINGSGPS
//                            )
//                        } catch (e: IntentSender.SendIntentException) {
//                            // Ignore the error.
//                        } catch (e: ClassCastException) {
//                            // Ignore, should be an impossible error.
//                        }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    }
//                }
//            }
//        }
//    }


}

