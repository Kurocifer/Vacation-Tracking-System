package com.vts.vaccation_tracking_system.service.userService;

import com.vts.vaccation_tracking_system.api.model.auth.LoginBody;
import com.vts.vaccation_tracking_system.api.model.auth.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.EmailFailureException;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.exception.UserNotVerifiedException;
import com.vts.vaccation_tracking_system.model.dao.ClerkDAO;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.ManagerDAO;
import com.vts.vaccation_tracking_system.service.EncryptionService;
import com.vts.vaccation_tracking_system.service.JWTService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserService {

    private EmployeeDAO employeeDAO;
    private ManagerDAO managerDAO;
    private ClerkDAO clerkDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmployeeService employeeService;
    private ClerkService clerkService;
    private ManagerService managerService;

    private final int LENGTH_OF_SECOND_NAME_IN_USERNAME = 2;
    public UserService(EmployeeDAO employeeDAO, ManagerDAO managerDAO, ClerkDAO clerkDAO,
                       EncryptionService encryptionService, JWTService jwtService,
                       EmployeeService employeeService, ClerkService clerkService, ManagerService managerService) {
        this.employeeDAO = employeeDAO;
        this.managerDAO = managerDAO;
        this.clerkDAO = clerkDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.employeeService = employeeService;
        this.clerkService = clerkService;
        this.managerService = managerService;
    }


    public void registerUser(RegistrationBody registrationBody) throws InvalidUserRoleException, UserAlreadyExistsException, EmailFailureException {

        String username = generateUsername(registrationBody.getFirstName(), registrationBody.getLastName(),
                registrationBody.getUserRole(), LENGTH_OF_SECOND_NAME_IN_USERNAME);

        switch (registrationBody.getUserRole().toUpperCase()) {
            case "EMPLOYEE":
                employeeService.register(registrationBody, username);
                break;

            case "MANAGER":
                managerService.register(registrationBody, username);
                break;

            case "CLERK":
                clerkService.register(registrationBody, username);
                break;

            default:
                throw new InvalidUserRoleException();
        }
    }

    public String loginUser(LoginBody loginBody) throws InvalidUserRoleException, UserNotVerifiedException, EmailFailureException {
        // The first three letters of a user's username is an identifier for their role
        return switch (loginBody.getUsername().substring(0, 3).toUpperCase()) {
            case "EMP" -> employeeService.login(loginBody);
            case "MAN" -> managerService.login(loginBody);
            case "CLE" -> clerkService.login(loginBody);
            default -> throw new InvalidUserRoleException();
        };
    }

    public String generateUsername(String firstName, String lastName, String role, int lastNameLetters) {
        if(firstName == null || lastName == null || lastNameLetters < 0 || lastNameLetters > lastName.length()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String rolePortion = role.substring(0, 3);

        String lastNamePortion = lastName.substring(0, Math.min(lastNameLetters, lastName.length()));

        Long currentTime = (System.currentTimeMillis() / 1000) % 1000;

        int year = (Calendar.getInstance().get(Calendar.YEAR) - 1900) % 100; // get current year (year since 1900), and module by 100 to get two las digits

        return String.format("%s%s%s%d%02d", rolePortion, firstName, lastNamePortion, currentTime, year).toUpperCase();
    }

    @Transactional
    public boolean verifyUser(String token) throws InvalidUserRoleException {
        String username = jwtService.getVerificationUsername(token);

        System.out.println("user service username: " + username);

        return switch (username.substring(0, 3).toUpperCase()) {
            case "EMP" -> employeeService.verifyUser(token);
            case "MAN" -> managerService.verifyUser(token);
            case "CLE" -> clerkService.verifyUser(token);
            default -> throw new IllegalArgumentException("Invalid role");
        };
    }

}
