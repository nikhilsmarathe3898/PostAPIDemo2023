package com.localgenie.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import com.localgenie.IntroActivity.IntroActivity;
import com.localgenie.home.MainActivity;
import java.util.Locale;

public class LocaleUtil {

  public static final String LAN_SPANISH = "hi";
  public static final String LAN_PORTUGUESE = "pt";
  public static final String LAN_ENGLISH = "en";
  public static final String LAN_ARABIC = "ar";

  /**
   * Get user-set Locale
   *
   * @return Locale
   */
  public static Locale getUserLocale() {
    int currentLanguage = SpUtil.getInstance().getInt("currentLanguage", 0);
    Locale myLocale = new Locale(LAN_ENGLISH);
    Constants.selLang = LAN_ENGLISH;
    switch (currentLanguage) {
      case 0:
        myLocale = new Locale(LAN_ARABIC);
        Constants.selLang = LAN_ARABIC;
        break;
      case 1:
        myLocale = new Locale(LAN_SPANISH);
        Constants.selLang = LAN_SPANISH;
        break;
      case 2:
        myLocale = new Locale(LAN_ENGLISH);
        Constants.selLang = LAN_ENGLISH;
        break;
    }
    return myLocale;
  }

  /**
   * Set the language: if there is a setting before, follow the setting; if not, follow the system
   * language
   */
  public static void changeAppLanguage(Context context) {
    if (context == null) return;
    Context appContext = context.getApplicationContext();

    int currentLanguage = 2;
    switch (appContext.getResources().getConfiguration().locale.getLanguage()) {
      case LAN_ARABIC:
        currentLanguage = 0;
        break;
      case LAN_SPANISH:
        currentLanguage = 1;
        break;
      case LAN_ENGLISH:
        currentLanguage = 2;
        break;
    }

    SpUtil.getInstance().save("currentLanguage", currentLanguage);

    int tLanguage = SpUtil.getInstance().getInt("currentLanguage", -1);
    Locale myLocale;
    // 0 Arabic 1 Espanol 2 English
    switch (tLanguage) {
      case 0:
        myLocale = new Locale(LAN_ARABIC);
        Constants.selLang = LAN_ARABIC;
        break;
      case 1:
        myLocale = new Locale(LAN_SPANISH);
        Constants.selLang = LAN_SPANISH;
        break;
      case 2:
        myLocale = new Locale(LAN_ENGLISH);
        Constants.selLang = LAN_ENGLISH;
        break;
      default:
        myLocale = appContext.getResources().getConfiguration().locale;
        if (myLocale != null) {
          Constants.selLang = myLocale.getLanguage();
        }
    }
    // National language settings
    if (needUpdateLocale(appContext, myLocale)) {
      updateLocale(appContext, myLocale);
    }
  }

  /**
   * Language for saving settings
   *
   * @param currentLanguage index
   */
  public static void changeAppLanguage(Context context, int currentLanguage,boolean isFromIntro) {
    if (context == null) return;
    Context appContext = context.getApplicationContext();
    SpUtil.getInstance().save("currentLanguage", currentLanguage);
    Locale myLocale = new Locale(LAN_ENGLISH);
    Constants.selLang = LAN_ENGLISH;
    // 0 Arabic 1 Espanol 2 English
    switch (currentLanguage) {
      case 0:
        myLocale = new Locale(LAN_ARABIC);
        Constants.selLang = LAN_ARABIC;
        break;
      case 1:
        myLocale = new Locale(LAN_SPANISH);
        Constants.selLang = LAN_SPANISH;
        break;
      case 2:
        myLocale = new Locale(LAN_ENGLISH);
        Constants.selLang = LAN_ENGLISH;

        break;
    }
    // National language settings
    if (needUpdateLocale(appContext, myLocale)) {
      updateLocale(appContext, myLocale);
    }

/*
    Toast.makeText(appContext, appContext.getString(R.string.set_success),
        Toast.LENGTH_SHORT).show();
*/
    restartApp(appContext,isFromIntro);
  }

  /**
   * Restart the app to take effect
   */
  public static void restartApp(Context context, boolean isFromIntro) {

    if(isFromIntro)
    {
      Intent intent = new Intent(context, IntroActivity.class);
      intent.setAction(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    }else {
      Intent intent = new Intent(context, MainActivity.class);
      intent.setAction(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    }
  }

  /**
   * Get the current Locale
   *
   * @param context Context
   * @return Locale
   */
  public static Locale getCurrentLocale(Context context) {
    Locale locale;
    if (Build.VERSION.SDK_INT
        >= Build.VERSION_CODES.N) { //7.0There are multiple language settings to get the top
      // language
      locale = context.getResources().getConfiguration().getLocales().get(0);
    } else {
      locale = context.getResources().getConfiguration().locale;
    }
    Log.d("locale", "getCurrentLocale: "+locale);
    return locale;
  }

  /**
   * Update Locale
   *
   * @param context Context
   * @param locale  New User Locale
   */
  public static void updateLocale(Context context, Locale locale) {
    if (needUpdateLocale(context, locale)) {
      Configuration configuration = context.getResources().getConfiguration();
      if (Build.VERSION.SDK_INT >= 19) {
        configuration.setLocale(locale);
      } else {
        configuration.locale = locale;
      }
      DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
      context.getResources().updateConfiguration(configuration, displayMetrics);
    }
  }

  /**
   * Judge if update is needed
   *
   * @param context Context
   * @param locale  New User Locale
   * @return true / false
   */
  public static boolean needUpdateLocale(Context context, Locale locale) {
    return locale != null && !getCurrentLocale(context).equals(locale);
  }

  /**
   * When the system language changes, it will continue to follow the language set by the user
   */
  public static void setLanguage(Context context, Configuration newConfig) {
    if (context == null) return;
    Context appContext = context.getApplicationContext();
    int currentLanguage = 2;
    switch (newConfig.locale.getLanguage()) {
      case LAN_ARABIC:
        currentLanguage = 0;
        break;
      case LAN_SPANISH:
        currentLanguage = 1;
        break;
      case LAN_ENGLISH:
        currentLanguage = 2;
        break;
    }

    SpUtil.getInstance().save("currentLanguage", currentLanguage);
  // int Language = SpUtil.getInstance().getInt("currentLanguage", -1);
    Locale locale  = new Locale(newConfig.locale.getLanguage());

    if (newConfig.locale.getLanguage() != null) {
      Constants.selLang = newConfig.locale.getLanguage();
    }
    /*Locale locale = appContext.getResources().getConfiguration().locale;
    Log.d("SEtLAng", "setLanguage: "+newConfig.locale.getLanguage().toString());*/
    // 0 Arabic 1 Espanol 2 English
/*
    switch (Language) {
      case 0:
        locale = new Locale(LAN_ARABIC);
        Constants.selLang = LAN_ARABIC;
        break;
      case 1:
        locale = new Locale(LAN_SPANISH);
        Constants.selLang = LAN_SPANISH;
        break;
      case 2:
        locale = new Locale(LAN_ENGLISH);
        Constants.selLang = LAN_ENGLISH;
        break;
      default:
       // locale = appContext.getResources().getConfiguration().locale;
        if (newConfig.locale.getLanguage() != null) {
          Constants.selLang = newConfig.locale.getLanguage();
        }
    }
*/
    // The system language changes the language set before the app keeps
   /* Constants.selLang = locale.getLanguage();
    Locale.setDefault(locale);
    Configuration configuration = new Configuration(newConfig);
    if (Build.VERSION.SDK_INT >= 19) {
      configuration.setLocale(locale);
    } else {
      configuration.locale = locale;
    }
    appContext.getResources().updateConfiguration(configuration,
        appContext.getResources().getDisplayMetrics());*/

    // National language settings
    if (needUpdateLocale(appContext, locale)) {
      updateLocale(appContext, locale);
    }

  }

}
