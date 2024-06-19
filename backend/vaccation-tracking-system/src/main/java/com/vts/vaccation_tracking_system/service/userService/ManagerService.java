package com.vts.vaccation_tracking_system.service.userService;

import com.vts.vaccation_tracking_system.api.model.auth.LoginBody;
import com.vts.vaccation_tracking_system.api.model.auth.RegistrationBody;
import com.vts.vaccation_tracking_system.api.model.request.ValidateVacationRequestBody;
import com.vts.vaccation_tracking_system.exception.EmailFailureException;
import com.vts.vaccation_tracking_system.exception.InvalidVacationRequestException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.exception.UserNotVerifiedException;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.ManagerVerificationToken;
import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.bussinessLogicModel.*;
import com.vts.vaccation_tracking_system.model.dao.ManagerDAO;
import com.vts.vaccation_tracking_system.model.dao.ManagerVerificationTokenDAO;
import com.vts.vaccation_tracking_system.model.dao.RequestDAO;
import com.vts.vaccation_tracking_system.service.EmailService;
import com.vts.vaccation_tracking_system.service.EncryptionService;
import com.vts.vaccation_tracking_system.service.JWTService;
import com.vts.vaccation_tracking_system.service.vacationRequestService.VacationValidationResponse;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private ManagerDAO managerDAO;
    private JWTService jwtService;
    private EncryptionService encryptionService;
    private EmailService emailService;
    private ManagerVerificationTokenDAO managerVerificationTokenDAO;
    private final RequestDAO requestDAO;
    private final PeriodLimitedRestriction periodLimitedRestriction;
    private final DayOfWeekRestriction dayOfWeekRestriction;
    private final DateExclusionRestriction dateExclusionRestriction;
    private final CoworkerRestriction coworkerRestriction;
    private final ConsecutiveDayRestriction consecutiveDayRestriction;
    private final AdjacentDayRestriction adjacentDayRestriction;

    public ManagerService(ManagerDAO managerDAO, JWTService jwtService, EncryptionService encryptionService, EmailService emailService, ManagerVerificationTokenDAO managerVerificationTokenDAO, RequestDAO requestDAO, PeriodLimitedRestriction periodLimitedRestriction, DayOfWeekRestriction dayOfWeekRestriction, DateExclusionRestriction dateExclusionRestriction, CoworkerRestriction coworkerRestriction, ConsecutiveDayRestriction consecutiveDayRestriction, AdjacentDayRestriction adjacentDayRestriction) {
        this.managerDAO = managerDAO;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
        this.managerVerificationTokenDAO = managerVerificationTokenDAO;
        this.requestDAO = requestDAO;
        this.periodLimitedRestriction = periodLimitedRestriction;
        this.dayOfWeekRestriction = dayOfWeekRestriction;
        this.dateExclusionRestriction = dateExclusionRestriction;
        this.coworkerRestriction = coworkerRestriction;
        this.consecutiveDayRestriction = consecutiveDayRestriction;
        this.adjacentDayRestriction = adjacentDayRestriction;
    }


    public void register(RegistrationBody registrationBody, String username) throws UserAlreadyExistsException, EmailFailureException {

        if(managerDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        Manager manager = new Manager();

        manager.setFirstName(registrationBody.getFirstName());
        manager.setLastName(registrationBody.getLastName());
        manager.setEmail(registrationBody.getEmail());
        manager.setUsername(username);

        String password = registrationBody.getPassword();
        manager.setPassword(encryptionService.encryptPassword(password));
        managerDAO.save(manager);

        ManagerVerificationToken verificationToken = createVerificationToken(manager);
        emailService.sendVerificationEmail(verificationToken);
        managerVerificationTokenDAO.save(verificationToken);
        System.out.println(manager);

        managerDAO.save(manager);
    }

    public String login(LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException {
        Optional<Manager> opManger = managerDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if(opManger.isPresent()) {
            Manager manager = opManger.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), manager.getPassword())) {
                if(manager.isEmailVerified()) {
                    return jwtService.generateJWT(manager);
                } else {
                    List<ManagerVerificationToken> verificationTokens = manager.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimeStamp()
                                    .before(new Timestamp(System.currentTimeMillis() - (60 * 6 * 1000)));

                    if(resend) {
                        ManagerVerificationToken verificationToken = createVerificationToken(manager);
                        emailService.sendVerificationEmail(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                        managerVerificationTokenDAO.save(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    private ManagerVerificationToken createVerificationToken(Manager manager) {
        ManagerVerificationToken verificationToken = new ManagerVerificationToken();

        verificationToken.setToken(jwtService.generateVerificationJWT(manager));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setManager(manager);

        manager.getVerificationTokens().add(verificationToken);

        return verificationToken;
    }

    public boolean verifyUser(String token) {
        Optional<ManagerVerificationToken> optionalToken = managerVerificationTokenDAO.findByToken(token);

        if(optionalToken.isPresent()) {
            ManagerVerificationToken verificationToken = optionalToken.get();
            Manager manager = (Manager) verificationToken.getUser();

            if (!manager.isEmailVerified()) {
                manager.setEmailVerified(true);
                managerDAO.save(manager);
                managerVerificationTokenDAO.deleteByManager(manager);
                return true;
            }
        }
        return false;
    }

    public List<VacationValidationResponse> validateVacationRequest(ValidateVacationRequestBody validateVacationRequestBody) throws InvalidVacationRequestException {
        Optional<Request> optionalRequest = requestDAO.findById(validateVacationRequestBody.getRequestId());

        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();

            // check if the request is associated with this employee
            if (requestDAO.existsByManager_UsernameIgnoreCase(validateVacationRequestBody.getUsername())) {
                List<VacationValidationResponse> validationResponses = new ArrayList<>();
                List<ValidationResult> validationResults = new ArrayList<>();

                // I don't know to what extent, but I'm sure this is terrible
                validationResults.add(adjacentDayRestriction.validate(request));
                validationResults.add(periodLimitedRestriction.validate(request));
                validationResults.add(dateExclusionRestriction.validate(request));
                validationResults.add(dayOfWeekRestriction.validate(request));
                validationResults.add(coworkerRestriction.validate(request));
                validationResults.add(consecutiveDayRestriction.validate(request));

                validationResponses = validationResults.stream()
                        .map(ValidationResult::getVacationValidationResponse)
                        .collect(Collectors.toList());

                boolean requestAbidesToAllRestrictions = validationResults.stream()
                        .allMatch(ValidationResult::validated);

                if(requestAbidesToAllRestrictions) {
                    request.setIsValidated(true);
                }

                return validationResponses;
            } else {
                throw new InvalidVacationRequestException();
            }
        }
        return null;
    }
}
