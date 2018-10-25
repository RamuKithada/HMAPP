package com.m.hyderabadmarketingapp.autoreadotp;

public interface Common {
    interface OTPListener {
        void onOTPReceived(String otp);
    }
}