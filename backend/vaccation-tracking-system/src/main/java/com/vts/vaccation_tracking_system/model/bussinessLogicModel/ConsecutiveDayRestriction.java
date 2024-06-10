// An employee can’t take more than X consecutive days of leave for Y type of
//grant.
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

public class ConsecutiveDayRestriction extends Restriction {
    @Override
    public ValidationResult validate(Request request) {
        return null;
    }
}
