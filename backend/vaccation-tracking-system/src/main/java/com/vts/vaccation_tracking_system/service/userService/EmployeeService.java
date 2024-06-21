package com.vts.vaccation_tracking_system.service.userService;

import com.vts.vaccation_tracking_system.api.model.auth.LoginBody;
import com.vts.vaccation_tracking_system.api.model.auth.RegistrationBody;
import com.vts.vaccation_tracking_system.api.model.request.VacationRequestBody;
import com.vts.vaccation_tracking_system.api.model.request.ValidateVacationRequestBody;
import com.vts.vaccation_tracking_system.exception.*;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.EmployeeVerificationToken;
import com.vts.vaccation_tracking_system.model.Request;
import com.vts.vaccation_tracking_system.model.bussinessLogicModel.*;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.EmployeeVerificationTokenDAO;
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
public class EmployeeService {

    private EmployeeDAO employeeDAO;
    private JWTService jwtService;
    private EncryptionService encryptionService;
    private EmailService emailService;
    private EmployeeVerificationTokenDAO employeeVerificationTokenDAO;
    private final RequestDAO requestDAO;


    public EmployeeService(EmployeeDAO employeeDAO, JWTService jwtService, EncryptionService encryptionService, EmailService emailService, EmployeeVerificationTokenDAO employeeVerificationTokenDAO, RequestDAO requestDAO) {
        this.employeeDAO = employeeDAO;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
        this.employeeVerificationTokenDAO = employeeVerificationTokenDAO;
        this.requestDAO = requestDAO;
    }

    public void register(RegistrationBody registrationBody, String username) throws UserAlreadyExistsException, EmailFailureException {

        if (employeeDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        Employee employee = new Employee();

        employee.setFirstName(registrationBody.getFirstName());
        employee.setLastName(registrationBody.getLastName());
        employee.setEmail(registrationBody.getEmail());
        employee.setUsername(username);

        String password = registrationBody.getPassword();
        employee.setPassword(encryptionService.encryptPassword(password));
        employeeDAO.save(employee);

        EmployeeVerificationToken verificationToken = createVerificationToken(employee);
        System.out.println("employee service: " + verificationToken.getToken());
        emailService.sendVerificationEmail(verificationToken);
        employeeVerificationTokenDAO.save(verificationToken);
        System.out.println(employee);

        employeeDAO.save(employee);
    }

    public String login(LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException {
        Optional<Employee> opEmployee = employeeDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if (opEmployee.isPresent()) {
            Employee employee = opEmployee.get();
            if (encryptionService.checkPassword(loginBody.getPassword(), employee.getPassword())) {
                if (employee.isEmailVerified()) {
                    return jwtService.generateJWT(employee);
                } else {
                    List<EmployeeVerificationToken> verificationTokens = employee.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimeStamp()
                                    .before(new Timestamp(System.currentTimeMillis() - (60 * 6 * 1000)));

                    if (resend) {
                        EmployeeVerificationToken verificationToken = createVerificationToken(employee);
                        emailService.sendVerificationEmail(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                        employeeVerificationTokenDAO.save(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    private EmployeeVerificationToken createVerificationToken(Employee employee) {
        EmployeeVerificationToken verificationToken = new EmployeeVerificationToken();

        verificationToken.setToken(jwtService.generateVerificationJWT(employee));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setEmployee(employee);

        employee.getVerificationTokens().add(verificationToken);

        return verificationToken;
    }

    public boolean verifyUser(String token) {
        Optional<EmployeeVerificationToken> optionalToken = employeeVerificationTokenDAO.findByToken(token);

        if (optionalToken.isPresent()) {
            EmployeeVerificationToken verificationToken = optionalToken.get();
            Employee employee = (Employee) verificationToken.getUser();

            if (!employee.isEmailVerified()) {
                employee.setEmailVerified(true);
                employeeDAO.save(employee);
                employeeVerificationTokenDAO.deleteByEmployee(employee);
                return true;
            }
        }
        return false;
    }

    public Employee findEmployee(String username) {
        Optional<Employee> optionalEmployee = employeeDAO.findByUsernameIgnoreCase(username);

        return optionalEmployee.orElse(null);
    }
}
