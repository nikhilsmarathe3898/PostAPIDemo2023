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

package com.localgenie.profile;

public interface ProfileView {

    void showProgress(String message);

    void hideProgress();

    void setProfileEmail(String email);

    void setProfileFirstName(String firstName);

    void setProfileLastName(String lastName);

    void setProfileMob(String mobile_no);

    void setProfilePic(String profilePicUrl);

    void setCountryCode(String countryCode);

    void setAbout(String about);

    void navToLogin();

    //void editEnable(boolean toggle);

    void onSuccess(String message);

    void onLogout(String emailId);

    void onError(String message);
}
