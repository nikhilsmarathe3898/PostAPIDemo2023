package com.pojo;

import java.io.Serializable;

/**
 * <h>PhoneContact</h>
 * Created by Ali on 2/20/2018.
 */

public class PhoneContact implements Serializable
{
    /*"countryCode":"+91",
"phone":"8080808080",
"isCurrentlyActive":true*/

    private String countryCode,phone;

 //   private boolean isCurrentlyActive;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*public boolean isCurrentlyActive() {
        return isCurrentlyActive;
    }

    public void setCurrentlyActive(boolean currentlyActive) {
        isCurrentlyActive = currentlyActive;
    }*/
}
