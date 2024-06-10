// Vacation time of type X cannot be taken when directly adjacent to a com-//pany or location-specific holiday.
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

import java.util.List;

public class AdjacentDayRestriction extends Restriction {

    private List<String> errorMessages;
    @Override
    public ValidationResult validate(Request request) {
        ValidationResult validationResult = new ValidationResult();

        validationResult.setValidated(checkForLegibility());
        validationResult.setErrorMessages(errorMessages);

        return validationResult;
    }

    boolean checkForLegibility() {
        // checks how legit employee is to take the requested vacation time based on this.
        return false;
    }
}
