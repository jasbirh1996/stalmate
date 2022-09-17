package com.stalmate.user.view.dashboard.welcome;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import com.stalmate.user.base.App;
import com.stalmate.user.base.callbacks.AddressCallbacks;
import com.stalmate.user.utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddressManager {


    private static AddressCallbacks callbacks;
    private static List<GetAddressInBackground> allAsyncTasksList = new ArrayList<>();

    public void setCallbacks(AddressCallbacks callbacks) {
        AddressManager.callbacks = callbacks;
    }

    public void findAddress(LatLng location, boolean isPick){
        try{
            Log.d("asfhasdfa",String.valueOf(location));
            for(GetAddressInBackground singleTask : allAsyncTasksList){
                singleTask.cancel(true);
                if(singleTask.isCancelled()){
                    allAsyncTasksList.remove(singleTask);
                }
            }
            GetAddressInBackground newTask = new GetAddressInBackground(isPick);
            newTask.execute(location);
            allAsyncTasksList.add(newTask);
        }catch (IllegalStateException ie){ie.printStackTrace();}
    }

    private static class GetAddressInBackground extends AsyncTask<LatLng, Void, String> {

        private boolean isWorkingWithPickUp;

        GetAddressInBackground(boolean isWorkingWithPickUp){
            this.isWorkingWithPickUp = isWorkingWithPickUp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(LatLng... locations) {
            return getAddress(locations[0]);
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);

            if(callbacks!=null){
                Log.e("zzzzzz",address);
                if(isWorkingWithPickUp){
                    Log.e("zzzzzzzzzz",address);
                    callbacks.onPickUpAddressFound(address);
                }else {
                    callbacks.onDropAddressFound(address);
                }
            }
        }
    }

    private static String getAddress(LatLng location){
        Geocoder geocoder = App.Companion.getInstance().getGeoCoder();
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude,
                    location.longitude, 1);
            if (addresses != null&& addresses.size()>0) {
                Address returnedAddress = addresses.get(0);
                try {
                    callbacks.onPlaceFoundByAddressManager(returnedAddress);
                    Log.d("asihd","awsd");
                }catch (Exception ex){
                    Log.d("asihd",ex.getMessage());
                }


                if(returnedAddress.getMaxAddressLineIndex()>0) {




                    StringBuilder strReturnedAddress = new StringBuilder(" ");
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                    }
                    return strReturnedAddress.toString();
                }else {
                    return returnedAddress.getAddressLine(0);
                }
            } else {
                return  Constants.MESSAGE_LOC_API_EMPTY_RESULT;
            }
        } catch (IOException e) {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.latitude,
                        location.longitude, 1);
                if (addresses != null&& addresses.size()>0) {
                    Address returnedAddress = addresses.get(0);
                    if(returnedAddress.getMaxAddressLineIndex()>0) {

                        StringBuilder strReturnedAddress = new StringBuilder(" ");
                        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                        }
                        return strReturnedAddress.toString();
                    }else {
                        return returnedAddress.getAddressLine(0);
                    }
                } else {
                    return  Constants.MESSAGE_LOC_API_EMPTY_RESULT;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                //getAddress(location);
                return Constants.MESSAGE_LOC_API_EMPTY_RESULT;
            }
        }
    }

}
