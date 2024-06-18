package com.vts.vaccation_tracking_system.api.model.request;

public class RequestVacationResponseBody {
    private boolean success;
    private String responseComment;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponseComment() {
        return responseComment;
    }

    public void setResponseComment(String responseComment) {
        this.responseComment = responseComment;
    }
}
