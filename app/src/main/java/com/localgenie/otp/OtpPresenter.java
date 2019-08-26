package com.localgenie.otp;

/**
 * Created by Pramod on 15/12/17.
 */

public interface OtpPresenter {

    void verifyOtp(String otp, String sid, String flag);

    void verifyPhone(String otp, String sid, String flag);

    void resendOtp(String otp, String userId, int trigger, boolean resend_flag);
}
