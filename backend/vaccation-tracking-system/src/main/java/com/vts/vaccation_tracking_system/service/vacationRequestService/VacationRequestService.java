package com.vts.vaccation_tracking_system.service.vacationRequestService;

import com.vts.vaccation_tracking_system.api.model.request.VacationRequestBody;
import com.vts.vaccation_tracking_system.api.model.request.ValidateVacationRequestBody;
import com.vts.vaccation_tracking_system.exception.EmployeeDoesNotExistException;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.exception.InvalidVacationRequestException;
import com.vts.vaccation_tracking_system.exception.UnauthorisedOperationException;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.Grant;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.GrantDAO;
import com.vts.vaccation_tracking_system.model.dao.RequestDAO;
import com.vts.vaccation_tracking_system.service.EmailService;
import com.vts.vaccation_tracking_system.service.userService.EmployeeService;
import com.vts.vaccation_tracking_system.service.userService.ManagerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacationRequestService {
    private final GrantDAO grantDAO;
    private final RequestDAO requestDAO;
    private final EmailService emailService;
    private final EmployeeService employeeService;
    private final ManagerService managerService;
    public VacationRequestService(GrantDAO grantDAO, RequestDAO requestDAO, EmailService emailService, EmployeeService employeeService, ManagerService managerService) {
        this.grantDAO = grantDAO;
        this.requestDAO = requestDAO;
        this.emailService = emailService;
        this.employeeService = employeeService;
        this.managerService = managerService;
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

        if(!manager.getUsername().substring(0, 3).equalsIgnoreCase("MAN")) {
            throw new UnauthorisedOperationException();
        } else {
            return switch (validateVacationRequestBody.getUsername().substring(0, 3).toUpperCase()) {
                case "EMP" -> employeeService.validateVacationRequest(validateVacationRequestBody);
                case "MAN" -> managerService.validateVacationRequest(validateVacationRequestBody);
                default -> throw new InvalidUserRoleException();
            };
        }
    }
}

