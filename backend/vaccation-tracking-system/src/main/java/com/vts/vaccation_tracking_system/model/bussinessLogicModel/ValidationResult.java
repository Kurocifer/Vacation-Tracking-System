package com.vts.vaccation_tracking_system.model.bussinessLogicModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

public class ValidationResult {
    public boolean validated() {
        return true;
    }

    public List getErrors() {
        return new ArrayList();
    }
}
