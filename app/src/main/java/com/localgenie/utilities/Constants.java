package com.localgenie.utilities;

import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.localgenie.BuildConfig;
import com.localgenie.model.Offers;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * <h>Constants</h>
 * Created by ${3Embed} on 6/10/17.
 */

public class Constants {

 //9865986532
 public static final String PARENT_FOLDER = "lsp" ;
 // public static final String BASE_URL = "http://45.77.190.140:9999/" ;
 // public static final String BASE_URL = "https://api.localgenie.online/" ;
 //public static final String BASE_URL = "https://api.service-genie.xyz/" ;

 //public static final String BASE_URL = "https://api.service-genie.xyz/" ;
 public static final String GEO_LOC_URL = "http://www.geoplugin.net/" ;
 public static final int WALLETCALL = 15;
 public static  boolean IS_CHATTING_OPENED = false;
 public static boolean IS_CHATTING_RESUMED = false;
 public static String currencySymbol="$";
 public static String bookingcurrencySymbol ="$";

 public static final String MQTT_URL_HOST = "tcp://45.77.190.140";
 public static final String MQTT_PORT = "1883";
 public static final String BASE_URL = "https://api.service-genie.xyz";
/* public static final String MQTT_URL_HOST = "tcp://206.189.39.225";
 public static final String MQTT_PORT = "2052";*/
 public static final String MQTT_TOPIC = "provider/";

// public static final String CHAT_URL = "https://dev-call.service-genie.xyz/";
 //public static final String CHAT_URL = "http://45.77.190.140:5011/";
 //public static final String CHAT_URL = "https://call.service-genie.xyz/";
/* public static final String MQTT_URL_HOST = "tcp://45.77.190.140";
 public static final String MQTT_PORT = "1883";*/

 /*public static final String BASE_URL = "https://dev-api.service-genie.xyz/" ;
 public static final String MQTT_URL_HOST = "tcp://206.189.39.225";
 public static final String MQTT_PORT = "2052";*/
 //public static final String MQTT_TOPIC = "provider/";

 //public static final String CHAT_URL = "https://dev-call.service-genie.xyz";
 //public static final String CHAT_URL = "http://45.77.190.140:5011/";
 public static final String CHAT_URL = "https://call.service-genie.xyz";

 public static final String CHAT_DATA_UPLOAD = "http://45.77.190.140:8009/";/*"http://45.77.190.140:8009/";*/

 //public static final String SERVER_KEY = "AIzaSyAbz187ZVtHiI6rK_bjtm1ILKa8gIqpMT8";
 public static final String SERVER_KEY = "AIzaSyAbz187ZVtHiI6rK_bjtm1ILKa8gIqpMT8";
 public static final String Amazonbucket = "appscrip";
 public static final String Amazoncognitoid = "us-east-1:9a14c311-0b08-4a80-a725-157af6547833";
 public static final String AmazonProfileFolderName = "iserve2.0/ProfileImages";
 public static final String AmazonUploadJobImage = "iserve2.0/UploadJobImage";

 public static final int GALLERY_PIC = 10;
 public static final int CAMERA_PIC = 11;
 public static final int CROP_IMAGE = 12;

 public static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{7,20}");

 public static final String APP_VERSION = BuildConfig.VERSION_NAME;
 public static final String DEVICE_MODEL = Build.MODEL;
 public static final String DEVICE_MAKER = Build.MANUFACTURER;
 public static final int DEVICE_TYPE = 2; //1-iOS , 2-android, 3-web
 public static final String DEVICE_OS_VERSION = Build.VERSION.RELEASE;

 public static final String MAPS_SERVER_KEY = "AIzaSyDLJri6fY1D7lM2dJol-VkuL8jRaMf66ss";

 public static String customerPushTopic;
 public static String stripeKeys;

 public static double latitude = 0;
 public static double longitude = 0;

 public static double proLatitude = 0;
 public static double proLongitude = 0;
 public static String proAddress = "";
 public static double currentLat = 0;
 public static double currentLng = 0;


 public static LatLng custLatLng ;

 public static int customerHomePageInterval = 5;
 public static String selLang = "en";

 public static String mqttErrorMsg = "";

 public static double lat = 0.0;
 public static double lng = 0.0;
 public static double minPrice = 0.0;
 public static double maxPrice = 0.0;
 public static double distance = 30;

 /**
  *<h>bookingType</h>
  * 1 now booking
  * 2 late booking
  * 3 repeat
  */
 public static int bookingType = 1;

 /**
  * <H1>serviceType</H1>
  * service_type = 1 onDemand (ex Taxi booking) not price
  * service_type = 2 MarketPlace(ex Music) edit price
  * service_type = 3 Bidding
  */
 public static int serviceType = 0;

 /**
  * <H1>bookingModel</H1>
  * billing_model
  * 1 - fixed not edit
  * 2 - hourly + fixed not edit
  * 3 - hourly not edit
  *
  * 4 - hourly edit
  * 5 - fixed edit
  * 6 - hourly + fixed edit
  * 7 - bidding
  */
 public static int bookingModel = 0;
 public static String catId = "";
 public static String catName = "";
 public static String subCatId = "";
 public static int bookingTypeNowSchedule = 0;
 public static int bookingOffer = 0;
 public static int offerType = 0;
 public static String offerName = "";
 public static ArrayList<Offers> offers=null;
 public static String proId = "0";
 public static int scheduledTime = 0;
 public static long onRepeatEnd = 0;
 public static String scheduledDate = "";
 public static int callType = 0; //1 in call 2 out call
 public static int bookingModelJobDetails; // 1 on-Demand 2 market place, 3 bidding
 public static ArrayList<String> onRepeatDays = new ArrayList<>();

 public static String distanceUnit = "";

 public static final int FILTER_RESULT_CODE = 6;
 public static final int ADDRESS_RESULT_CODE = 14;
 public static final int PAYMENT_RESULT_CODE = 151;
 public static final int REPEAT_RESULT_CODE = 152;
 public static final int AMOUNT_RESULT_CODE = 153;
 public static final int TIME_RESULT_CODE = 154;

 public static final int SUCCESS_RESPONSE = 200;
 public static final int SESSION_LOGOUT = 498;
 public static final int SESSION_EXPIRED = 440;
 public static final int SESSION_NoDues = 410;


 public static boolean isConfirmBook = false;

 public static String LiveTrackBookingPid = "";
 public static boolean IS_NETWORK_ERROR_SHOWED = false;
 public static boolean isHomeFragment = false;
 public static boolean isJobDetailsOpen = false;
 public static int jobType = 2;
 public static long fromTime = 0;
 public static long toTime = 0;
 public static boolean isMenuActivityCalled = false;

 public static String filteredAddress = "";
 public static double filteredLat = 0;
 public static double filteredLng = 0;
 public static double visitFee = 0;
 public static double pricePerHour = 0;
 public static int serviceSelected = 2;

 public static int paymentAbbr = 1;

 public static boolean isHelpIndexOpen = false;
 public static long serverTime = 0;
 public static long diffServerTime = 0;

 public static String walletCurrency = "$";
 public static double walletAmount = 0;
 public static boolean isLoggedIn = true;
 public static final String TERMS_LINK = "https://admin.service-genie.xyz/supportText/customer/en_termsAndConditions.php";
 public static final String PRIVECY_LINK = "https://admin.service-genie.xyz/supportText/customer/en_privacyPolicy.php";

 public static String selectedDate = "";
 public static String selectedDuration = "";


 /*******************************RepeatText Area **************************************/

 public static String repeatStartTime = "";
 public static String repeatStartDate = "";
 public static String repeatEndTime = "";
 public static String repeatEndDate = "";
 public static int repeatNumOfShift = 0;
 public static ArrayList<String> repeatDays = new ArrayList<>();
 public static ArrayList<String> GOOGLEKEY = new ArrayList<>();

 public static final String KILOMETER = "Kilometers";
 public static final String METER = "meters";
 public static final String NAUTICAL_MILES = "nauticalMiles";
 public static final boolean isInComingCalls = false;
 public static JSONArray jsonArray = new JSONArray();

 public static String userUids = "";
 public static String userNames = "";
 public static String userIdentifiers = "";
 public static String userImageUrls = "";
 public static final String SIGNATURE_PIC_DIR = "Signature";
 public static final String SIGNATURE_UPLOAD = "SignatureUpload";
 public static final String AMAZON_BASE_URL ="https://s3-ap-southeast-1.amazonaws.com/";
 /*"https://s3.amazonaws.com/"*/;


 /*Call Module */

 public static void updateUserDetails(String userUid, String userName, String userIdentifier,
                                      String userImageUrl)
 {
  userUids =userUid;
  userNames =userName;
  userIdentifiers =userIdentifier;
  userImageUrls =userImageUrl;
 }



}
