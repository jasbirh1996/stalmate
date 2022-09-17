package com.stalmate.user.base.callbacks;


import android.location.Address;

public interface AddressCallbacks {

    void onPickUpAddressFound(String pickAddress);

    void onDropAddressFound(String dropAddress);
    void onPlaceFoundByAddressManager(Address address);

}
