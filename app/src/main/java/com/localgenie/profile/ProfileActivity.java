package com.localgenie.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.localgenie.R;
import com.localgenie.change_email.ChangeEmailActivity;
import com.localgenie.changepassword.ChangePwdActivity;
import com.localgenie.countrypic.CountryPicker;
import com.localgenie.utilities.AppPermissionsRunTime;
import com.localgenie.utilities.AppTypeface;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.HandlePictureEvents;
import com.localgenie.utilities.ImageUploadedAmazon;
import com.localgenie.utilities.SessionManagerImpl;
import com.localgenie.utilities.Utility;
import com.utility.AlertProgress;
import com.utility.DialogInterfaceListner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;
import eu.janmuller.android.simplecropimage.CropImage;

public class ProfileActivity extends DaggerAppCompatActivity implements ProfileView {

    @BindView(R.id.iv_prof_img)
    ImageView ivProfilePic;

    @BindView(R.id.etProflNme)
    EditText tieFirstName;

    @BindView(R.id.etProflLNme)
    EditText tieLastName;

    @BindView(R.id.tvProflEml)
    TextView tvProflEml;

    @BindView(R.id.tvProflMob)
    TextView tvProflMob;

    @BindView(R.id.etProfAboutMe)
    EditText tieAboutMe;

    @BindView(R.id.ivProEditForwrdEml)
    ImageView ivProEditForwrdEml;

    @BindView(R.id.ivProEditForwrdMob)
    ImageView ivProEditForwrdMob;

    @BindView(R.id.countryCode_divider)
    View countryCode_divider;

    @BindView(R.id.viewEml)
    View viewEml;

    @BindView(R.id.viewNm)
    View viewFnm;

    @BindView(R.id.viewLNm)
    View viewLnm;

    @BindView(R.id.viewAbtMe)
    View viewAbtMe;

    @BindView(R.id.viewMob)
    View viewMob;

    @BindView(R.id.countryFlag)
    ImageView countryFlag;

    @BindView(R.id.countryCode)
    TextView countryCode;

    //@Inject
    HandlePictureEvents handlePicEvent;

    @BindString(R.string.save)
    String save_text;

    @BindString(R.string.edit)
    String edit_text;

    @BindString(R.string.about_me_hint)
    String about_me_hint_text;

    @BindColor(R.color.grey_background_vdarker)
    int greyColor;

    @BindView(R.id.btnChangepasswd)
    TextView btnChangePwd;

    @BindView(R.id.btnLogout)
    TextView btnLogout;

    @BindView(R.id.toolbarLayout)Toolbar toolbarLayout;

    @BindView(R.id.tv_center) TextView tvYourProf;
    @BindView(R.id.tv_skip) TextView tv_tb_rightbtn;
    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    @Inject
    ProfilePresenter presenter;

    @Inject
    AlertProgress alertProgress;

    @Inject
    AppTypeface appTypeface;

    @Inject
    CountryPicker mCountryPicker;

    @Inject
    SessionManagerImpl manager;

    private String auth;

    private boolean imageflag = false;
    String  profilePicUrl = "";

    private ArrayList<AppPermissionsRunTime.MyPermissionConstants> myPermissionCameraFileArrayList;
    public static final int REQUEST_CAMERA_PERMISSION = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initialize();

    }
    private void initialize() {

        tvYourProf.setText(R.string.your_profile);
        tvYourProf.setTypeface(appTypeface.getHind_semiBold());

        handlePicEvent = new HandlePictureEvents(ProfileActivity.this);

        tv_tb_rightbtn.setVisibility(View.VISIBLE);
        tv_tb_rightbtn.setText(R.string.edit);
        tv_tb_rightbtn.setTypeface(appTypeface.getHind_medium());
       // editMethod();
        saveMethod();

        toolbarLayout.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbarLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_btn_text = tv_tb_rightbtn.getText().toString();
                if (getString(R.string.save).equals(str_btn_text)) {
                    saveMethod();
                    tv_tb_rightbtn.setText(R.string.edit);
                } else {
                    onBackPressed();
                }
            }
        });

        auth = manager.getAUTH();

        typeFae();
    }

    private void typeFae() {
        btnChangePwd.setTypeface(appTypeface.getHind_medium());
        btnLogout.setTypeface(appTypeface.getHind_medium());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(manager.isProfileCalled())
        {
            setProfileFirstName(manager.getFirstName());
            setProfileLastName(manager.getLastName());
            setAbout(manager.getAbout());
            setProfileEmail(manager.getEmail());
            setProfilePic(manager.getProfilePicUrl());
            setProfileMob(manager.getMobileNo());
            setCountryCode(manager.getCountryCode());
        }else
        {
            if(alertProgress.isNetworkAvailable(this))
                presenter.getProfile(auth);
            else
                alertProgress.showNetworkAlert(this);
        }
    }

    private void editMethod() {

        tv_tb_rightbtn.setText(getString(R.string.save));

        tieFirstName.clearFocus();
        tieLastName.clearFocus();
        tieAboutMe.clearFocus();
        tvProflMob.clearFocus();

        tieFirstName.setFocusable(true);
        tieFirstName.setFocusableInTouchMode(true);
        tieFirstName.setClickable(true);
        tieLastName.setFocusable(true);
        tieLastName.setFocusableInTouchMode(true);
        tieLastName.setClickable(true);
        tieAboutMe.setFocusable(true);
        tieAboutMe.setFocusableInTouchMode(true);
        tieAboutMe.setClickable(true);
        ivProfilePic.setClickable(true);

        viewFnm.setVisibility(View.VISIBLE);
        viewLnm.setVisibility(View.VISIBLE);
        viewAbtMe.setVisibility(View.VISIBLE);

        viewEml.setVisibility(View.VISIBLE);
        viewMob.setVisibility(View.VISIBLE);

        countryCode_divider.setVisibility(View.VISIBLE);

        tvProflEml.setClickable(true);
        tvProflMob.setClickable(true);

        ivProEditForwrdEml.setVisibility(View.VISIBLE);
        ivProEditForwrdMob.setVisibility(View.VISIBLE);

        btnChangePwd.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);

    }

    private void saveMethod()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        tv_tb_rightbtn.setText(getString(R.string.edit));

        viewFnm.setVisibility(View.INVISIBLE);
        viewLnm.setVisibility(View.INVISIBLE);
        viewAbtMe.setVisibility(View.INVISIBLE);

        tieFirstName.setFocusable(false);
        tieFirstName.setFocusableInTouchMode(false);
        tieLastName.setFocusable(false);
        tieFirstName.setClickable(false);
        tieLastName.setClickable(false);
        tieAboutMe.setFocusable(false);
        tieAboutMe.setFocusableInTouchMode(false);
        tieAboutMe.setClickable(false);
        ivProfilePic.setClickable(false);

        tieFirstName.setBackgroundColor(Color.TRANSPARENT);
        tieLastName.setBackgroundColor(Color.TRANSPARENT);

        viewEml.setVisibility(View.INVISIBLE);
        viewMob.setVisibility(View.INVISIBLE);

        countryCode_divider.setVisibility(View.INVISIBLE);

        tvProflEml.setClickable(false);
        tvProflMob.setClickable(false);

        ivProEditForwrdEml.setVisibility(View.GONE);
        ivProEditForwrdMob.setVisibility(View.GONE);

        btnChangePwd.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.tvProflEml)
    public void setTvProflEml() {
        editEmailPhone(true);
    }

    @OnClick(R.id.tvProflMob)
    public void setTvProflMob() {
        editEmailPhone(false);
    }

    @OnClick(R.id.iv_prof_img)
    public void profilePicClick() {
        checkCameraPermission();
    }

    private void checkCameraPermission() {

        if (Build.VERSION.SDK_INT >= 23)
        {
            myPermissionCameraFileArrayList = new ArrayList<>();
            //myPermissionConstantsArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_PHONE_STATE);
            myPermissionCameraFileArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_CAMERA);
            myPermissionCameraFileArrayList.add(AppPermissionsRunTime.MyPermissionConstants.PERMISSION_READ_EXTERNAL_STORAGE);

            if(AppPermissionsRunTime.checkPermission(ProfileActivity.this, myPermissionCameraFileArrayList, REQUEST_CAMERA_PERMISSION))
            {
                selectImage();
            }
        }
        else
        {
            selectImage();
        }
    }

    @OnClick(R.id.tv_skip)
    public void editProfile() {
            String str_btn_text = tv_tb_rightbtn.getText().toString();
            if (getString(R.string.save).equals(str_btn_text)) {

                if(!"".equals(tieFirstName.getText().toString()))
                {
                    if(alertProgress.isNetworkAvailable(this))
                    {
                        if (!imageflag) {
                            presenter.editProfile(auth, profilePicUrl,tieFirstName.getText().toString(), tieLastName.getText().toString(), tieAboutMe.getText().toString());
                        } else {
                            presenter.editProfile(auth, profilePicUrl,tieFirstName.getText().toString(), tieLastName.getText().toString(), tieAboutMe.getText().toString());
                        }
                        saveMethod();

                    }else
                        alertProgress.showNetworkAlert(this);

                }else
                {
                    alertProgress.alertinfo(this,"First Name should not be empty");
                }


            } else {
                editMethod();
            }
    }

    @OnClick(R.id.btnChangepasswd)
    void changePassword() {
        Intent intent = new Intent(this, ChangePwdActivity.class);
        intent.putExtra("auth",auth);
        intent.putExtra("coming_from","profile");
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnLogout)
    void logout() {
        presenter.doLogout(auth);
    }

    private void editEmailPhone(boolean isEmail) {
        Intent intent = new Intent(ProfileActivity.this, ChangeEmailActivity.class);
        intent.putExtra("IS_EMAIL", isEmail);
        startActivity(intent);
    }

    @Override
    public void onLogout(String emailId) {

        alertProgress.alertPositiveOnclick(this, emailId, getString(R.string.logout),getString(R.string.ok), new DialogInterfaceListner() {
            @Override
            public void dialogClick(boolean isClicked) {
               // Utility.setMAnagerWithBID(AddAddressActivity.this,sessionManager);
                Utility.setMAnagerWithBID(ProfileActivity.this,manager);
            }
        });

       /* AccountManager accountManager = AccountManager.get(ProfileActivity.this);
        Utility.removeAccount(accountManager,emailId);*/
    }

    @Override
    public void onError(String message) {
        alertProgress.alertinfo(this,message);
    }

    @Override
    public void setProfileEmail(String email) {
        tvProflEml.setText(email);
    }

    @Override
    public void setProfileFirstName(String name) {
        tieFirstName.setText(name);
    }

    @Override
    public void setProfileLastName(String lastName) { tieLastName.setText(lastName);}

    @Override
    public void setProfileMob(String mobile_no) {
        tvProflMob.setText(mobile_no);
    }

    @Override
    public void setAbout(String about)
    {
            tieAboutMe.setText(about);
    }

    @Override
    public void navToLogin() {

        Utility.setMAnagerWithBID(this,manager);

    }

    @Override
    public void showProgress(String message) {
        //progressBar.setVisibility(View.VISIBLE);
        if (!isFinishing()) {
            dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
            TextView tv_progress = dialogView.findViewById(R.id.tv_progress);
            if ("FETCH".equals(message)) {
                tv_progress.setText(getString(R.string.wait_profile));
            } else if ("CHANGE_PWD".equals(message)) {
                tv_progress.setText(getString(R.string.wait_change_password));
            } else {
                tv_progress.setText(getString(R.string.wait_logout));
            }
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
    public void onSuccess(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProfilePic(String profilePicUrl) {
        if (!TextUtils.isEmpty(profilePicUrl)) {
            manager.setProfilePicUrl(profilePicUrl);

            Glide.with(this)
                    .load(profilePicUrl)
                    .apply(Utility.createGlideOptionCall(this))
                    .into(ivProfilePic);
        }
    }

    @Override
    public void setCountryCode(String countryCode) {
        countrycodeFlag(countryCode);
    }

    private void countrycodeFlag(String country_code)
    {
        String[] arrContryCode=this.getResources().getStringArray(R.array.DialingCountryCode);
        for (String anArrContryCode : arrContryCode) {
            String[] arrDial = anArrContryCode.split(",");
            if (arrDial[0].equals(country_code)) {

                countryCode.setText(country_code);
                String drawableName = "flag_"
                        + arrDial[1].trim().toLowerCase(Locale.getDefault());
                countryFlag.setImageResource(getResId(drawableName));
                break;
            }
        }
    }

    public static int getResId(String drawableName) {
        try {
            Class res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            System.out.println("resource ids****"+drawableId);
            return drawableId;
        } catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
            return 0;
        }
    }

    /**
     * <h1>selectImage</h1>
     * @see HandlePictureEvents
     * This mehtod is used to show the popup where we can select our images.
     */
    private void selectImage() {
        handlePicEvent.openDialog();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isDenine = false;
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isDenine = true;
                    }
                }
                if (isDenine) {
                    Toast.makeText(this, "Permission denied by the user", Toast.LENGTH_SHORT).show();
                } else {
                    selectImage();
                }
                break;
        }
    }

    /**
     * This is an overrided method, got a call, when an activity opens by StartActivityForResult(), and return something back to its calling activity.
     * @param requestCode returning the request code.
     * @param resultCode returning the result code.
     * @param data contains the actual data. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            //result code to check is the result is ok or not
            return;
        }

        switch (requestCode) {

            case Constants.CAMERA_PIC:

                handlePicEvent.startCropImage(handlePicEvent.newFile);
                break;
            case Constants.GALLERY_PIC:
                if(data!=null)
                    handlePicEvent.gallery(data.getData());
                break;
            case Constants.CROP_IMAGE:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path != null)
                {
                    try {
                        File fileExist = new File(path);

                        imageflag=true;
                        handlePicEvent.uploadToAmazon(Constants.Amazonbucket+"/"+ Constants.AmazonProfileFolderName,fileExist, new ImageUploadedAmazon() {
                            @Override
                            public void onSuccessAdded(String image)
                            {
                                Log.d("TAG", "onSuccessAdded: "+image);
                                profilePicUrl = image;
                                Glide.with(ProfileActivity.this)
                                        .load(profilePicUrl)
                                        .apply(Utility.createGlideOptionCall(ProfileActivity.this))
                                        .into(ivProfilePic);
                            }
                            @Override
                            public void onerror(String errormsg)
                            {
                                Log.d("TAG", "onerror: "+errormsg);
                            }

                        });

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    break;
                }
        }
    }

}
