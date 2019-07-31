package com.localgenie.forgotpassword;

/**
 * @author Pramod
 * @since 19/12/17.
 */

public interface ForgotPwdPresenter {

    boolean validateEmail(String email);

    boolean validatePhone(String email);

    void forgotPassword(String emailOrPhone, String countryCode, int type);

}
