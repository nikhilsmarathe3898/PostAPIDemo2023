package com.localgenie.signup;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.localgenie.countrypic.CountryPicker;

/**
 * <h1>SignUpPresenter</h1>
 * <h4>This is an interface for SignUpPresenterImpl</h4>
 * this interface hides the implementation of SignUpPresenterImpl and abstracts them to the view.
 * @author Pramod
 * @since 21-12-2017.
 * @version 1.0
 */


public interface SignUpPresenter {


    /**
     * <h2>validateFname</h2>
     * This method is used to validate the FirstName for signup
     * @param firstName First Name of the user
     */
    void validateFname(String firstName);

    /**
     * <h2>validateLname</h2>
     * This method is used to validate the LastName for signup
     * @param lastName Last Name of the user
     */
    void validateLname(String lastName);
    void validateBusiness(String business);

    /**
     * <h2>validateEmail</h2>
     * This method is used to validate the Email Id for signup
     * @param email Email of the user
     */
    void validateEmail(String flag_show_dialog, String email);

    /**
     * <h2>initializeFacebook</h2>
     * This method is used to initialize the facebook
     */
    void initializeFacebook();


    /**
     * <h2>fbLogin</h2>
     * <P>
     * This method is used for Facebook login.
     * </P>
     * @param callbackManager: facebook call back manager interface
     */
    void handleResultFromFB(CallbackManager callbackManager);


    /**
     * <h2>handleResultFromGoogle</h2>
     * <P>
     * This method is used for Google Login.
     * </P>
     * @param googleSignInResult : Google SignIn result object
     */


    void handleResultFromGoogle(GoogleSignInResult googleSignInResult);

    /**
     * <h2>storeLoginType</h2>
     * This method is used to store the login type
     * @param loginType login type 1 for facebook and 2 for google
     */
    void storeLoginType(int loginType);


    /**
     * <h2>handleLoginType</h2>
     * This method is used to handle the login type
     * @param callbackManager facebook call back manager
     */
    void handleLoginType(CallbackManager callbackManager);

    /**
     * <h2>initializeFBGoogle</h2>
     * This method is used to initialize FB and google SDK
     */
    void initializeFBGoogle();


    /**
     * <h2>googleLogin</h2>
     * This method is used to login via google into the System
     */
    void googleLogin();

    void validatePhone(String flag_show_dialog, String countryCode, String phone);

    /**
     * <h2>validatePassword</h2>
     * This method is used to validate the password for signup
     * @param password Email of the user
     */
    void validatePassword(String password);

    boolean irregularPhone(CountryPicker mCountryPicker, Context context, String phone);

    void emailAlreadyExists(String flag_show_dialog, String email);

    void phoneAlreadyExists(String flag_show_dialog, String countryCode, String phone);

    void doRegister(String profilePic, String firstName, String lastName, String email, String password, String countryCode, String phone, String referralCode);

    boolean validatePhoneNumber(String countryCode, String mobileNumber);

    void setTermsCondition(TextView tv_termsclick, String privacyTerms);

    void setOnclickHighlighted(TextView tv_termsclick, String privacyPolicy, View.OnClickListener onClickListener);

    void checkReferralCode(String trim);

    void storeFbId(String facebookId);

    void storeGoogleId(String googleId);
}
