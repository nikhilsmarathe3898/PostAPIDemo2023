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

package com.localgenie.signup;

import android.content.Intent;

public interface SignUpView {
    void showProgress(String text);

    void hideProgress();

    void  setFirstNameError();

    void  setLastNameError();

    void setEmailError();

    void setPasswordError();

    void setMobileError();

    void setEmailInvalid(String error);

    //  void setEmailErrorMsg(String message);

    void setMobileErrorMsg(String message);

    void setMobileInvalid();

    void setPasswordInvalid();

    void onError(String msg);

    void navigateToLogin();

    void setFieldsFromFB(String first_name, String last_name, String email, String phone, String profilePic);

    /**
     * <h2>openGoogleActivity</h2>
     * This method is used to open the google login activity.
     * @param intent Intent to open the google
     */
    void openGoogleActivity(Intent intent);

    void onReferral(String trim);

    void onLoginSuccess(String auth, String email, String password);

    void showPro();

    void onBusinessNameIsEmpty();

    void clearError(int i);
}
