package com.localgenie.utilities;

import com.pojo.LanguagesList;

/**
 * <h>SessionManagerImpl</h>
 * Created by Ali on 1/30/2018.
 */

public interface SessionManagerImpl
{

    void clearSession();

    void setSID(String sid);
    String getSID();

    void setRegistrationId(String registrationId);
    String getRegistrationId();

    void setAUTH(String auth);
    String getAUTH();

    void setLatitude(String latitude);
    String getLatitude();

    void setLongitude(String longitude);
    String getLongitude();

    void setAddress(String address);
    String getAddress();

    void setHomeScreenData(String Data);
    String getHomeScreenData();

    void setFirstName(String firstName);
    String getFirstName();

    void setLastName(String lastName);
    String getLastName();

    void setEmail(String email);
    String getEmail();

    void setMobileNo(String mobileNo);
    String getMobileNo();

    void setAbout(String about);
    String getAbout();

    void setRegisterId(String registerId);
    String getRegisterId();

    void setReferralCode(String refCode);
    String getReferralCode();

    void setGuestLogin(boolean guestLogin);
    boolean getGuestLogin();

    void setProfileCalled(boolean isProfileCalled);
    boolean isProfileCalled();

    String getProfilePicUrl();
    void setProfilePicUrl(String profilePicUrl);

    String getCountryCode();
    void setCountryCode(String countryCode);

    long getChatBookingID();
    void setChatBookingID(long bookingID);

    String getChatProId();
    void setChatProId(String chatProId);

    String getProName();
    void setProName(String proName);

    int getBookingStatus(long bookingId);
    void setBookingStatus(long bookingId, int status);

    int getChatCount(long bookingId);
    void setChatCount(long bookingId, int chatCount);

    void clearChatCountPreference(long bookingId);

    long getLastTimeStampMsg();
    void setLastTimeStampMsg(long lastTimeStampMsg);

    String getDefaultCardName();
    void setDefaultCardName(String defaultCardName);

    String getIpAddress();
    void setIpAddress(String ipAddress);

    String getDefaultCardNum();
    void setDefaultCardNum(String defaultCardNum);

    String getDefaultCardId();
    void setDefaultCardId(String defaultCardId);

    String getFcmTopic();
    void setFcmTopic(String fcmTopic);

    String getFcmTopicCity();
    void setFcmTopicCity(String fcmTopicCity);

    String getFcmTopicAllCustomer();
    void setFcmTopicAllCustomer(String fcmTopicAllCustomer);

    String getFcmTopicAllCity();
    void setFcmTopicAllCity(String fcmTopicAllCity);


    String getContactName();
    void setContactName(String contactName);

    String getContactPicUrl();
    void setContactPicUrl(String contactPicUrl);

    LanguagesList getLanguageSettings();
    void setLanguageSettings(LanguagesList languageSettings);

    int getAppOpenTime();
    void setAppOpenTime(int AppOpenTime);

    boolean getDontShowRate();
    void setDontShowRate(boolean dontOpenRate);

    boolean isAppOpen();
    void setAppOpen(boolean isAppOpen);


    void setCallToken(String authToken);
    String getCallToken();

    long getExpireOtp();
    void setExpireOtp(long expireOtp);
}

