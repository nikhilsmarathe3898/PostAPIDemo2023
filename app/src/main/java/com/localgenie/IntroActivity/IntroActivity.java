package com.localgenie.IntroActivity;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.home.MainActivity;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.LocaleUtil;
import com.localgenie.utilities.SessionManagerImpl;
import com.pojo.LanguageResponse;
import com.utility.AlertProgress;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class IntroActivity extends DaggerAppCompatActivity implements IntroActivityContract.IntroView {

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.btnGetStarted)
    Button btnGetStarted;

    @BindString(R.string.wait_guest_login)
    String wait_guest_login;
    @BindView(R.id.tvLandingLanguages)
    AppCompatTextView tvLandingLanguages;

    @Inject
    IntroActivityContract.IntroPresenter presenter;
    @Inject
    AlertProgress alertProgress;
    @Inject
    SessionManagerImpl manager;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.lfade);
        Animation rAnimation = AnimationUtils.loadAnimation(this, R.anim.rfade);

        btnLogin.startAnimation(animation);
        btnGetStarted.startAnimation(rAnimation);

        accountManager = AccountManager.get(this);


        if(LocaleUtil.getUserLocale().getLanguage().trim().equals("hi"))
        {
            btnLogin.setText("abcd");
        }
    }

    @Override
    public void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(IntroActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText(wait_guest_login);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        //progressBar.setVisibility(View.GONE);
        alertDialog.dismiss();
    }


    @OnClick({R.id.btnLogin, R.id.btnGetStarted, R.id.tvLandingLanguages})
    public void onClickEvent(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnLogin:
               // Constants.selLang = manager.getLanguageSettings().getCode();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btnGetStarted:
                //Toast.makeText()
                    presenter.doGuestLogin();
                  break;
            case R.id.tvLandingLanguages:
                presenter.onLanguageCalled();
                break;
        }
    }

    @Override
    public void loginSuccess(String auth, String device_id) {
        //  setAuthToken(device_id,"",auth);

      /*  String get_Auth = getAuthToken(device_id);
        Log.e("LOGIN","auth_token :  "+get_Auth);*/
       // Constants.selLang = manager.getLanguageSettings().getCode();
        Log.d("TAG", "loginSuccess: " + Constants.selLang);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void setLanguage(String language, boolean restart) {
        Log.d("TAG", "setLanguage: " + language);
        tvLandingLanguages.setText(language);
       /* if (restart) {
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Runtime.getRuntime().exit(0);
        }*/
    }

    @Override
    public void showLanguagesDialog(int index, ArrayList<LanguageResponse.LanguagesLists> languagesLists) {
        alertProgress.showLanguagesAlert(IntroActivity.this, languagesLists, presenter, index);
    }

    /**
     * <h2>addAccount</h2>
     * This method is used to set the auth token by creating the account manager with the account
     * @param emailID email ID to be added
     */
  /*  public void setAuthToken(String emailID, String password, String authToken) {
        Account account = new Account(emailID, AccountGeneral.ACCOUNT_TYPE);
        try {
            accountManager.addAccountExplicitly(account, password, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);
    }*/

    /**
     * <h2>getAuthToken</h2>
     * This method is used to get the auth token from the created account
     *
     * @return auth token stored
     */
  /*  public String getAuthToken(String emailID) {
        Account[] account = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        List<Account> accounts = Arrays.asList(account);
        Log.e("INTRO_GET", "auth token from size " + accounts.size() + " " + emailID);
        if (accounts.size() > 0) {
            for (int i = 0; i < accounts.size(); i++) {
                Log.e("INTRO_GET","auth token from size " + accounts.get(i).name);
                if (accounts.get(i).name.equals(emailID))
                    return accountManager.peekAuthToken(accounts.get(i), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                else
                    removeAccount(accounts.get(i).name);
            }
        }
        return null;
    }*/

    /**
     * <h2>removeAccount</h2>
     * This method is used to remove the account stored
     * <p>
     * emailID email ID of the account
     */
   /* public void removeAccount(String emailID) {
        Account[] account = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        List<Account> accounts = Arrays.asList(account);
        Log.e("INTRO_REMOVE","auth token from size " + accounts.size() + " " + emailID);
        if (accounts.size() > 0) {
            for (int i = 0; i < accounts.size(); i++) {
                Log.e("INTRO_REMOVE", "auth token from size " + accounts.get(i).name);
                if (accounts.get(i).name.equals(emailID)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                        Log.e("INTRO","account removed " + accountManager.removeAccountExplicitly(accounts.get(i)));
                }
            }
        }
    }*/
    @Override
    public void onError(String msg) {
        alertProgress.alertinfo(this, msg);
    }

}