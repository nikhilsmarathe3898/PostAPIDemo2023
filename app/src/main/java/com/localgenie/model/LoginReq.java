package com.localgenie.model;

/**
 * Created by Pramod on 14/12/17.
 */

public class LoginReq {
    private String emailOrPhone;
    private String password;
    private String deviceId;
    private Integer devType;
    private Integer loginType;

    public LoginReq(String emailOrPhone, String password, String deviceId, Integer devType, Integer loginType) {
        this.emailOrPhone = emailOrPhone;
        this.password = password;
        this.deviceId = deviceId;
        this.devType = devType;
        this.loginType = loginType;
    }

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDevType() {
        return devType;
    }

    public void setDevType(Integer devType) {
        this.devType = devType;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }
}
