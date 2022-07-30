package com.slatmate.user.base.callbacks;




public interface MapCallbacks extends AddressCallbacks {

    void onPickAddressFetchStart();

    void onDropAddressFetchStart();

    @Override
    void onPickUpAddressFound(String pickAddress);

    @Override
    void onDropAddressFound(String dropAddress);

    void drawPolylineBetweenMarkers();

    void hideBottomOptions();

    void showBottomOptions();
    void onGettinngPolygonData( String distanceInKM, int distanceInMetres, String timeInMin, int timeInSeconds);

    void recreateBottomOptions();

    void blockTouch();

    void unBlockTouch();



}
