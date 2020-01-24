package com.localgenie.utilities;

import static com.localgenie.utilities.Constants.KILOMETER;
import static com.localgenie.utilities.Constants.METER;
import static com.localgenie.utilities.Constants.NAUTICAL_MILES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.localgenie.IntroActivity.IntroActivity;
import com.localgenie.R;
import com.localgenie.countrypic.Country;
import com.localgenie.countrypic.CountryPicker;
import com.utility.MathUtil;
import com.utility.NetworkErrorActivity;
import com.utility.TimezoneMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

/**
 * <h>Utility</h>
 * Created by ${3Embed} on 6/10/17.
 */

public class Utility {

  /**
   * <h2>Get Device Id</h2>
   * <p>
   * method to retrieve the device id
   * Please call this in try catch block to handle gracefully
   * </p>
   *
   * @param context of the application such as activity/fragment
   */
  public static String getDeviceId(Context context) {
    String device_id;
    @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(
        context.getContentResolver(), Settings.Secure.ANDROID_ID);
    if (!android_id.equals("") && android_id != null) {
      device_id = android_id;
    } else {
      device_id = UUID.randomUUID().toString();
    }

    return device_id;
  }

  public static String dateInTwentyFour(long addedTime) {
    Calendar calendar = Calendar.getInstance();
    if (addedTime > 0) {
      calendar.setTimeInMillis(addedTime * 1000L);
    }
    calendar.setTimeZone(getTimeZone());
    Date date = calendar.getTime();
    SimpleDateFormat formated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    return formated.format(date);
  }

  public static TimeZone getTimeZone() {
    String timeZoneString = TimezoneMapper.latLngToTimezoneString(Constants.latitude,
        Constants.longitude);
    return TimeZone.getTimeZone(timeZoneString);
  }

  public static void copyStream(InputStream input, OutputStream output)
      throws IOException {

    byte[] buffer = new byte[2084];
    int bytesRead;
    while ((bytesRead = input.read(buffer)) != -1) {
      output.write(buffer, 0, bytesRead);
    }
  }

  /*get the color*/

  public static int getColor(Context mContext, int id) {


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return mContext.getColor(id);
    } else {
      return ContextCompat.getColor(mContext, id);
    }
  }

  public static void showKeyboard(final Context context, final View view) {
    view.requestFocus();
    view.postDelayed(() -> {
      InputMethodManager keyboard = (InputMethodManager) context.getSystemService(
          Context.INPUT_METHOD_SERVICE);
      keyboard.showSoftInput(view, 0);
    }, 200);
  }

  public static int getCountryMax(CountryPicker mCountryPicker, Context context) {

    Country country = mCountryPicker.getUserCountryInfo(context);
    String max_dig = country.getMax_digits();
    int max = 0;
    if (max_dig != null || !("".equals(max_dig.trim()))) {
      max = Integer.parseInt(max_dig);
    }
    return max;
  }

  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null &&
        activeNetwork.isConnectedOrConnecting();
  }

  public static boolean isLocationEnabled(Context context) {
    int locationMode = 0;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode = Settings.Secure.getInt(context.getContentResolver(),
            Settings.Secure.LOCATION_MODE);

      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
        return false;
      }

      return locationMode != Settings.Secure.LOCATION_MODE_OFF;

    } else {
      locationProviders = Settings.Secure.getString(context.getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }


  }

  /**
   * This method is used when our current Session got expired, so we instructs user to again do
   * Login.
   *
   * @param mcontext Context of the Activity or the Fragment
   * @param manager  sharedPreference
   */
  public static void setMAnagerWithBID(Context mcontext, SessionManagerImpl manager) {


    Intent intent = new Intent(mcontext, IntroActivity.class);

    Log.d("TAGUTILITY", "setMAnagerWithBID: " + manager.getFcmTopic());
    if (!"".equals(manager.getFcmTopic()) && manager.getFcmTopic() != null) {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(manager.getFcmTopic());
      // manager.setFcmTopic("");

    }
    if (!"".equals(manager.getFcmTopicCity()) && manager.getFcmTopicCity() != null) {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(manager.getFcmTopicCity());
      //  manager.setFcmTopicCity("");
    }

    if (!"".equals(manager.getFcmTopicAllCustomer()) && manager.getFcmTopicAllCustomer() != null) {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(manager.getFcmTopicAllCustomer());
      //  manager.setFcmTopicAllCustomer("");
    }

    if (!"".equals(manager.getFcmTopicAllCity()) && manager.getFcmTopicAllCity() != null) {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(manager.getFcmTopicAllCity());
      //  manager.setFcmTopicAllCity("");
    }
    manager.clearSession();
    SpUtil.getInstance().clearAll();
    int currentLanguage;
    Locale locale = mcontext.getResources().getConfiguration().locale;
    if (locale.getLanguage().equals("en")) {
      currentLanguage = 2;
    } else {
      currentLanguage = 1;
    }
    LocaleUtil.changeAppLanguage(mcontext, currentLanguage, true);

       /* if (manager.getLanguageSettings() == null)
            manager.setLanguageSettings(new LanguagesList("en","English", 0));
        else
            Utility.changeLanguageConfig(manager.getLanguageSettings().getCode(),mcontext);
*/
/*
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    mcontext.startActivity(intent);
    // VariableConstant.CALLMQTTDATA = 0;
    ((Activity) mcontext).finish();
*/

  }

  public static void setAmtOnRecept(double amt, TextView textView, String currencySybmol) {
    if (amt <= 0) {
      if (Constants.paymentAbbr == 1) {
        String timefee = currencySybmol + " 0.00";
        textView.setText(timefee);
      } else {
        String timefee = "0.00 " + currencySybmol;
        textView.setText(timefee);
      }

    } else {
      if (Constants.paymentAbbr == 1) {
        String timefee = currencySybmol + " " + doubleformate(amt);
        textView.setText(timefee);
      } else {
        String timefee = doubleformate(amt) + " " + currencySybmol;
        textView.setText(timefee);
      }

    }
  }

  public static void setAmtOnRecept(double amt, TextView textView, String currencySybmol,
      String text) {
    if (amt <= 0) {
      if (Constants.paymentAbbr == 1) {
        String timefee = text + "   (" + currencySybmol + " 0.00)";
        textView.setText(timefee);
      } else {
        String timefee = text + "   (0.00 " + currencySybmol + ")";
        textView.setText(timefee);
      }

    } else {
      if (Constants.paymentAbbr == 1) {
        String timefee = text + "   (" + currencySybmol + " " + doubleformate(amt) + ")";
        textView.setText(timefee);
      } else {
        String timefee = text + "   (" + doubleformate(amt) + " " + currencySybmol + ")";
        textView.setText(timefee);
      }

    }
  }

  public static String doubleformate(double amountvalue) {
    NumberFormat formatter = new DecimalFormat("#0.00");
    String datavalueIs;
    String datavalue = String.valueOf(amountvalue);
    if (datavalue.contains(",")) {
      String value = datavalue.replace(",", ".");
      datavalueIs = formatter.format(Double.parseDouble(value));
    } else {
      datavalueIs = formatter.format(amountvalue);
    }

    return datavalueIs;
  }

  public static int[] calculateTimeDifference(long reviewRate, long serverTime) {
    String currentDate = Utility.dateInTwentyFour(0);
    int[] duration = {0, 0, 0};
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
        Locale.getDefault());
    // Date rAppDate=null,
    Date curDate;

    try {
      //  rAppDate = simpleDateFormat.parse(reviewRate*1000L);
      curDate = simpleDateFormat.parse(currentDate);
      long timeDiffInMilli = curDate.getTime() - (reviewRate * 1000L);
      int seconds = (int) (timeDiffInMilli / 1000);
      duration[0] = Utility.getDurationString(seconds)[0];
      duration[1] = Utility.getDurationString(seconds)[1];
      duration[2] = Utility.getDurationString(seconds)[2];
      Log.d("TAG", "InvoiceAct calculateTimeDifference duration " + duration[0] + " min "
          + duration[1] + " sec " + duration[2]);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return duration;
  }

  /**
   * Custom method to convert consumed seconds into hh:mm:sec
   *
   * @param seconds: is the total amount of time consumed to reach to pickup adrs
   */
  private static int[] getDurationString(int seconds) {
    int[] duration = {0, 0, 0};
    int hours = seconds / 3600;
    int minutes = (seconds % 3600) / 60;
    seconds = seconds % 60;

    duration[0] = hours;

    duration[1] = minutes;

    duration[2] = seconds;

    return duration;
  }

  public static String getFormattedDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.setTimeZone(getTimeZone());
    //2nd of march 2015
    int day = cal.get(Calendar.DATE);

    if (!((day > 10) && (day < 19))) {
      switch (day % 10) {
        case 1:
          return simpleDateFormater("EEE d'st' MMM yyyy',' hh:mm a", date);
        case 2:

          return simpleDateFormater("EEE d'nd' MMM yyyy',' hh:mm a", date);
        case 3:
          return simpleDateFormater("EEE d'rd' MMM yyyy',' hh:mm a", date);
        default:
          return simpleDateFormater("EEE d'th' MMM yyyy',' hh:mm a", date);
      }
    }
    return simpleDateFormater("EEE d'th' MMM yyyy',' hh:mm a", date);
  }

  private static String simpleDateFormater(String format, Date date) {
    SimpleDateFormat sfd = new SimpleDateFormat(format, Locale.getDefault());
    sfd.setTimeZone(getTimeZone());
    return sfd.format(date);
  }


  @SuppressLint("PrivateResource")
  public static Bitmap setCreditCardLogo(String cardMethod, Context context) {
    Bitmap anImage;
    switch (cardMethod) {
      case "Visa":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_visa);
        break;
      case "MasterCard":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_mastercard);
        break;
      case "American Express":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_amex);
        break;
      case "Discover":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_discover);
        break;
      case "Diners Club":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_diners);
        break;

      case "JCB":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_jcb);
        break;

      case "Cash":
        anImage = getBitmapFromVectorDrawable(context, R.drawable.ic_cash_icon);
        break;
      case "Wallet":
        anImage = getBitmapFromVectorDrawable(context,
            R.drawable.ic_account_balance_wallet_black_24dp);
        break;

      default:
        anImage = getBitmapFromVectorDrawable(context, R.drawable.visa_card);
        break;
    }
    return anImage;
  }

  @SuppressLint("RestrictedApi")
  public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
    Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      drawable = (DrawableCompat.wrap(drawable)).mutate();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }

  public static long timeStamp(long expireTime, long serverTime) {

    Log.d("TAGTIME", " expireTime " + expireTime + " server " + serverTime);
    Date date = new Date(expireTime * 1000L);
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    String formattedDate = sdf.format(date);
    Log.d("TIME", "timeStamp: " + formattedDate + " TIMER " + (date.getTime()));
    Date date2 = new Date(serverTime * 1000L);
    String formattedDate2 = sdf.format(date2);
    Log.d("TIME", "timeStamp2: " + formattedDate2 + " TIMER2 " + (date2.getTime()));
    long diff = date.getTime() - date2.getTime();
    if (diff > 0) {
      int seconds = (int) (diff / 1000);
      int hours = seconds / 3600;
      int minutes = (seconds % 3600) / 60;
      seconds = seconds % 60;
      long totaltime = (minutes * 60 * 1000) + (seconds * 1000);
      Log.d("TAGTIME", "timeStamp: " + hours + " min " + minutes + " sec " + seconds);

      return diff;
    } else {
      return 0;
    }
  }


  public static boolean isAppIsInBackgroundOne(Context context) {
    boolean isInBackground = true;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activeProcess : processInfo.pkgList) {
            if (activeProcess.equals(context.getPackageName())) {
              isInBackground = false;
            }
          }
        }
      }
    } else {
      List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
      ComponentName componentInfo = taskInfo.get(0).topActivity;
      if (componentInfo.getPackageName().equals(context.getPackageName())) {
        isInBackground = false;
      }
    }

    return isInBackground;
  }


  /**
   * Method checks if the app is in background or not
   */
  public static boolean isAppIsInBackground(Context context) {
    boolean isInBackground = true;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activeProcess : processInfo.pkgList) {
            if (activeProcess.equals(context.getPackageName())) {
              isInBackground = false;
            }
          }
        }
      }
    } else {

      if (isTopActivity(context, context.getPackageName())) {
        isInBackground = false;
      }
    }

    return isInBackground;
  }

  /**
   * TODO 根据包名判断是否在最前面显示
   *
   * @param context {@link Context}
   * @return boolean
   * @author Melvin
   * @date 2013-4-23
   */
  private static boolean isTopActivity(Context context, String packageName) {
    if (context == null) {
      return false;
    }//|| isNull(packageName)
    int id = context.checkCallingOrSelfPermission(Manifest.permission.GET_TASKS);
    if (PackageManager.PERMISSION_GRANTED != id) {
      return false;
    }

    ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
    if (tasksInfo.size() > 0) {
      return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
    }
    return false;
  }


  public static void hideKeyboard(Activity mcontext) {
    try {
      InputMethodManager inputManager = (InputMethodManager) mcontext.getSystemService(
          Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(mcontext.getCurrentFocus().getWindowToken(),
          InputMethodManager.RESULT_UNCHANGED_SHOWN);
    } catch (NullPointerException e) {

    }

  }

  public static void showKeyBoard(Activity mContext) {
    try {
      InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
          Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    } catch (Exception e) {

    }
  }

  public static boolean containsLocation(double latitude, double longitude, List<LatLng> polygon,
      boolean geodesic) {
    int size = polygon.size();
    if (size == 0) {
      return false;
    } else {
      double lat3 = Math.toRadians(latitude);
      double lng3 = Math.toRadians(longitude);
      LatLng prev = polygon.get(size - 1);
      double lat1 = Math.toRadians(prev.latitude);
      double lng1 = Math.toRadians(prev.longitude);
      int nIntersect = 0;

      double lng2;
      for (Iterator var17 = polygon.iterator(); var17.hasNext(); lng1 = lng2) {
        LatLng point2 = (LatLng) var17.next();
        double dLng3 = MathUtil.wrap(lng3 - lng1, -3.141592653589793D, 3.141592653589793D);
        if (lat3 == lat1 && dLng3 == 0.0D) {
          return true;
        }

        double lat2 = Math.toRadians(point2.latitude);
        lng2 = Math.toRadians(point2.longitude);
        if (intersects(lat1, lat2,
            MathUtil.wrap(lng2 - lng1, -3.141592653589793D, 3.141592653589793D), lat3, dLng3,
            geodesic)) {
          ++nIntersect;
        }

        lat1 = lat2;
      }

      return (nIntersect & 1) != 0;
    }
  }

  private static boolean intersects(double lat1, double lat2, double lng2, double lat3, double lng3,
      boolean geodesic) {
    if ((lng3 < 0.0D || lng3 < lng2) && (lng3 >= 0.0D || lng3 >= lng2)) {
      if (lat3 <= -1.5707963267948966D) {
        return false;
      } else if (lat1 > -1.5707963267948966D && lat2 > -1.5707963267948966D
          && lat1 < 1.5707963267948966D && lat2 < 1.5707963267948966D) {
        if (lng2 <= -3.141592653589793D) {
          return false;
        } else {
          double linearLat = (lat1 * (lng2 - lng3) + lat2 * lng3) / lng2;
          return !(lat1 >= 0.0D && lat2 >= 0.0D && lat3 < linearLat) && (
              lat1 <= 0.0D && lat2 <= 0.0D && lat3 >= linearLat || (lat3 >= 1.5707963267948966D || (
                  geodesic ? Math.tan(lat3) >= tanLatGC(lat1, lat2, lng2, lng3) : MathUtil.mercator(
                      lat3) >= mercatorLatRhumb(lat1, lat2, lng2, lng3))));
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  private static double tanLatGC(double lat1, double lat2, double lng2, double lng3) {
    return (Math.tan(lat1) * Math.sin(lng2 - lng3) + Math.tan(lat2) * Math.sin(lng3)) / Math.sin(
        lng2);
  }

  private static double mercatorLatRhumb(double lat1, double lat2, double lng2, double lng3) {
    return (MathUtil.mercator(lat1) * (lng2 - lng3) + MathUtil.mercator(lat2) * lng3) / lng2;
  }

  public static RequestOptions createGlideOption(Context mContext) {
    return new RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.register_profile_default_image)
        .error(R.drawable.register_profile_default_image)
        .transform(new CircleTransform(mContext))
        .priority(Priority.HIGH);

  }

  public static RequestOptions createGlideOptionCall(Context mContext) {
    return new RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.register_profile_default_image)
        .error(R.drawable.register_profile_default_image)
        .transform(new CircleTransform(mContext))
        .priority(Priority.HIGH);

  }

  public static void checkAndShowNetworkError(Context context) {
    if (!isNetworkAvailable(context) && !Constants.IS_NETWORK_ERROR_SHOWED) {
      Intent intent = new Intent(context, NetworkErrorActivity.class);
      context.startActivity(intent);
    }
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
  }

  public static void statusbar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = activity.getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      int blueColorValue = Color.parseColor("#FFFFFFFF");//#2598ED
      window.setStatusBarColor(blueColorValue);
    }
  }

  /**
   * <h2>changeLanguageConfig</h2>
   * used to set the language configuration
   *
   * @param code language code
   */
  public static int changeLanguageConfig(String code, Context context) {
    Configuration configuration = context.getResources().getConfiguration();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      configuration.setLayoutDirection(new Locale(code));
      Log.d("TAG", " language direction " + configuration.getLayoutDirection());
    }
    configuration.locale = new Locale(code);
    context.getResources().updateConfiguration(configuration,
        context.getResources().getDisplayMetrics());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return configuration.getLayoutDirection();
    }
    return 0;
  }

  public static List<Date> getDatesBetweenUsingJava7(
      Date startDate, Date endDate) {
    List<Date> datesInRange = new ArrayList<>();
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(startDate);

    Calendar endCalendar = new GregorianCalendar();
    endCalendar.setTime(endDate);

    while (calendar.before(endCalendar)) {
      Date result = calendar.getTime();
      datesInRange.add(result);
      calendar.add(Calendar.DATE, 1);
    }
    return datesInRange;
  }


  public static String dayOfTheWeek(int day) {
    String days;
    switch (day) {
      case 1:
        days = "Sunday";
        break;
      case 2:
        days = "Monday";
        break;
      case 3:
        days = "Tuesday";
        break;
      case 4:
        days = "Wednesday";
        break;
      case 5:
        days = "Thursday";
        break;
      case 6:
        days = "Friday";
        break;
      default:
        days = "Saturday";
        break;


    }
    return days;
  }

  public static double distance(double lat1, double lng1, double lat2, double lng2, String unit) {
    double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double sindLat = Math.sin(dLat / 2);
    double sindLng = Math.sin(dLng / 2);
    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1))
        * Math.cos(Math.toRadians(lat2));
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = earthRadius * c;
    if (KILOMETER.equals(unit))
// Kilometer
    {
      dist = dist * 1.609344;
    } else if (NAUTICAL_MILES.equals(unit))
// Nautical Miles
    {
      dist = dist * 0.8684;
    } else if (METER.equals(unit))
// meter
    {
      dist = dist * 1609.344;
    }
    return dist;
  }

  public static View getCustomeBackgroundTextProfilePic(LayoutInflater inflater, String firstLetter,
      String secondLetter) {
    View view = inflater.inflate(R.layout.custom_background_text_profile_pic_100, null);
    TextView tvImageName = view.findViewById(R.id.tvImageName);
    String name = "";
    try {
      if (firstLetter != null) {
        name = firstLetter.substring(0, 1);
        if (secondLetter != null && !secondLetter.equals("")) {
          name = name + secondLetter.substring(0, 1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    tvImageName.setText(name);
    return view;
  }

  public static Bitmap createDrawableFromView(Context context, View view) {

    DisplayMetrics displayMetrics = new DisplayMetrics();
    if (context != null) {
      if (context.getApplicationContext() != null) {
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      }
    }
    view.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
        ActionBar.LayoutParams.WRAP_CONTENT));
    view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
    view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
    view.buildDrawingCache();
    Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
        Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(bitmap);
    view.draw(canvas);

    return bitmap;
  }


  public static void saveImage(Bitmap finalBitmap, File file) {
    Random generator = new Random();
    int n = 10000;
    n = generator.nextInt(n)
    ;
    if (file.exists()) {
      file.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * method for conveting utctime to custom time format
   *
   * @param time utc time
   * @return custom time format
   */
  public static String convertUTCToServerFormat(String time) {
    long timestamp = Long.parseLong(time) * 1000;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    Date currenTimeZone = cal.getTime();
    return sdf.format(currenTimeZone);
  }

  /**
   * method for conveting utctime to custom time format
   *
   * @param time utc time
   * @return custom time format
   */
  public static String convertUTCToServerFormat(String time, String format) {
    long timestamp = Long.parseLong(time) * 1000;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    Date currenTimeZone = cal.getTime();
    return sdf.format(currenTimeZone);
  }


  /**
   * method for conveting utctime to custom time format
   *
   * @param time utc time
   * @return custom time format
   */
  public static String convertUTCToDateFormat(String time) {
    long timestamp = Long.parseLong(time) * 1000;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    Calendar cal = Calendar.getInstance(Locale.getDefault());
    cal.setTimeInMillis(timestamp);
    //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    Date currenTimeZone = cal.getTime();
    return sdf.format(currenTimeZone);
  }

  public static String convertUTCToTodayDateFormat(String time) {
    long timestamp = Long.parseLong(time);
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    Calendar cal = Calendar.getInstance(Locale.getDefault());
    cal.setTimeInMillis(timestamp);
    //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    Date currenTimeZone = cal.getTime();
    return sdf.format(currenTimeZone);
  }


  /**
   * method for conveting utctime to custom time format
   *
   * @param time utc time
   * @return custom time format
   */
  public static String convertUTCToYesterdayDateFormat(String time) {
    long timestamp = Long.parseLong(time);
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    sdf.setTimeZone(getTimeZone());
    Calendar cal = Calendar.getInstance(Locale.getDefault());
    cal.setTimeInMillis(timestamp);
    //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    cal.add(Calendar.DATE, -1);
    Date currenTimeZone = cal.getTime();
    return sdf.format(currenTimeZone);
  }


  /**
   * method for converting utc time to timeStamp
   *
   * @param time utc time
   * @return timestamp
   */
  public static long convertUTCToTimeStamp(String time) {

    try {
      long timestamp = Long.parseLong(time) * 1000;
      Calendar cal = Calendar.getInstance(Locale.getDefault());
      cal.setTimeInMillis(timestamp);
      //cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
      cal.setTimeZone(getTimeZone());

      return cal.getTimeInMillis();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

}
