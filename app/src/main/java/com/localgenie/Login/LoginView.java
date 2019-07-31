/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.localgenie.Login;

import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.localgenie.model.UserDetailsDataModel;

public interface LoginView {

    /**
     * <h2>showProgress</h2>
     * This method is used to show progress bar on call of APIS
     */
    void showProgress();

    /**
     * <h2>hideProgress</h2>
     * This method is used to hide progress bar after the call of API success
     */
    void hideProgress();

    /**
     * <h2>setUsernameError</h2>
     * This method is used to show the error message on empty username
     */
    void setUsernameError();

    /**
     * <h2>setInvalidUname</h2>
     * This method is used to show the error message on invalid username
     */
    void setInvalidUname();

    /**
     * <h2>setPasswordError</h2>
     * This method is used to show the error message on invalid password
     */
    void setPasswordError();

    /**
     * <h2>onLoginSuccess</h2>
     * This method is used to navigate to the HomeScreen after the success of the Login API CALL,
     * and save the auth token to the Account via AccountManager of ANDROID
     * @param auth Auth token to store in Account
     * @param username Username to store in Account
     * @param password Password used to store in Account via AccountManager
     */
    void onLoginSuccess(String auth, String username, String password);

    /**
     * <h2>onError</h2>
     * This method is used to show the error message if the Login API call fails and gives error from server
     * @param msg Message to show via TOAST
     */
    void onError(String msg);

    /**
     * <h2>openGoogleActivity</h2>
     * This method is used to open the google login activity.
     * @param intent Intent to open the google
     */
    void openGoogleActivity(Intent intent);

    /**
    * <h2>revokeGuestLogin</h2>
    * This method is used to revoke the guestLogin and remove the account from account manager.
     * @param emailId deviceId used for GuestLogin - this would be device_id preferably.
    */
    void revokeGuestLogin(String emailId);

    void navToSignUp(Integer loginType, UserDetailsDataModel userDetailsDataModel, GoogleApiClient mGoogleApiClient);

    void setEmailView(String username);

    void googleClientIsNotConnected();

}
