package com.vts.vaccation_tracking_system.service;

import com.vts.vaccation_tracking_system.api.model.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.HRClerk;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.dao.ClerkDAO;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.ManagerDAO;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserService {

    private EmployeeDAO employeeDAO;
    private ManagerDAO managerDAO;
    private ClerkDAO clerkDAO;

    public UserService(EmployeeDAO employeeDAO, ManagerDAO managerDAO, ClerkDAO clerkDAO) {
        this.employeeDAO = employeeDAO;
        this.managerDAO = managerDAO;
        this.clerkDAO = clerkDAO;
    }


    public void registerUser(RegistrationBody registrationBody) throws InvalidUserRoleException {

        System.out.println(generateUsername(registrationBody.getFirstName(), registrationBody.getLastName(), 2));
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

    private void registerEmployee(RegistrationBody registrationBody) {
        Employee employee = new Employee();

        employee.setFirstName(registrationBody.getFirstName());
        employee.setLastName(registrationBody.getLastName());
        employee.setEmail(registrationBody.getEmail());
        employee.setUsername(generateUsername(registrationBody.getFirstName(), registrationBody.getLastName(), 2));
        employee.setPassword(registrationBody.getPassword()); // to be encrypted

        System.out.println(employee);

        employeeDAO.save(employee);
    }

    private void registerManager(RegistrationBody registrationBody) {
        Manager manager = new Manager();

        manager.setFirstName(registrationBody.getFirstName());
        manager.setLastName(registrationBody.getLastName());
        manager.setEmail(registrationBody.getEmail());
        manager.setUsername(generateUsername(registrationBody.getFirstName(), registrationBody.getLastName(), 2));
        manager.setPassword(registrationBody.getPassword()); // to be encrypted

        System.out.println(manager);
    }

    private void registerClerk(RegistrationBody registrationBody) {
        HRClerk clerk = new HRClerk();

        clerk.setFirstName(registrationBody.getFirstName());
        clerk.setLastName(registrationBody.getLastName());
        clerk.setEmail(registrationBody.getEmail());
        clerk.setUsername(generateUsername(registrationBody.getFirstName(), registrationBody.getLastName(), 2));
        clerk.setPassword(registrationBody.getPassword()); // to be encrypted

        System.out.println(clerk);
    }



    private String generateUsername(String firstName, String lastName, int lastNameLetters) {
        if(firstName == null || lastName == null || lastNameLetters < 0 || lastNameLetters > lastName.length()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String lastNamePortion = lastName.substring(0, Math.min(lastNameLetters, lastName.length()));

        Long currentTime = System.currentTimeMillis() / 1000000000;

        int year = (Calendar.getInstance().get(Calendar.YEAR) - 1900) % 100; // get current year (year since 1900), and module by 100 to get two las digits

        return String.format("%s%s%d%02d", firstName, lastNamePortion, currentTime, year).toUpperCase();
    }
}
