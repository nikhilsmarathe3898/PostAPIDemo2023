package com.localgenie.Login;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.localgenie.IntroActivity.IntroActivity;
import com.localgenie.R;
import com.localgenie.forgotpassword.ForgotPwdActivity;
import com.localgenie.home.MainActivity;
import com.localgenie.model.UserDetailsDataModel;
import com.localgenie.signup.SignUpActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManagerImpl;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * /**
 * <h1>Login Activity</h1>
 * <p>
 *     This class is used to provide the Login screen, where we can do our login and if we forget
 *     our password then here we also can make a request to forgot password
 *     and if login successful, then it directly opens HomeScreen.
 * </p>
 * @author Pramod
 * @since 21-12-2017.
 */

public class LoginActivity extends DaggerAppCompatActivity implements LoginView {

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.at_login_email)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.et_login_password)
    EditText passwordEditText;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.tv_signup)
    TextView tv_signup;
    @BindView(R.id.tv_center)
    TextView tv_center;
    @BindView(R.id.rlFbLogin)
    RelativeLayout rlFbLogin;
    @BindView(R.id.rlGoogleLogin)
    RelativeLayout rlGoogleLogin;
    @BindView(R.id.tv_forgotpass)
    TextView tv_forgotpass;
    @BindView(R.id.til_login_email)
    TextInputLayout til_login_email;
    @BindView(R.id.til_login_pwd)
    TextInputLayout til_login_pwd;
    @BindView(R.id.loginscrollview)
    ScrollView loginscrollview;
    @BindString(R.string.signin_login) String signin_login;
    @BindString(R.string.wait_signin) String wait_signin;
    @BindString(R.string.err_uname) String err_uname;
    @BindString(R.string.invalid_username) String invalid_username;
    @BindString(R.string.err_password) String err_password;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    AccountManager accountManager;
    private CallbackManager callbackManager;
    @Inject
    SessionManagerImpl manager;

    @Inject
    AlertProgress alertProgress;
    boolean flag_fb_google = false;

    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int REQUEST_ID_READ_STORAGE_PERMISSION = 116;

    private static final int REQUEST_ID_GOOGLE_SIGNIN = 66;
    @Inject
    AppTypeface appTypeface;

    /**
     * This is the onCreate LoginActivity method that is called firstly, when user came to Login screen.
     * @param savedInstanceState contains an instance of Bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);

        initialize();
    }

    /**
     * <h2>initialize</h2>
     * <p> method to initialize the views</p>
     */
    private void initialize() {
        Toolbar toolbar= findViewById(R.id.login_tool);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        accountManager = AccountManager.get(this);

        callbackManager = CallbackManager.Factory.create();

        presenter.initializeFBGoogle();

        tv_center.setText(signin_login);
        tv_center.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logout_flag = getIntent().getStringExtra("logout");
                if (logout_flag == null || "".equals(logout_flag.trim())) {
                    onBackPressed();
                } else {
                    //onBackPressed();
                    Intent intent = new Intent(LoginActivity.this,IntroActivity.class);
                    startActivity(intent);
                }
            }
        });

        accountManager = AccountManager.get(this);

        til_login_email.setErrorEnabled(true);
        til_login_pwd.setErrorEnabled(true);

        Animation lAnimation = AnimationUtils.loadAnimation(this,R.anim.lfade);

        rlFbLogin.startAnimation(lAnimation);
        btn_login.startAnimation(lAnimation);

        Animation rAnimation = AnimationUtils.loadAnimation(this,R.anim.rfade);

        rlGoogleLogin.startAnimation(rAnimation);

    }

    @Override
    public void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            tv_progress.setText(wait_signin);
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

    @Override
    public void setUsernameError() {
        til_login_email.setError(err_uname);
    }

    @Override
    public void setInvalidUname() {
        til_login_email.setError(invalid_username);
    }

    @Override public void setPasswordError() {
        til_login_pwd.setError(err_password);
    }

    @Override
    public void revokeGuestLogin(String auth) {
        // removeAccount(auth);
        Log.e("LOGIN","Revoked auth_token :  ");
    }



    @Override
    public void onLoginSuccess(String auth,String username,String password) {
        //  setAuthToken(username,password,auth);



        if(manager.getGuestLogin())
            manager.setGuestLogin(false);
        else
        {
            manager.setGuestLogin(false);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        Log.d("TAG", "onLoginSuccess: "+manager.getGuestLogin());
        finish();

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
   /* public String getAuthToken(String emailID) {
        Account[] account = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        List<Account> accounts = Arrays.asList(account);
        Log.e("LOGIN_GET", "auth token from size " + accounts.size() + " " + emailID);
        if (accounts.size() > 0) {
            for (int i = 0; i < accounts.size(); i++) {
                Log.e("LOGIN_GET","auth token from size " + accounts.get(i).name);
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
     *
     /
     /*  public void removeAccount(String emailID) {
     Account[] account = accountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
     List<Account> accounts = Arrays.asList(account);
     Log.e("LOGIN_REMOVE","auth token from size " + accounts.size() + " " + emailID);
     if (accounts.size() > 0) {
     for (int i = 0; i < accounts.size(); i++) {
     Log.e("LOGIN_REMOVE", "auth token from size " + accounts.get(i).name);
     if (accounts.get(i).name.equals(emailID)) {
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
     Log.e("LOGIN","account removed " + accountManager.removeAccountExplicitly(accounts.get(i)));
     }
     }
     }
     }*/

    @Override
    public void onError(String msg) {
        alertProgress.alertinfo(this,msg);
    }


    @OnTextChanged(value = R.id.et_login_password,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPwdTxtChange(Editable editable) {
        Log.e("TAG", "onTextChanged event");
        int str_len = editable.toString().length();
        if (str_len <= 0) {
            til_login_pwd.setError(null);
            setPasswordError();
        }
        if (str_len > 0) {
            til_login_pwd.setError(null);
        }
    }

    @OnTextChanged(value = R.id.at_login_email,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterEmailTxtChange(Editable editable) {
        int email_str_len = editable.toString().length();
        if (email_str_len <= 0) {
            til_login_email.setError(null);
            setUsernameError();
        }
        if (email_str_len > 0) {
            til_login_email.setError(null);
        }
    }

    @OnFocusChange({R.id.at_login_email,R.id.et_login_password})
    void onFocusEvent(View view,boolean hasFocus) {
        switch (view.getId()) {
            case R.id.at_login_email:
                if(!hasFocus) {
                    //Checks Email
                    til_login_email.setError(null);
                    presenter.validateUname(autoCompleteTextView.getText().toString());
                }
                break;
            case R.id.et_login_password:
                if(!hasFocus) {
                    //Checks password
                    til_login_pwd.setError(null);
                    //presenter.validatePwd(passwordEditText.getText().toString());
                }
                break;

            default:
                Log.e("LOGON","onFocus default case");
                break;
        }
    }

    @OnClick({R.id.btn_login,R.id.tv_signup,R.id.rlFbLogin,R.id.rlGoogleLogin,R.id.tv_forgotpass})
    void onClickEvent(View view) {
        switch (view.getId()){

            case R.id.btn_login:
                presenter.validateCredentials(autoCompleteTextView.getText().toString(),passwordEditText.getText().toString());
                break;

            case R.id.tv_signup:
                Intent signUpIntent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;

            case R.id.rlFbLogin:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
                        presenter.storeLoginType(2);
                        presenter.handleResultFromFB(callbackManager);
                    } else {
                        flag_fb_google = true;
                        checkAndReqReadPerms();
                    }
                } else {
                    presenter.storeLoginType(2);
                    presenter.handleResultFromFB(callbackManager);
                }
                break;

            case R.id.rlGoogleLogin:

                googleLogin();

                break;

            case R.id.tv_forgotpass:
                Intent forgotPwdIntent=new Intent(LoginActivity.this, ForgotPwdActivity.class);
                startActivity(forgotPwdIntent);
                break;

            default:
                Log.e("LOGON","Click Default case");
                break;

        }
    }

    private void googleLogin() {
        Log.e("TAG", "onClickEvent: Login with Google");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
                presenter.storeLoginType(3);
                presenter.googleLogin();
            } else {
                checkAndReqReadPerms();
            }
        } else {
            presenter.storeLoginType(3);
            presenter.googleLogin();
        }
    }

    /*<h2>checkAndReqReadPerms</h2>
     * <p> Check and Request for READ EXTERNAL STORAGE Permissions for facebook login. </p>
     */
    private void checkAndReqReadPerms() {
        if (Build.VERSION.SDK_INT >= 23) {

            int readPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (readPerm != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_READ_STORAGE_PERMISSION);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.initializeFacebook();
    }


    /**
     * predefined method to check run time permissions list call back
     *
     * @param requestCode   request code
     * @param permissions:  contains the list of requested permissions
     * @param grantResults: contains granted and un granted permissions result list
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_READ_STORAGE_PERMISSION:

                boolean permissionAllowed=false;
                String deniedPermission="";
                int i = 0;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionAllowed = true;
                        i++;
                    } else {
                        deniedPermission = permissions[i];
                        permissionAllowed = false;
                        break;
                    }
                }
                if(permissionAllowed){
                    if (flag_fb_google) {
                        presenter.storeLoginType(2);
                        presenter.handleLoginType(callbackManager);
                    } else {
                        presenter.storeLoginType(3);
                        presenter.googleLogin();
                    }
                } else{
                    //Toast.makeText(this,deniedPermission+" Permission is denied",Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN",deniedPermission+" Permission is denied");
                    boolean somePermissionsForeverDenied = false;
                    for(String permission: permissions) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            //denied
                            Log.e("denied", permission);
                            somePermissionsForeverDenied = true;
                            //Toast.makeText(this,"Permission Denied! Please grant SMS permissions to proceed further!",Toast.LENGTH_LONG).show();
                        } else {
                            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                                //allowed
                                Log.e("allowed", permission);
                            } else {
                                //set to never ask again
                                Log.e("set to never ask again", permission);
                                somePermissionsForeverDenied = true;
                            }
                        }
                    }

                    if (somePermissionsForeverDenied)
                        Toast.makeText(this,"Permission is denied, Please grant SMS permission to proceed further!",Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void openGoogleActivity(Intent intent)
    {
        startActivityForResult(intent,REQUEST_ID_GOOGLE_SIGNIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_GOOGLE_SIGNIN) {
            if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString()))
                autoCompleteTextView.setText("");
            if (!TextUtils.isEmpty(passwordEditText.getText().toString()))
                passwordEditText.setText("");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            presenter.handleResultFromGoogle(result);
        }
    }



    @Override
    public void navToSignUp(Integer loginType, UserDetailsDataModel userDetailsDataModel, GoogleApiClient mGoogleApiClient) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("user_details",userDetailsDataModel);
       // bundle.putSerializable("GoogleApiClient",mGoogleApiClient);
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void setEmailView(String username)
    {
        til_login_email.setErrorEnabled(false);
        til_login_email.setError(null);
        autoCompleteTextView.setText(username);
    }

  /*  @OnFocusChange(R.id.et_login_password)
    public void onFocusChanged(boolean hasFocus)
    {
        if(hasFocus)
        {
            loginscrollview.smoothScrollTo(0,0);
        }
    }*/

    @Override
    public void googleClientIsNotConnected() {

        alertProgress.tryAgain(this, getString(R.string.googleClientIsNotConnected), getString(R.string.try_again)
                , new DialogInterfaceListner() {
                    @Override
                    public void dialogClick(boolean isClicked) {
                        if(isClicked)
                        {
                            presenter.initializeFBGoogle();
                            googleLogin();
                        }
                    }
                });
    }
}
