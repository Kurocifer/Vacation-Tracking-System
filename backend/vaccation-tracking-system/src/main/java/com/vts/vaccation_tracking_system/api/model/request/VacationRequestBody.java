package com.vts.vaccation_tracking_system.api.model.request;

import com.vts.vaccation_tracking_system.model.Grant;

import java.time.LocalDate;

public class VacationRequestBody {
    private String title;
    private String comments;
    private LocalDate startDate;
    private LocalDate endDate;

    private int grantId;

    public String getTitle() {
        return title;
    }


    public String getComments() {
        return comments;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getGrantId() {
        return grantId;
    }
}
