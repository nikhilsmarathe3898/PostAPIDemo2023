package com.localgenie.Login;

import android.util.Patterns;

/**
 * @author Pramod
 * @since  18/12/2017
 */

public class LoginModel {

    private int loginType; // 1 facebook || 2 google

    int getLoginType()
    {
        return loginType;
    }

    void setLoginType(int loginType)
    {
        this.loginType = loginType;
    }

   boolean emptyUname(final String username) {
       return username == null || "".equals(username);
    }

    boolean validateUserName(final String username) {
        return !(!Patterns.EMAIL_ADDRESS.matcher(username).matches() && Patterns.PHONE.matcher(username).matches()) && !(Patterns.EMAIL_ADDRESS.matcher(username).matches() && !Patterns.PHONE.matcher(username).matches());
    }

    boolean validatePassword(final String password) {
        return password == null || "".equals(password);
    }
}
