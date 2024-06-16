package com.vts.vaccation_tracking_system.exception;

public class UserNotVerifiedException extends Exception {
    private boolean newEmailSent;


    public UserNotVerifiedException(boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }

    public boolean isNewEmailSent() {
        return newEmailSent;
    }
}
