// Vacation time may not be granted on these dates: X
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DateExclusionRestriction extends Restriction {
    private List<String> errorMessages;

    @Override
    public ValidationResult validate(Request request) {
        ValidationResult validationResult = new ValidationResult();

        if(checkForLegibility(request)) {
            validationResult.setValidated(true);
            validationResult.vacationValidationResponse.setSuccess("true");
        }

        validationResult.setValidated(false);
        validationResult.vacationValidationResponse.setRestrictionDescription("false");
        validationResult.vacationValidationResponse.setRestrictionDescription("Date exclusion restriction");
        validationResult.setErrorMessages(errorMessages);

        return validationResult;
    }

    boolean checkForLegibility(Request request) {
        // checks how legit employee is to take the requested vacation time based on this.
        // Some logic for checking this restriction
        return true;
    }
}
