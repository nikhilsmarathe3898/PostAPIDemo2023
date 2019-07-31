package com.localgenie.profile;

/**
 * @author Pramod
 * @since  12-01-2018.
 */

public interface ProfilePresenter {

    void getProfile(String auth);

    void editProfile(String auth, String profile_pic, String first_name, String last_name, String about_me);

    void doLogout(String auth);
}
