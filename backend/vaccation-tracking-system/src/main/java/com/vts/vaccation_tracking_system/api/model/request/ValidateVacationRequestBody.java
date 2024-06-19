package com.vts.vaccation_tracking_system.api.model.request;

import com.vts.vaccation_tracking_system.model.Request;

public class ValidateVacationRequestBody {

    private String username;
    private Long requestId;


    public String getUsername() {
        return username;
    }

    public Long getRequestId() {
        return requestId;
    }
}
