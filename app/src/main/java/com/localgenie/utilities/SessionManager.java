package com.localgenie.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pojo.LanguagesList;

import javax.inject.Inject;

/**
 * <h>SessionManager</h>
 * Created by ${3Embed} on 6/11/17.
 */

public class SessionManager implements SessionManagerImpl{

    private static final String PREF_NAME = "ServiceGenie";
    // Constructor

    private final String SID = "SID";

    private final String AUTH = "TOKEN";
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    Context context;
    // Shared pref mode
    private final int PRIVATE_MODE = 0;
    private String countryCode;
    private static SessionManager helper;

    @Inject
    public SessionManager(Context context)
    {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public static SessionManager getInstance(Context context){
        if(helper==null){
            helper=new SessionManager(context);
        }
        return helper;
    }

    @Override
    public String getSID() {
        return pref.getString(SID,"");
    }
    @Override
    public void setSID(String sid) {
        editor.putString(SID,sid);
        editor.commit();
    }


    @Override
    public long getExpireOtp() {
        return pref.getLong("ExpireOtp",0);
    }
    @Override
    public void setExpireOtp(long expireOtp) {
        editor.putLong("ExpireOtp",expireOtp);
        editor.commit();
    }

    @Override
    public void setRegistrationId(String registrationId) {
        editor.putString("REGISTRATION",registrationId);
        editor.commit();
    }

    @Override
    public String getRegistrationId() {
        return pref.getString("REGISTRATION","");
    }

    @Override
    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    @Override
    public void setHomeScreenData(String Data) {
        editor.putString("HomeData",Data);
        editor.commit();
    }

    @Override
    public String getHomeScreenData() {
         return pref.getString("HomeData","");
    }

    @Override
    public String getAUTH() {
        return pref.getString(AUTH,"");
    }

    @Override
    public void setAUTH(String auth) {
        editor.putString(AUTH,auth);
        editor.commit();
    }

    public void setScheduleData(String data)
    {
        editor.putString("ScheduleData",data);
        editor.commit();
    }

    public String getScheduleData()
    {
        return pref.getString("ScheduleData","");
    }

    @Override
    public void setLatitude(String latitude)
    {
        editor.putString("latitude",latitude);
        editor.commit();
    }
    @Override
    public String getLatitude()
    {
        return pref.getString("latitude","");
    }
    /*******************************************************/
    @Override
    public void setLongitude(String longitude)
    {
        editor.putString("longitude",longitude);
        editor.commit();
    }
    @Override
    public String getLongitude()
    {
        return pref.getString("longitude","");
    }


    @Override
    public void setAddress(String address) {
        editor.putString("Address",address);
        editor.commit();
    }

    @Override
    public String getAddress() {
        return  pref.getString("Address","");
    }

    @Override
    public void setFirstName(String firstName) {

        editor.putString("FirstName",firstName);
        editor.commit();
    }

    @Override
    public String getFirstName() {
        return pref.getString("FirstName","");
    }

    @Override
    public void setLastName(String lastName) {
        editor.putString("LastName",lastName);
        editor.commit();
    }

    @Override
    public String getLastName() {
        return pref.getString("LastName","");
    }

    @Override
    public void setEmail(String email) {

        editor.putString("Email",email);
        editor.commit();
    }

    @Override
    public String getEmail() {
        return pref.getString("Email","");
    }

    @Override
    public void setMobileNo(String mobileNo) {

        editor.putString("MobileNo",mobileNo);
        editor.commit();
    }

    @Override
    public String getMobileNo() {
        return pref.getString("MobileNo","");
    }

    @Override
    public void setAbout(String about)
    {
        editor.putString("AboutMe",about);
        editor.commit();
    }

    @Override
    public String getAbout()
    {
        return pref.getString("AboutMe","");
    }

    @Override
    public void setRegisterId(String registerId)
    {
        editor.putString("RegisterId",registerId);
        editor.commit();
    }

    @Override
    public String getRegisterId() {
        return pref.getString("RegisterId","");
    }

    @Override
    public void setReferralCode(String refCode) {
        editor.putString("refRalCode",refCode);
        editor.commit();
    }

    @Override
    public String getReferralCode() {
        return pref.getString("refRalCode","");
    }

    @Override
    public void setGuestLogin(boolean guestLogin) {

        editor.putBoolean("guestLogin",guestLogin);
        editor.commit();
    }

    @Override
    public boolean getGuestLogin() {
        return pref.getBoolean("guestLogin",false);
    }

    @Override
    public void setProfileCalled(boolean isProfileCalled)
    {
        editor.putBoolean("IsProfileCalled",isProfileCalled);
        editor.commit();
    }

    @Override
    public boolean isProfileCalled() {
        return pref.getBoolean("IsProfileCalled",false);
    }


    @Override
    public String getProfilePicUrl() {
        return pref.getString("ProfilePic","");
    }

    @Override
    public void setProfilePicUrl(String profilePicUrl) {

        editor.putString("ProfilePic",profilePicUrl);
        editor.commit();
    }

    @Override
    public String getCountryCode() {
        return pref.getString("CountryCode","");
    }


    public void setCountryCode(String countryCode)
    {
        editor.putString("CountryCode",countryCode);
        editor.commit();
    }

    @Override
    public long getChatBookingID() {
        return pref.getLong("ChatBookingId",0);
    }

    @Override
    public void setChatBookingID(long bookingID) {

        editor.putLong("ChatBookingId",bookingID);
        editor.commit();
    }

    @Override
    public String getChatProId() {
        return pref.getString("ChatProId","");
    }

    @Override
    public void setChatProId(String chatProId)
    {
        editor.putString("ChatProId",chatProId);
        editor.commit();

    }

    @Override
    public String getProName() {
        return pref.getString("ChatProName","");
    }

    @Override
    public void setProName(String proName)
    {
        editor.putString("ChatProName",proName);
        editor.commit();
    }

    @Override
    public int getChatCount(long bookingId) {
        return pref.getInt("ChatCount"+bookingId, 0);
    }

    @Override
    public void setChatCount(long bookingId, int chatCount) {

        editor.putInt("ChatCount"+bookingId, chatCount);
        editor.commit();
    }

    @Override
    public void clearChatCountPreference(long bookingId) {

        editor.remove("ChatCount"+bookingId).commit();
    }

    @Override
    public long getLastTimeStampMsg()
    {

        return pref.getLong("LastTimeStamp", 0);
    }

    @Override
    public void setLastTimeStampMsg(long lastTimeStampMsg) {

        editor.putLong("LastTimeStamp", lastTimeStampMsg);
        editor.commit();
    }


    /*-----------------------Default Card Num----------------------------*/

    public String getDefaultCardName()
    {
        return pref.getString("defaultCardName", "");
    }

    public void setDefaultCardName(String defaultCardName) {
        editor.putString("defaultCardName", defaultCardName);
        editor.commit();
    }

    /*-----------------------Default Card Num----------------------------*/

    public String getDefaultCardNum()
    {
        return pref.getString("defaultCardNum", "");
    }

    public void setDefaultCardNum(String defaultCardNum) {
        editor.putString("defaultCardNum", defaultCardNum);
        editor.commit();
    }

    /*-----------------------Default Card Id----------------------------*/

    public String getDefaultCardId()
    {
        return pref.getString("defaultCardId", "");
    }

    public void setDefaultCardId(String defaultCardId) {
        editor.putString("defaultCardId", defaultCardId);
        editor.commit();
    }

    /*-----------------------Booking Status----------------------------*/

    public int getBookingStatus(long bid){
        return pref.getInt(""+bid, -1);
    }
    @Override
    public void setBookingStatus(long bookingId, int status) {

        editor.putInt(""+bookingId, status);
        editor.commit();
    }

    /*-----------------------Booking Status----------------------------*/

    @Override
    public String getFcmTopic() {
        return pref.getString("FcmTopic", "");
    }

    @Override
    public void setFcmTopic(String fcmTopic) {
        editor.putString("FcmTopic", fcmTopic);
        editor.commit();
    }

    @Override
    public String getFcmTopicCity() {
        return pref.getString("FcmTopicCity", "");
    }

    @Override
    public void setFcmTopicCity(String fcmTopicCity) {
        editor.putString("FcmTopicCity", fcmTopicCity);
        editor.commit();
    }

    @Override
    public String getFcmTopicAllCustomer() {
        return pref.getString("FcmTopicAllCustomer", "");
    }

    @Override
    public void setFcmTopicAllCustomer(String fcmTopicAllCustomer) {
        editor.putString("FcmTopicAllCustomer", fcmTopicAllCustomer);
        editor.commit();
    }

    @Override
    public String getFcmTopicAllCity() {
        return pref.getString("fcmTopicAllCity", "");
    }

    @Override
    public void setFcmTopicAllCity(String fcmTopicAllCity) {
        editor.putString("FcmTopicAllCity", fcmTopicAllCity);
        editor.commit();
    }

    @Override
    public String getContactName() {
        return pref.getString("ContactName", "");
    }

    @Override
    public void setContactName(String contactName) {
        editor.putString("ContactName", contactName);
        editor.commit();
    }

    @Override
    public String getContactPicUrl() {
        return pref.getString("ContactPicUrl", "");
    }

    @Override
    public void setContactPicUrl(String contactPicUrl) {
        editor.putString("ContactPicUrl", contactPicUrl);
        editor.commit();
    }

    @Override
    public LanguagesList getLanguageSettings()
    {
        String jsonString = pref.getString("LANGUAGE_SETTINGS", "");
        return new Gson().fromJson(jsonString, LanguagesList.class);
    }
    @Override
    public void setLanguageSettings(LanguagesList languageSettings)
    {
        if (languageSettings != null)
        {
            String jsonString = new Gson().toJson(languageSettings);
            editor.putString("LANGUAGE_SETTINGS", jsonString);
            editor.commit();
        }
        else
        {
            editor.putString("LANGUAGE_SETTINGS", "");
            editor.commit();
        }
    }

    @Override
    public String getIpAddress() {
        return pref.getString("IpAddress", "0");
    }

    @Override
    public void setIpAddress(String ipAddress) {
        editor.putString("IpAddress", ipAddress);
        editor.commit();
    }


    @Override
    public void setCallToken(String authToken) {
        editor.putString("AuthTokenCall", authToken);
        editor.commit();
    }

    @Override
    public String getCallToken() {
        return pref.getString("AuthTokenCall", "");
    }



    @Override
    public int getAppOpenTime() {
        return pref.getInt("AppOpenTime", 0);
    }

    @Override
    public void setAppOpenTime(int appOpenTime) {
        editor.putInt("AppOpenTime", appOpenTime);
        editor.commit();
    }

    @Override
    public void setDontShowRate(boolean dontOpenRate)
    {
        editor.putBoolean("DontOpenRate", dontOpenRate);
        editor.commit();
    }

    @Override
    public boolean getDontShowRate()
    {
        return pref.getBoolean("DontOpenRate", false);
    }

    @Override
    public boolean isAppOpen() {
        return pref.getBoolean("isAppOpen", false);
    }

    @Override
    public void setAppOpen(boolean isAppOpen) {
        editor.putBoolean("isAppOpen", isAppOpen);
        editor.commit();
    }
}
