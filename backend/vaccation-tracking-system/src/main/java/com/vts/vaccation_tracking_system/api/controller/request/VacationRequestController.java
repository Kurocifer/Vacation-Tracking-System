package com.vts.vaccation_tracking_system.api.controller.request;

import com.vts.vaccation_tracking_system.api.model.request.RequestVacationResponseBody;
import com.vts.vaccation_tracking_system.api.model.request.VacationRequestBody;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.service.vacationRequestService.VacationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request-vacation")
public class VacationRequestController {

    private final VacationRequestService vacationRequestService;

    public VacationRequestController(VacationRequestService vacationRequestService) {
        this.vacationRequestService = vacationRequestService;
    }

    @PostMapping
    public ResponseEntity<RequestVacationResponseBody> requestVacation(
            @AuthenticationPrincipal Employee employee, @RequestBody VacationRequestBody vacationRequestBody) {

        RequestVacationResponseBody response = new RequestVacationResponseBody();

        if(vacationRequestService.checkVacationRequest(employee, vacationRequestBody)) {
            response.setSuccess(true);
            response.setResponseComment("Your request has been sent to your responsible manager(s) for approval.\n" +
                    "You will receive a feedback as soon as they attend to your request.");

            return ResponseEntity.ok(response);

        }

        response.setSuccess(false);
        response.setResponseComment("Your vacation request dates, do not abide to the chosen grant");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
