package com.localgenie.Login;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * @author  Pramod
 * @since on 15/12/17.
 */

public interface LoginPresenter {

    /**
     * <h2>validateCredentials</h2>
     * This method is used to validate the credentials for login
     * @param username Username of the user, email id or phone number
     * @param password Password of the user
     */
    void validateCredentials(String username, String password);

    /**
     * <h2>validateUname</h2>
     * This method is used to validate the User name
     * @param emailOrPhone Username of the user, email id or phone number
     */
    void validateUname(String emailOrPhone);

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
     * <h2>normalLogin</h2>
     * This method will call, when user click on Login button and
     * <p>
     *     this method will make a call to CallLoginService() method located in Login Model class.
     * </p>
     * @param emailOrPhone contains the email id or phone.
     * @param password contain the password.
     */
    void normalLogin(String emailOrPhone, String password);


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

    /**
     * <h2>doLogin</h2>
     * This method is used to perform login
     * @param username Email/Mobile number of the user.
     * @param password Password of the user.
     */
    void doLogin(String username, String password);
}
