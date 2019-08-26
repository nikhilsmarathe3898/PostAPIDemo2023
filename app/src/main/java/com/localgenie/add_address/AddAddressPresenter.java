package com.localgenie.add_address;

/**
 * @author Pramod
 * @since 19-01-2018.
 */

public interface AddAddressPresenter {

    void addAddress(String auth, String addLine1, String addLine2, String city, String state, String country, String placeId, String pinCode, Double latitude, Double longitude, String taggedAs, Integer userType, String houseNo, String addressName);

    void editAddress(String auth, String addressId, String houseNo, String addressName, String addrLine1, String addrLine2, String city, String state, String country, String s, String pinCode, double new_lat, double new_long, String tag, int i);
}
