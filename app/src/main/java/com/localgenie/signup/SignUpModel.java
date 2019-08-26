package com.localgenie.signup;

import android.util.Patterns;

import com.localgenie.utilities.Constants;

/**
 * @author Pramod
 * @since 27-02-2018.
 */

public class SignUpModel {

    private int loginType; // 1 facebook || 2 google

    int getLoginType()
    {
        return loginType;
    }

    void setLoginType(int loginType)
    {
        this.loginType = loginType;
    }

    boolean validateFname(final String firstname) {
        return firstname == null || "".equals(firstname);
    }

    boolean validateLname(final String lastname) {
        return lastname == null || "".equals(lastname);
    }

    boolean emptyEmail(String email) {
        return email == null || email.length() == 0 || "".equals(email);
    }

    boolean validEmail(String email) {
        //Validation for Invalid Email Address
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches() && !Patterns.DOMAIN_NAME.matcher(email).matches();
    }

    boolean emptyPassword(final String password) {
        return password == null || "".equals(password);
    }

    boolean validPassword(final String password) {
        return !Constants.PASSWORD_PATTERN.matcher(password).matches();
    }

    boolean emptyPhone(String phone) {
        return phone == null || phone.length() == 0 || "".equals(phone);
    }

    boolean validPhone(String phone) {
        return !Patterns.PHONE.matcher(phone).matches();
    }

    boolean irregularPhone(String phone, int max) {
        return phone.length() < max;
    }

}
