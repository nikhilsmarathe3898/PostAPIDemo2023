package com.localgenie.sidescreens;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.AboutLSPActivity;
import com.localgenie.faq.FaqActivity;
import com.localgenie.favouriteProvider.FavouriteProvider;
import com.localgenie.home.ServicesFrag;
import com.localgenie.model.CityData;
import com.localgenie.networking.LSPServices;
import com.localgenie.payment_method.PaymentMethodActivity;
import com.localgenie.profile.ProfileActivity;
import com.localgenie.share.ShareActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.localgenie.wallet.WalletActivity;
import com.localgenie.wallet.WalletActivityContract;
import com.localgenie.youraddress.YourAddressActivity;
import com.localgenie.zendesk.zendeskHelpIndex.ZendeskHelpIndex;
import com.pojo.LanguageResponse;
import com.pojo.LanguagesList;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import adapters.SidescreenAdapter;
import dagger.android.support.DaggerFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SidescreensFrag extends DaggerFragment implements
        WalletActivityContract.WalletView,View.OnClickListener, SidescreenAdapter.OnSideScreenClick {
    public static CityData.PaymentMode paymentMode;
    RecyclerView rvSidescreens;
    View layout;

    TextView tvName;
    TextView tvEmail;
    TextView tvPhoneNo;
    TextView tvGuestLogin;

    AppBarLayout appBarProfile;
   CollapsingToolbarLayout toolbar_layout;

    ImageView iv_prof_img;

    boolean flag_guest_login;

    @Inject SessionManagerImpl sessionManager;
    @Inject
    AppTypeface appTypeface;
    @Inject
    AlertProgress alertProgress;
    private boolean isLoginTrue = false;
    private String name = "";
    private SidescreenAdapter adapter;
    private Context mContext;
    @Inject
    WalletActivityContract.WalletPresenterBalance walletPresenterBalance;
    private String sidescreens[];

    int drawableArray[] ={R.drawable.ic_menu_payment, R.drawable.ic_wallet_grey,R.drawable.ic_favourite,
            R.drawable.ic_menu_youraddress,R.drawable.ic_menu_share
            ,R.drawable.ic_menu_faq,R.drawable.ic_menu_helpcentre, R.drawable.ic_chat_black_24dp ,R.drawable.ic_menu_lsp,R.drawable.ic_lang};
    @Inject
    LSPServices lspServices;
    @Inject
    Gson gson;
    private static AlertDialog alertDialogs = null;
    @Inject
    public SidescreensFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout=inflater.inflate(R.layout.fragment_sidescreen, container, false);

        walletPresenterBalance.attachView(this);
        return layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SidescreensPresenter presenter=new SidescreensPresenter(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void appBarChangeListener()
    {
        appBarProfile.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                Log.d("TAG", "onOffsetChanged: "+verticalOffset+"  scroll "+scrollRange);
                if (scrollRange + verticalOffset == 0)
                {
                   // toolbar_layout.setTitle(name);
                    toolbar_layout.setTitle(getString(R.string.accounts));
                  //  toolBarTitle.setText("Registereres");
                   // ivProDtlsPic.setVisibility(View.GONE);
                   // toolProvider.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    isShow = true;
                } else if (isShow) {
                    toolbar_layout.setTitle("");
                    // toolBarTitle.setText("");
                 //   ivProDtlsPic.setVisibility(View.VISIBLE);
                  //  toolProvider.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onResume() {
        Log.e("TAG", "onResume: " );
        flag_guest_login = sessionManager.getGuestLogin();
        super.onResume();
        getWalletBalance();
        if (flag_guest_login) {
            tvEmail.setVisibility(View.INVISIBLE);
            tvName.setVisibility(View.INVISIBLE);
            tvPhoneNo.setVisibility(View.INVISIBLE);
            tvGuestLogin.setVisibility(View.VISIBLE);
            Constants.isLoggedIn = true;
        } else {
            callProfileUpdate();
            Constants.isLoggedIn = false;

        }

        walletBalanceValues();

    }

    private void callProfileUpdate()
    {
        tvEmail.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
        tvPhoneNo.setVisibility(View.VISIBLE);
        tvGuestLogin.setVisibility(View.GONE);

        tvEmail.setText(sessionManager.getEmail());
         name = sessionManager.getFirstName() + " " + sessionManager.getLastName();
        tvName.setText(name);
        String mobileNumber = sessionManager.getCountryCode() +"-"+sessionManager.getMobileNo();
        tvPhoneNo.setText(mobileNumber);

        if (!TextUtils.isEmpty(sessionManager.getProfilePicUrl())) {
            if (iv_prof_img != null) {
                Glide.with(getActivity())
                        .load(sessionManager.getProfilePicUrl())
                        .apply(Utility.createGlideOptionCall(getActivity()))
                        .into(iv_prof_img);
            }
        }
    }


    private void initialize() {

        rvSidescreens=layout.findViewById(R.id.rvSidescreens);
        tvName=layout.findViewById(R.id.tvName);
        tvEmail=layout.findViewById(R.id.tvEmail);
        tvPhoneNo=layout.findViewById(R.id.tvPhoneNo);
        tvGuestLogin=layout.findViewById(R.id.tvGuestLogin);
        iv_prof_img=layout.findViewById(R.id.iv_prof_img);
        appBarProfile=layout.findViewById(R.id.appBarProfile);
        toolbar_layout=layout.findViewById(R.id.toolbar_layout);
        appBarChangeListener();
        toolbar_layout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        toolbar_layout.setCollapsedTitleTypeface(appTypeface.getHind_semiBold());

        tvName.setTypeface(appTypeface.getHind_medium());
        tvEmail.setTypeface(appTypeface.getHind_regular());
        tvPhoneNo.setTypeface(appTypeface.getHind_regular());

        RelativeLayout rlProfile=layout.findViewById(R.id.rlProfile);
        rlProfile.setOnClickListener(this);

        LinearLayoutManager llmanager=new LinearLayoutManager(getActivity());
        llmanager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSidescreens.setLayoutManager(llmanager);
         sidescreens=getResources().getStringArray(R.array.sidescreens);

    }


    public void getWalletBalance()
    {
        if(alertProgress.isNetworkAvailable(getActivity()))
        {
            walletPresenterBalance.getWalletLimits();
        }else
            alertProgress.showNetworkAlert(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlProfile:
                //Toast.makeText(getActivity(),"To profile Activity",Toast.LENGTH_SHORT).show();
                if (flag_guest_login) {
                    isLoginTrue = true;
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    public void onSideScreenclicked(String sideScreenName)
    {
        switch (sideScreenName){
            /*case "Reviews":
                Intent intent=new Intent(getActivity(),ReviewActivity.class);
                startActivity(intent);
                break;*/
            case "Add Card":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent paymentIntent = new Intent(getActivity(), PaymentMethodActivity.class);
                    startActivity(paymentIntent);
                }
                break;
            case "My Addresses":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent addrIntent = new Intent(getActivity(), YourAddressActivity.class);
                    startActivity(addrIntent);
                }
                break;
            case "Favourite Providers":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent favIntent = new Intent(getActivity(), FavouriteProvider.class);
                    startActivity(favIntent);
                    getActivity().overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);

                }
                break;
            case "FAQ":
                Intent faqIntent=new Intent(getActivity(),FaqActivity.class);
                startActivity(faqIntent);
                getActivity().overridePendingTransition(R.anim.side_slide_out,R.anim.stay_still);
                break;
            case "About Service Genie":
                Intent aboutLSPIntent=new Intent(getActivity(),AboutLSPActivity.class);
                startActivity(aboutLSPIntent);
                break;
            case "Share":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent shareIntent = new Intent(getActivity(), ShareActivity.class);
                    shareIntent.putExtra("ReferralCode", sessionManager.getReferralCode());
                    startActivity(shareIntent);
                }
                break;
            case "Help Centre":
                Intent helpIntent =new Intent(getActivity(),ZendeskHelpIndex.class);
                startActivity(helpIntent);
                break;
            case "Live Chat":
                callLiveSupport(name, sessionManager.getEmail());
                    break;
            case "Wallet":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    if(ServicesFrag.paymentMode.isWallet()) {
                        Intent intent = new Intent(getActivity(), WalletActivity.class);
                        startActivityForResult(intent, Constants.WALLETCALL);
                    }else{
                        alertProgress.alertinfo(getContext(),"Wallet is not active for selected Location.");
                    }
                    }

                break;
            case "Select Language":
                if (flag_guest_login) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else {

                    // ((Activity)mContext).recreate();
                    onLanguageCalled();
                   /* Intent intent = new Intent(getActivity(), LanguageActivity.class);
                    startActivityForResult(intent, 2);*/
                }
                break;

        }
    }
    /*Start of LiveChat (www.livechatinc.com) code*/

    public void callLiveSupport(String uname,String email) {
        final String LICENSE_KEY = "4711811"; //4711811  8926529
        final String GROUP_ID = getString(R.string.app_name);

        Intent intent = new Intent(getActivity(), ChatWindowActivity.class);
        intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, GROUP_ID);
        intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, LICENSE_KEY);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, uname);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, email);
        startActivity(intent);

    }
/*End of LiveChat code*/

    @Override
    public void onSideScreenClicked(String sideScreenName) {
        onSideScreenclicked(sideScreenName);
    }

    @Override
    public void walletDetailsApiErrorViewNotifier(String error) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void showToast(String msg, int duration) {

    }

    @Override
    public void showAlert(String title, String msg) {

    }

    @Override
    public void noInternetAlert() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void setBalanceValues(String balance, String hardLimit, String softLimit) {

       // adapter.notifyDataSetChanged();
        walletBalanceValues();
    }

    @Override
    public void showRechargeConfirmationAlert(String amount) {

    }

    @Override
    public void setCard(String cardNum, String cardType) {

    }

    @Override
    public void setNoCard() {

    }

    @Override
    public void walletRecharged(boolean recharged, String message)
    {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        walletPresenterBalance.detachView();
    }

    public void walletBalanceValues()
    {

        adapter=new SidescreenAdapter(sidescreens,drawableArray,getActivity(),this);
        rvSidescreens.setHasFixedSize(true);
        rvSidescreens.setAdapter(adapter);
      //  setBalanceValues("","","");
    }

    @Override
    public void onLogout(String msg) {
        alertProgress.alertPositiveOnclick(mContext, msg, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
                Utility.setMAnagerWithBID(mContext,sessionManager);
            }
        });
    }

    @Override
    public void onError(String message) {

        alertProgress.alertinfo(mContext,message);
    }


    public void onLanguageCalled()
    {
        Observable<Response<ResponseBody>> languageResponse = lspServices.onLanguageCalled(Constants.selLang);

        languageResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {

                        int code = responseBodyResponse.code();
                        try {
                            if(code==200)
                            {

                                String response = responseBodyResponse.body().string();

                                Log.d("TAG", "onNextLAnguage: "+response);
                                LanguageResponse languageResponses = gson.fromJson(response,LanguageResponse.class);

                                Log.d("TAG", "onNext: "+languageResponses.getLanguagesLists().get(0).getCode()
                                        +" sess "+sessionManager.getLanguageSettings().getCode());

                                boolean isLanguage = false;
                                for(int i = 0;i<languageResponses.getLanguagesLists().size(); i++)
                                {
                                    if(sessionManager.getLanguageSettings().getCode().equals(languageResponses.getLanguagesLists().get(i).getCode()))
                                    {
                                        isLanguage = true;
                                        showLanguagesDialog(languageResponses.getLanguagesLists().indexOf(languageResponses.getLanguagesLists().get(i)),languageResponses.getLanguagesLists());
                                        break;
                                    }
                                }
                                if(!isLanguage)
                                    showLanguagesDialog(-1,languageResponses.getLanguagesLists());
                            }else
                            {
                                String response = responseBodyResponse.errorBody().string();

                                onErrorMsg(new JSONObject(response).getString("message"));
                            }
                        } catch (IOException e) {
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


    public void changeLanguage(String langCode, String langName, int direction)
    {
        sessionManager.setLanguageSettings(new LanguagesList(langCode,langName,direction));
        Constants.selLang = sessionManager.getLanguageSettings().getCode();

        setLanguage(langName,true);
    }
    public void onErrorMsg(String msg) {
        alertProgress.alertinfo(mContext, msg);
    }

    public void showLanguagesDialog(int index, ArrayList<LanguageResponse.LanguagesLists> languagesLists) {
        showLanguagesAlertOne(getActivity(), languagesLists, index);
    }

    public void setLanguage(String language, boolean restart) {
        Log.d("TAG", "setLanguage: " + language);
        // tvLandingLanguages.setText(language);
        if(language.contains("Arabic")) {
            // LocaleHelper.initialize(getApplicationContext(), LocaleHelper.ARABIC);
            sessionManager.setLanguageSettings(new LanguagesList("AR", "Arabic", 1));
        }else{
            //  LocaleHelper.initialize(getApplicationContext(), LocaleHelper.ENGLISH);
            sessionManager.setLanguageSettings(new LanguagesList("en", "English", 0));
        }
        Constants.selLang = sessionManager.getLanguageSettings().getCode();
        // Utility.changeLanguageConfig(manager.getLanguageSettings().getCode(), this);

        /*if (restart) {
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Runtime.getRuntime().exit(0);

        }*/

        ((Activity)mContext).recreate();

    }

    public void showLanguagesAlertOne(final Activity mActivity, ArrayList<LanguageResponse.LanguagesLists> languagesList, int indexSelected)
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
                    int dir = Utility.changeLanguageConfig(langCode,mActivity);

                    //  dialogCallbackHelper.changeLanguage(langCode,langName,dir);
                    changeLanguage(langCode,langName,dir);

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

}
