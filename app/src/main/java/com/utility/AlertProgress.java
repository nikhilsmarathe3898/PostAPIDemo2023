package com.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.localgenie.BuildConfig;
import com.localgenie.IntroActivity.IntroActivityContract;
import com.localgenie.R;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.pojo.LanguageResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * <h>AlertProgress</h>
 * Created by Ali on 8/5/2017.
 */

public class AlertProgress
{
    private static final String TAG = "AlertProgress";
    private Context mcontect;
    private static AlertDialog alertDialogs = null;
    private int currentLanguage = -1;
    public AlertProgress(Context mcontect)
    {
        this.mcontect  =mcontect;
    }
    public AlertDialog getProgressDialog(Context mContext,String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
        tv_progress.setText(message);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        /*alertDialog = dialogBuilder.create();
        alertDialog.show();*/
        return dialogBuilder.create();
    }

    /************************************************    for checking internet connection*******/
    public boolean isNetworkAvailable(Context mcontect) {

        ConnectivityManager connectivity;
        boolean isNetworkAvail = false;
        try {
            connectivity = (ConnectivityManager)mcontect.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null) { // connected to the internet
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        isNetworkAvail = true;
                        return isNetworkAvail;
                    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        isNetworkAvail = true;
                        return isNetworkAvail;
                    }
                } else {
                    // not connected to the internet
                    isNetworkAvail = false;
                    return isNetworkAvail;
                }

            }
            else
            {
                isNetworkAvail = false;
                return isNetworkAvail;
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return isNetworkAvail;
    }





    public void  showNetworkAlert(final Context mcontect)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mcontect);

        // Setting Dialog Title
        alertDialog.setTitle(mcontect.getResources().getString(R.string.network_alert_title));
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage(mcontect.getResources().getString(R.string.network_alert_message));

        // On pressing Settings button
        alertDialog.setPositiveButton(mcontect.getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                mcontect.startActivity(intent);
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void alertinfo(Context mContext,String message)
    {
        try
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            AlertDialog dialog = alertDialog.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            // Setting Dialog Title
            alertDialog.setTitle(mContext.getResources().getString(R.string.system_error));
            alertDialog.setCancelable(false);
            // Setting Dialog Message
            alertDialog.setMessage(message);
            // On pressing Settings button
            alertDialog.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void alertPositiveOnclick(Context mContext, String message, String title,String positiveText, final DialogInterfaceListner isclick)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveText, (dialog1, which) -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog.cancel();
            }
           /* dialog1.dismiss();
            dialog1.cancel();*/
            isclick.dialogClick(true);
        });
        builder.show();
    }

    public void alertPositiveNegativeOnclick(Context mContext, String message, String title, String positiveClick, String negativeClick, boolean isWalletCalled, final DialogInterfaceListner isclick)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        builder.setTitle(title);
        builder.setMessage(message);
        if(isWalletCalled)
            builder.setCancelable(true);
        else
            builder.setCancelable(false);
        builder.setPositiveButton(positiveClick, (dialog1, which) -> {
            dialog1.dismiss();
            dialog1.cancel();
            isclick.dialogClick(true);
        });


        builder.setNegativeButton(negativeClick, (dialog12, i) -> {
            dialog12.dismiss();
            dialog12.cancel();
            isclick.dialogClick(false);
        });
        builder.show();
    }

    public void tryAgain(Context mContext, String message, String title, final DialogInterfaceListner isclick)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getResources().getString(R.string.try_again), (dialog1, which) -> {
            dialog1.dismiss();
            dialog1.cancel();
            isclick.dialogClick(true);
        });
        if(!((Activity)mContext).isFinishing()) {
            builder.show();
        }
    }

    /************************************get Ip Address****************************************/

    private IpAddressInterface ipAddressInterface;


    public void IPAddress(IpAddressInterface ipAddressInterface) {
        this.ipAddressInterface = ipAddressInterface;
        new IPAddres().execute();
    }



private class IPAddres  extends AsyncTask<Void,Void,String> {
    @Override
    protected String doInBackground(Void... voids)
    {
        // String stringUrl = "https://ipinfo.io/ip";
        String stringUrl = "https://ipinfo.io/json";
        // String stringUrl = "https://freegeoip.net/json/";
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            StringBuffer response = new StringBuffer();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            Log.d(TAG, "ipAddress2: "+response.toString());
            return  response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


        Log.d(TAG, "onPostExecuteEx: "+s);
        if(s!=null)
        {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String ipAddress = jsonObject.getString("ip");
                String location = jsonObject.getString("loc");
                String splitLocation[] = location.split(",");
                ipAddressInterface.onIpAddress(ipAddress,Double.parseDouble(splitLocation[0]) ,Double.parseDouble(splitLocation[1]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

public interface IpAddressInterface
{
    void onIpAddress(String ipAddress, double lat, double lng);
}

    /**
     *
     * @param mActivity Activity of the Called Class
     * @param languagesList has List of Language
     * @param presenter Interface of the presenter
     * @param indexSelected selected language index;
     */

    public void showLanguagesAlert(final Activity mActivity, ArrayList<LanguageResponse.LanguagesLists> languagesList,
                                   IntroActivityContract.IntroPresenter presenter, int indexSelected)
    {


        ArrayList languageListTemp = new ArrayList<>();
        for(int language = 0; language< languagesList.size(); language++)
        {
            languageListTemp.add(languagesList.get(language).getLan_name());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mActivity.getString(R.string.select_lang));
        builder.setSingleChoiceItems((CharSequence[]) languageListTemp.toArray(new CharSequence[languagesList.size()]),
                indexSelected,(dialogInterface, i) -> {

                    String langCode = languagesList.get(languagesList.indexOf(languagesList.get(i))).getCode();
                    String langName = languagesList.get(languagesList.indexOf(languagesList.get(i))).getLan_name();
                   // int dir = Utility.changeLanguageConfig(langCode,mActivity);
                    //  dialogCallbackHelper.changeLanguage(langCode,langName,dir);
                if(langCode.equals("en"))
                    currentLanguage = 2;
                else
                    currentLanguage = 1;
                    presenter.changeLanguage(langCode,langName,currentLanguage);

                    if(alertDialogs!=null && alertDialogs.isShowing())
                        alertDialogs.dismiss();

                });

        alertDialogs = builder.create();
        alertDialogs.show();
        /* AlertDialog alertDialog = null;
        ArrayList<String> languageListTemp = new ArrayList<>();
        for(int language = 0; language< languagesList.size(); language++)
        {
            languageListTemp.add(languagesList.get(language).getLan_name());
        }
        Log.d(TAG, "showLanguagesAlert: "+languagesList.get(0).getCode()+" lang "+languagesList.get(1).getCode()
        +" temp "+languageListTemp.size() + " List "+languagesList.size());

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.select_lang));
        alertDialog = builder.create();
        AlertDialog finalAlertDialog = alertDialog;
        alertDialog.show();
        builder.setSingleChoiceItems(languageListTemp.toArray(new CharSequence[languagesList.size()]),
                indexSelected, (dialog, item) ->
                {
                    String langCode = languagesList.get(languagesList.indexOf(languagesList.get(item))).getCode();
                    String langName = languagesList.get(languagesList.indexOf(languagesList.get(item))).getLan_name();
                    int direction = Utility.changeLanguageConfig(langCode,mActivity);
                    presenter.changeLanguage(langCode,langName,direction);
                    finalAlertDialog.dismiss();
                });*/


    }



    /**
     * method for showing alertDialog for rating the app i.e opening the play store
     * @param mActivity context
     */

    public void rateApp(final Activity mActivity, final SessionManagerImpl sessionManager, final DialogInterfaceListner isClick)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(mActivity.getString(R.string.headRatingApp));
        alertDialog.setMessage(mActivity.getString(R.string.msgRatingApp));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mActivity.getString(R.string.noThanks), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isClick.dialogClick(true);
                sessionManager.setDontShowRate(true);
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mActivity.getString(R.string.remindLater), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                isClick.dialogClick(true);
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getString(R.string.rateNow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                sessionManager.setDontShowRate(true);
                openPlayStore(mActivity);
                isClick.dialogClick(false);
            }
        });

        alertDialog.show();
    }

    private static void openPlayStore(Activity mActivity)
    {
        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID));
        mActivity.startActivity(i);
    }


}


