package com.localgenie.changepassword;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.localgenie.Login.LoginActivity;
import com.localgenie.R;
import com.localgenie.profile.ProfileActivity;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.SessionManager;
import com.utility.AlertProgress;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class ChangePwdActivity extends DaggerAppCompatActivity implements ChangePwdView {

    public final String TAG = "ChangePwdActivity";

    @BindView(R.id.btn_continue)
    Button btn_continue;

    @BindView(R.id.change_pwd_text)
    TextView change_pwd_text;

    @BindView(R.id.et_new_pwd)
    EditText et_new_pwd;

    @BindView(R.id.et_old_pwd)
    EditText et_old_pwd;

    @BindView(R.id.til_old_pwd)
    TextInputLayout til_old_pwd;

    @BindView(R.id.til_new_pwd)
    TextInputLayout til_new_pwd;

    @BindView(R.id.et_reenter_pwd)
    EditText et_reenter_pwd;

    @BindView(R.id.til_reenter_pwd)
    TextInputLayout til_reenter_pwd;

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;

    @Inject
    AlertProgress alertProgress;

    private String coming_from;
    private String auth;

    @Inject
    ChangePwdPresenter presenter;

    @Inject
    SessionManager sessionManager;

    @Inject
    AppTypeface appTypeface;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                //TextView tv = (TextView) findViewById(R.id.txtview);
                //Log.e("OTP"," message is ::  "+message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            coming_from="";
            btn_continue.setText(getResources().getString(R.string.continue_txt));
        } else {
            auth = bundle.getString("auth");
            coming_from = bundle.getString("coming_from");
            btn_continue.setText(getResources().getString(R.string.change_password));
        }
        initialize();
    }

    private void initialize() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.signup_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textCenter = toolbar.findViewById(R.id.tv_center);
        if ("profile".equals(coming_from))
            textCenter.setText(R.string.change_password);
        else
            textCenter.setText(R.string.your_new_password);

        textCenter.setTypeface(appTypeface.getHind_semiBold());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(view -> {
            if ("profile".equals(coming_from)) {
                Intent intent = new Intent(ChangePwdActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            } else
                onBackPressed();
        });

        if ("profile".equals(coming_from)) {
            change_pwd_text.setVisibility(View.GONE);
            til_new_pwd.setVisibility(View.VISIBLE);
        } else {
            change_pwd_text.setVisibility(View.VISIBLE);
            til_old_pwd.setVisibility(View.GONE);
            //til_new_pwd.setVisibility(View.GONE);

            et_new_pwd.requestFocus();

            et_reenter_pwd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String passwrd = et_new_pwd.getText().toString();
                    String confirm_pwd = et_reenter_pwd.getText().toString();
                    if (editable.length() > 0 && passwrd.length() > 0) {
                        if(!confirm_pwd.equals(passwrd )){
                            // give an error that password and confirm password not match
                            Log.e(TAG,"Error confirm_pwd"+confirm_pwd+ "   pwd ::  "+passwrd);
                        } else {
                            Log.e(TAG,"confirm_pwd"+confirm_pwd+ "   pwd ::  "+passwrd);
                        }

                    }
                }
            });
        }
    }

    @OnClick(R.id.btn_continue)
    void setBtn_continue() {
        if ("profile".equals(coming_from)) {
            Log.e(TAG, "setBtn_continue: AUTH"+auth);

            presenter.profChangePassword(auth,et_old_pwd.getText().toString(),et_new_pwd.getText().toString());

        } else {
            String sid = sessionManager.getSID();

            Log.e(TAG, "setBtn_continue: SID =>  " + sid);

            if(et_new_pwd.getText().toString().trim().equals(et_reenter_pwd.getText().toString().trim()))
                presenter.changePassword(sid, et_new_pwd.getText().toString());
            else
                alertProgress.alertinfo(this,getString(R.string.PasswordShouldBeSame));
        }

    }

    @Override
    public void showProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(ChangePwdActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            if ("profile".equals(coming_from))
                tv_progress.setText("Change Password");
            else
                tv_progress.setText(getString(R.string.wait_signin));
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
    public void setPasswordInvalid() {
        til_new_pwd.setError(getString(R.string.invalid_password));
        et_new_pwd.setText("");
    }

    @Override
    public void setPasswordError() {
        til_new_pwd.setError(getString(R.string.err_password));
    }

    @Override
    public void setConfirmPwdInvalid() {
        til_reenter_pwd.setError(getString(R.string.invalid_password));
        et_reenter_pwd.setText("");
    }

    @Override
    public void setConfirmPwdError() {
        til_reenter_pwd.setError(getString(R.string.err_password));
    }

    @Override
    public void setError(String msg) {
        Toast.makeText(this,""+msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void navtoLogin() {
        Intent intent = new Intent(ChangePwdActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void navToProfile() {
        Intent intent = new Intent(ChangePwdActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
