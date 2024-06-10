// Vacation time may not be granted on these dates: X
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

public class DateExclusionRestriction extends Restriction {
    @Override
    public ValidationResult validate(Request request) {
        return null;
    }
}
