package com.localgenie.changepassword;

/**
 * <h>ChangePwdPresenter</h>
 * Created by Pramod on 19/12/17.
 */

public interface ChangePwdPresenter {

    void changePassword(String sid, String new_password);

    void profChangePassword(String auth, String old_password, String new_password);

}
