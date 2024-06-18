package com.vts.vaccation_tracking_system.service.vacationRequestService;

import com.vts.vaccation_tracking_system.api.model.request.VacationRequestBody;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.Grant;
import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.dao.GrantDAO;
import com.vts.vaccation_tracking_system.model.dao.RequestDAO;
import com.vts.vaccation_tracking_system.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacationRequestService {
    private final GrantDAO grantDAO;
    private final RequestDAO requestDAO;
    private final EmailService emailService;

    public VacationRequestService(GrantDAO grantDAO, RequestDAO requestDAO, EmailService emailService) {
        this.grantDAO = grantDAO;
        this.requestDAO = requestDAO;
        this.emailService = emailService;
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
            emailService.sendVacationRequestNotificationEmailToManager(employee);

        }
        return false;
    }


}
