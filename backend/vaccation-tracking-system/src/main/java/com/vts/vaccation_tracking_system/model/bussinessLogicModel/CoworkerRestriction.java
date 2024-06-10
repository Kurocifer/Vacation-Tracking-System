//Vacation time may not be granted when there are only X number of employ-
//ees scheduled to work from the list Y of employees.
package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.Restriction;

public class CoworkerRestriction extends Restriction {
    @Override
    public ValidationResult validate(Request request) {
        return null;
    }
}
