package com.localgenie.model;

/**
 * Created by Pramod on 14/12/17.
 */

public class ForgotPwdReq {
    private String emailOrPhone;
    private String countryCode;
    private Integer userType;
    private Integer type;

    public ForgotPwdReq(String emailOrPhone, String countryCode, Integer userType, Integer type) {
        this.emailOrPhone = emailOrPhone;
        this.countryCode = countryCode;
        this.userType = userType;
        this.type = type;
    }

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Integer getType() {
        return type;
    }

}