package com.stalmate.user.utilities;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import com.stalmate.user.base.App;


public class NetworkUtils {

    public static boolean isNetworkAvailable() {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) App.Companion.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

            return connectivityManager!=null && connectivityManager.getActiveNetworkInfo()!=null &&
                    connectivityManager.getActiveNetworkInfo().isConnected();
        }catch (Exception e){
            return true;
        }

    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
