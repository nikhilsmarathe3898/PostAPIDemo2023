package com.localgenie.change_email;

/**
 * @author Pramod
 * @since 20/01/2018.
 */

public interface ChangeEmailPresenter {

    boolean validateEmail(String email);

    boolean validatePhone(String phone);

    /**
     * <h2>changeEmail</h2>
     *     <p>This method is used to change the email id of the user.
     *     API CALL for changing the email address of a user.</p>
     * @param emailId emailId to be changed.
     */
    void changeEmail(String emailId);


    /**
     * <h2>changeMobile</h2>
     *     <p>This method is used to change the mobile number of the user.
     *     API CALL for changing the mobile number of a user.</p>
     * @param countryCode countryCode of mobile number to be changed.
     * @param mobile mobile number to be changed.
     */
    void changeMobile(String countryCode, String mobile);

}
