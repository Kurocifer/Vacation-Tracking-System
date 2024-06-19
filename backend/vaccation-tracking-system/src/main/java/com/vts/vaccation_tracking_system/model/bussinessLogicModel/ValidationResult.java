package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.service.vacationRequestService.VacationValidationResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

public class ValidationResult {

    private boolean isValidated = false;
    private List<String> errorMessages = new ArrayList<>();

    public VacationValidationResponse getVacationValidationResponse() {
        return vacationValidationResponse;
    }

    public VacationValidationResponse vacationValidationResponse = new VacationValidationResponse();

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public boolean validated() {
        return isValidated;
    }

    public List<String> getErrors() {
        return errorMessages;
    }
}
