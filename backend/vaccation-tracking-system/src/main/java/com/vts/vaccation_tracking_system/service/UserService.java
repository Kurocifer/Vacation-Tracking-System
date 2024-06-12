package com.vts.vaccation_tracking_system.service;

import com.vts.vaccation_tracking_system.api.model.LoginBody;
import com.vts.vaccation_tracking_system.api.model.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.HRClerk;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.dao.ClerkDAO;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.ManagerDAO;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
public class UserService {

    private EmployeeDAO employeeDAO;
    private ManagerDAO managerDAO;
    private ClerkDAO clerkDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    public UserService(EmployeeDAO employeeDAO, ManagerDAO managerDAO, ClerkDAO clerkDAO, EncryptionService encryptionService, JWTService jwtService) {
        this.employeeDAO = employeeDAO;
        this.managerDAO = managerDAO;
        this.clerkDAO = clerkDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }


    public void registerUser(RegistrationBody registrationBody) throws InvalidUserRoleException, UserAlreadyExistsException {


        switch (registrationBody.getUserRole().toUpperCase()) {
            case "EMPLOYEE":
                registerEmployee(registrationBody);
                break;

            case "MANAGER":
                registerManager(registrationBody);
                break;

            case "CLERK":
                registerClerk(registrationBody);
                break;

            default:
                throw new InvalidUserRoleException();
        }
    }

    public String loginUser(LoginBody loginBody) throws InvalidUserRoleException {
        return switch (loginBody.getUserRole().toUpperCase()) {
            case "EMPLOYEE" -> loginEmployee(loginBody);
            case "MANAGER" -> loginManager(loginBody);
            case "CLERK" -> loginClerk(loginBody);
            default -> throw new InvalidUserRoleException();
        };
    }

    private String generateUsername(String firstName, String lastName, String role, int lastNameLetters) {
        if(firstName == null || lastName == null || lastNameLetters < 0 || lastNameLetters > lastName.length()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String rolePortion = role.substring(0, 3);

        String lastNamePortion = lastName.substring(0, Math.min(lastNameLetters, lastName.length()));

        Long currentTime = (System.currentTimeMillis() / 1000) % 1000;

        int year = (Calendar.getInstance().get(Calendar.YEAR) - 1900) % 100; // get current year (year since 1900), and module by 100 to get two las digits

        return String.format("%s%s%s%d%02d", rolePortion, firstName, lastNamePortion, currentTime, year).toUpperCase();
    }

    private void registerEmployee(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if(employeeDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        Employee employee = new Employee();

        employee.setFirstName(registrationBody.getFirstName());
        employee.setLastName(registrationBody.getLastName());
        employee.setEmail(registrationBody.getEmail());
        employee.setUsername(generateUsername(registrationBody.getFirstName(),
                registrationBody.getLastName(), registrationBody.getUserRole(), 2));

        String password = registrationBody.getPassword();
        employee.setPassword(encryptionService.encryptPassword(password));

        System.out.println(employee);

        employeeDAO.save(employee);
    }

    private void registerManager(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if(managerDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        Manager manager = new Manager();

        manager.setFirstName(registrationBody.getFirstName());
        manager.setLastName(registrationBody.getLastName());
        manager.setEmail(registrationBody.getEmail());
        manager.setUsername(generateUsername(registrationBody.getFirstName(),
                registrationBody.getLastName(), registrationBody.getUserRole(), 2));

        String password = registrationBody.getPassword();
        manager.setPassword(encryptionService.encryptPassword(password));

        System.out.println(manager);

        managerDAO.save(manager);
    }

    private void registerClerk(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if(clerkDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        HRClerk clerk = new HRClerk();

        clerk.setFirstName(registrationBody.getFirstName());
        clerk.setLastName(registrationBody.getLastName());
        clerk.setEmail(registrationBody.getEmail());
        clerk.setUsername(generateUsername(registrationBody.getFirstName(),
                registrationBody.getLastName(), registrationBody.getUserRole(), 2));

        String password = registrationBody.getPassword();
        clerk.setPassword(encryptionService.encryptPassword(password));

        System.out.println(clerk);

        clerkDAO.save(clerk);
    }

    private String loginEmployee(LoginBody loginBody) {
        System.out.println(loginBody.getUserRole());
        Optional<Employee> opEmployee = employeeDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if(opEmployee.isPresent()) {
            Employee employee = opEmployee.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), employee.getPassword())) {
                return jwtService.generateJWT(employee);
            }
        }

        return null;
    }

    private String loginManager(LoginBody loginBody) {
        System.out.println(loginBody.getUserRole());

        Optional<Manager> opManger = managerDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if(opManger.isPresent()) {
            Manager manager = opManger.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), manager.getPassword())) {
                return jwtService.generateJWT(manager);
            }
        }

        return null;
    }

    private String loginClerk(LoginBody loginBody) {
        System.out.println(loginBody.getUserRole());

        Optional<HRClerk> opClerk = clerkDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if(opClerk.isPresent()) {
            HRClerk clerk = opClerk.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), clerk.getPassword())) {
                return jwtService.generateJWT(clerk);
            }
        }

        return null;
    }

}
