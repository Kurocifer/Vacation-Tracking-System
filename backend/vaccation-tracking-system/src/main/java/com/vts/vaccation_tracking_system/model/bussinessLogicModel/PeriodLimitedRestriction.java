// Vacation time of type X is limited to Y hours per week or month.
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

public class PeriodLimitedRestriction extends Restriction {

    @Override
    public ValidationResult validate(Request request) {
        return null;
    }
}
