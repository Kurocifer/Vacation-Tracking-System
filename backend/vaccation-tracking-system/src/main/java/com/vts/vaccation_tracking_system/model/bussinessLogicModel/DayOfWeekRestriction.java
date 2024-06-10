// Vacation time of this type is limited to certain days of the week: {M, T, W,
//Th, F, Sat, Sun}.
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

public class DayOfWeekRestriction extends Restriction {
    @Override
    public ValidationResult validate(Request request) {
        return null;
    }
}
