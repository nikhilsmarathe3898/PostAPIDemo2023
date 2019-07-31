package com.localgenie.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.localgenie.BuildConfig;
import com.localgenie.R;
import com.localgenie.networking.LSPServices;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author Pramod
 * @since 31-01-2018.
 */

public class ShareActivity extends DaggerAppCompatActivity {


    @BindView(R.id.referral_code)
    TextView tv_referralCode;

    @BindView(R.id.tv_facebook)
    TextView tv_facebook;

    @BindView(R.id.tv_email)
    TextView tv_email;

    @BindView(R.id.tv_message)
    TextView tv_message;

    @BindView(R.id.tv_whatsapp)
    TextView tv_whatsapp;

    @BindView(R.id.tv_twitter)
    TextView tv_twitter;

    @BindView(R.id.tvShareCode)
    TextView tvShareCode;

    @BindView(R.id.share_txt)
    TextView share_txt;

    @BindView(R.id.social_share_txt)
    TextView social_share_txt;

    @BindString(R.string.share_text_1)
    String share_text_1;

    @BindString(R.string.share_text_2)
    String share_text_2;
    String marketUri;

    String  ref_code;
    String shareText;
    private AppTypeface appTypeface;

    @Inject
    LSPServices lspServices;
    @Inject
    SessionManagerImpl manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        appTypeface = AppTypeface.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        marketUri = "market://details?id="+ BuildConfig.APPLICATION_ID;//com.localserviceprovider.customer
        TextView tvTbTitle=toolbar.findViewById(R.id.tv_center);
        tvTbTitle.setText(R.string.share);
        tvTbTitle.setTypeface(appTypeface.getHind_semiBold());

        ref_code = getIntent().getStringExtra("ReferralCode");
        if (ref_code!=null)
            tv_referralCode.setText("" +ref_code);

        shareText = share_text_1+" "+ref_code+" "+share_text_2;

        tvShareCode.setTypeface(appTypeface.getHind_regular());
        share_txt.setTypeface(appTypeface.getHind_regular());
        tv_referralCode.setTypeface(appTypeface.getHind_semiBold());
        social_share_txt.setTypeface(appTypeface.getHind_regular());
        tv_facebook.setTypeface(appTypeface.getHind_medium());
        tv_email.setTypeface(appTypeface.getHind_medium());
        tv_message.setTypeface(appTypeface.getHind_medium());
        tv_whatsapp.setTypeface(appTypeface.getHind_medium());
        tv_twitter.setTypeface(appTypeface.getHind_medium());

        getPromoCode();
    }

    private void getPromoCode()
    {
        Observable<Response<ResponseBody>>observable = lspServices.getReferralCode(manager.getAUTH(), Constants.selLang);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        try
                        {
                            int code = responseBodyResponse.code();
                            String response;
                            switch (code)
                            {
                                case Constants.SUCCESS_RESPONSE:
                                    response = responseBodyResponse.body().string();

                                    Log.d("TAG", "onNextRESPONSE: "+response);
                                    JSONObject jsonObject = new JSONObject(response);

                                    JSONObject jsonData = jsonObject.getJSONObject("data");
                                    String desc = jsonData.getString("description");
                                    ref_code = jsonData.getString("referralCode");
                                    if (ref_code!=null)
                                        tv_referralCode.setText("" +ref_code);
                                    if(!"".equals(desc) && desc!=null)
                                        tvShareCode.setText(desc);
                                    break;
                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.tv_facebook)
    void setTv_facebook() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareText);

        // See if Facebook app installed/found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // If not installed, launch Facebook share in a browser
        if (!facebookAppFound) {

            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + shareText;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(intent);
        }else
        {
            ShareDialog shareDialog = new ShareDialog(this);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContents = new ShareLinkContent.Builder()
                        .setQuote(getString(R.string.app_name)+'\n'+shareText)
                        .setContentUrl(Uri.parse(marketUri))
                        .build();

                shareDialog.show(linkContents);  // Show facebook ShareDialog
            }
        }

    }

    @OnClick(R.id.tv_email)
    void setTv_email() {
        shareText = shareText+" "+marketUri+"\"";
        Intent email=new Intent(Intent.ACTION_SENDTO);
        email.setType("text/plain");
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_SUBJECT, "Download "+getString(R.string.app_name));
        email.putExtra(Intent.EXTRA_TEXT, shareText);
        email.setData(Uri.parse("mailto:"));
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @OnClick(R.id.tv_message)
    void setTv_message() {
        shareText = shareText+" "+marketUri+"\"";
        Intent sendIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", shareText);
        startActivity(sendIntent);
    }


    @OnClick(R.id.tv_whatsapp)
    void setTv_whatsapp() {
        shareText = shareText+" "+marketUri+"\"";
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_twitter)
    void setTv_twitter() {
        shareText = shareText+" "+marketUri+"\"";
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        tweetIntent.setType("text/plain");

        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, shareText);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(shareText)));
            startActivity(i);
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("SHARE", "UTF-8 should always be supported", e);
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}