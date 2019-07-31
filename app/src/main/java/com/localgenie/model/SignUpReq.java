package com.localgenie.model;

/**
 * @author  Pramod
 * @since  14/12/17.
 */

public class SignUpReq {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String countryCode;
    private String phone;
    private String dateOfBirth;
    private String profilePic;
    private Integer loginType;
    private String facebookId;
    private String googleId;
    private Double latitude;
    private Double longitude;
    private String preferredGenres;

    private Integer termsAndCond;
    private Integer devType;
    private String deviceId;
    private String pushToken;
    private String appVersion;
    private String deviceOsVersion;
    private String devMake;
    private String devModel;
    private String deviceTime;
    private String referralCode;

    public SignUpReq(String firstName, String lastName, String email, String password, String countryCode, String phone, String profilePic, Integer loginType, String facebookId, String googleId, Double latitude, Double longitude, String preferredGenres, Integer termsAndCond, Integer devType, String deviceId, String pushToken, String appVersion, String deviceOsVersion, String devMake, String devModel, String deviceTime, String referralCode, String ipAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.profilePic = profilePic;
        this.loginType = loginType;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferredGenres = preferredGenres;
        this.termsAndCond = termsAndCond;
        this.devType = devType;
        this.deviceId = deviceId;
        this.pushToken = pushToken;
        this.appVersion = appVersion;
        this.deviceOsVersion = deviceOsVersion;
        this.devMake = devMake;
        this.devModel = devModel;
        this.deviceTime = deviceTime;
        this.referralCode = referralCode;
        this.ipAddress = ipAddress;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPreferredGenres() {
        return preferredGenres;
    }

    public void setPreferredGenres(String preferredGenres) {
        this.preferredGenres = preferredGenres;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    private String ipAddress;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getTermsAndCond() {
        return termsAndCond;
    }

    public void setTermsAndCond(Integer termsAndCond) {
        this.termsAndCond = termsAndCond;
    }

    public Integer getDevType() {
        return devType;
    }

    public void setDevType(Integer devType) {
        this.devType = devType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceOsVersion() {
        return deviceOsVersion;
    }

    public void setDeviceOsVersion(String deviceOsVersion) {
        this.deviceOsVersion = deviceOsVersion;
    }

    public String getDevMake() {
        return devMake;
    }

    public void setDevMake(String devMake) {
        this.devMake = devMake;
    }

    public String getDevModel() {
        return devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
