package com.vts.vaccation_tracking_system.service.vacationRequestService;

public class VacationValidationResponse {
        private String restrictionDescription;
        private String success;

        public String getRestrictionDescription() {
            return restrictionDescription;
        }

        public void setRestrictionDescription(String restrictionDescription) {
            this.restrictionDescription = restrictionDescription;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }
}
