package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

import java.util.List;

public class AdjacentDayRestriction extends Restriction {

    private List<String> errorMessages;
    @Override
    public ValidationResult validate(Request request) {
        ValidationResult validationResult = new ValidationResult();

        validationResult.setValidated(false);
        validationResult.setErrorMessages(errorMessages);

        return validationResult;
    }
}
