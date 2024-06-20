package com.vts.vaccation_tracking_system.service.vacationRequestService;

import com.vts.vaccation_tracking_system.api.model.request.VacationRequestBody;
import com.vts.vaccation_tracking_system.api.model.request.ValidateVacationRequestBody;
import com.vts.vaccation_tracking_system.exception.*;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.Grant;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.bussinessLogicModel.*;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.GrantDAO;
import com.vts.vaccation_tracking_system.model.dao.RequestDAO;
import com.vts.vaccation_tracking_system.service.EmailService;
import com.vts.vaccation_tracking_system.service.userService.EmployeeService;
import com.vts.vaccation_tracking_system.service.userService.ManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VacationRequestService {

    @Value("${app.frontend.url}")
    private String url;

    private final GrantDAO grantDAO;
    private final RequestDAO requestDAO;
    private final EmailService emailService;
    private final EmployeeService employeeService;
    private final ManagerService managerService;
    private final PeriodLimitedRestriction periodLimitedRestriction;
    private final DayOfWeekRestriction dayOfWeekRestriction;
    private final DateExclusionRestriction dateExclusionRestriction;
    private final CoworkerRestriction coworkerRestriction;
    private final ConsecutiveDayRestriction consecutiveDayRestriction;
    private final AdjacentDayRestriction adjacentDayRestriction;
    public VacationRequestService(GrantDAO grantDAO, RequestDAO requestDAO, EmailService emailService, EmployeeService employeeService, ManagerService managerService, PeriodLimitedRestriction periodLimitedRestriction, DayOfWeekRestriction dayOfWeekRestriction, DateExclusionRestriction dateExclusionRestriction, CoworkerRestriction coworkerRestriction, ConsecutiveDayRestriction consecutiveDayRestriction, AdjacentDayRestriction adjacentDayRestriction) {
        this.grantDAO = grantDAO;
        this.requestDAO = requestDAO;
        this.emailService = emailService;
        this.employeeService = employeeService;
        this.managerService = managerService;
        this.periodLimitedRestriction = periodLimitedRestriction;
        this.dayOfWeekRestriction = dayOfWeekRestriction;
        this.dateExclusionRestriction = dateExclusionRestriction;
        this.coworkerRestriction = coworkerRestriction;
        this.consecutiveDayRestriction = consecutiveDayRestriction;
        this.adjacentDayRestriction = adjacentDayRestriction;
    }

    public boolean checkVacationRequest(
            Employee employee, VacationRequestBody vacationRequestBody) {

        List<Grant> grants = grantDAO.findByEmployee(employee);

        Optional<Grant> optionalGrant = grants.stream()
                .filter(grant -> grant.getId().equals(vacationRequestBody.getGrantId()))
                .findFirst();

        if(optionalGrant.isPresent()) {
            Grant grant = optionalGrant.get();

            Request request = new Request();

            request.setTitle(vacationRequestBody.getTitle());
            request.setComments(vacationRequestBody.getComments());
            request.setStartDate(vacationRequestBody.getStartDate());
            request.setEndDate(vacationRequestBody.getEndDate());
            request.setGrant(grant);
            request.setEmployee(employee);

            requestDAO.save(request);

            try {
                emailService.sendVacationRequestNotificationEmailToManager(employee);
            } catch (EmployeeDoesNotExistException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        return false;
    }

    public List<VacationValidationResponse> validateVacationRequest(
            ValidateVacationRequestBody validateVacationRequestBody, Manager manager) throws InvalidUserRoleException, InvalidVacationRequestException, UnauthorisedOperationException {

        // check if the user in the current session is a manager
        if(manager.getUsername().substring(0, 3).equalsIgnoreCase("MAN")) {
            Optional<Request> optionalRequest = requestDAO.findById(validateVacationRequestBody.getRequestId());

            if (optionalRequest.isPresent()) {
                Request request = optionalRequest.get();

                // check if the request is associated with this employee
                if (requestDAO.existsByEmployee_UsernameIgnoreCase(validateVacationRequestBody.getUsername())) {
                    List<ValidationResult> validationResults = new ArrayList<>();

                    // I don't know to what extent, but I'm sure this is terrible
                    validationResults.add(adjacentDayRestriction.validate(request));
                    validationResults.add(periodLimitedRestriction.validate(request));
                    validationResults.add(dateExclusionRestriction.validate(request));
                    validationResults.add(dayOfWeekRestriction.validate(request));
                    validationResults.add(coworkerRestriction.validate(request));
                    validationResults.add(consecutiveDayRestriction.validate(request));

                    List<VacationValidationResponse> validationResponses = validationResults.stream()
                            .map(ValidationResult::getVacationValidationResponse)
                            .collect(Collectors.toList());

                    boolean requestAbidesToAllRestrictions = validationResults.stream()
                            .allMatch(ValidationResult::validated);

                    if(requestAbidesToAllRestrictions) {
                        // update the status of this user's request in the database, and email them
                        requestDAO.updateIsValidatedById(true, request.getId());
                        try {
                            emailService.sendEmail(request.getEmployee().getEmail(), "Vacation Request Response",
                                    "Your vacation request has been validated. Enjoy it");
                        } catch (EmailFailureException ignored) {
                        }
                    }

                    return validationResponses;
                } else {
                    throw new InvalidVacationRequestException();
                }
            }
        }
        return null;
    }

    public void rejectVacationRequest(ValidateVacationRequestBody validateVacationRequestBody, Manager manager) throws UnauthorisedOperationException {
        if(manager.getUsername().substring(0, 3).equalsIgnoreCase("MAN")) {
            Optional<Request> optionalRequest = requestDAO.findById(validateVacationRequestBody.getRequestId());

            if(optionalRequest.isPresent()) {
                Request request = optionalRequest.get();
                requestDAO.updateIsValidatedById(false, request.getId());

                try {
                    emailService.sendEmail(request.getEmployee().getEmail(), "Vacation Request Response",
                            "Your vacation request was rejected.\n" +
                            " Check the site for more details " + url);
                } catch (EmailFailureException ignored) {
                }
            }
        } else throw new UnauthorisedOperationException();
    }
}

