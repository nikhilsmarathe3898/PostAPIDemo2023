package com.localgenie.changepassword;

import com.localgenie.utilities.Constants;

/**
 * Created by ${3Embed} on 4/11/17.
 */

public class ChangePwdModel {

    public boolean emptyPwd(String pwd) {
        if(pwd == null || pwd.length()==0 || "".equals(pwd.trim())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validatePwd(String pwd) {
        if (!Constants.PASSWORD_PATTERN.matcher(pwd).matches()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validatePassword(final String password) {
        return password == null || "".equals(password) || !Constants.PASSWORD_PATTERN.matcher(password).matches();
    }

}
