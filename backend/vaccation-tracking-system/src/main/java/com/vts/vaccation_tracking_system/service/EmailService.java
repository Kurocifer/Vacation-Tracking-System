package com.vts.vaccation_tracking_system.service;

import com.vts.vaccation_tracking_system.exception.EmailFailureException;
import com.vts.vaccation_tracking_system.exception.EmployeeDoesNotExistException;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.VerificationToken;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.service.userService.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class EmailService {


    @Value("${email.from}")
    private String fromAddress;
    @Value("${app.frontend.url}")
    private String url;

    private final JavaMailSender javaMailSender;
    private final EmployeeDAO employeeDAO;


    public EmailService( JavaMailSender javaMailSender, EmployeeDAO employeeDAO) {
        this.javaMailSender = javaMailSender;
        this.employeeDAO = employeeDAO;
    }

    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();

        message.setTo(verificationToken.getUser().getEmail());

        message.setSubject("Verify your email address to activate your account");
        message.setText("Please follow the link below to verify your email to activate you account.\n" +
                url + "auth/verify?token" + verificationToken.getToken());

        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            System.out.println("Email service throwing it's exception." );
            throw new EmailFailureException();
        }
    }

    public void sendVacationRequestNotificationEmailToManager(Employee employee) throws EmployeeDoesNotExistException {
        Optional<Employee> optionalEmployee = employeeDAO.findByUsernameIgnoreCase(employee.getUsername());

        if(optionalEmployee.isPresent()) {
            final String SUBJECT = "Validate an employee's vacation request";

            Stream<String> managerEmails = employee.getManagers().stream()
                    .map(Manager::getEmail); // get the emails of all managers associated with this employee

            managerEmails.forEach(email -> {
                try {
                    sendEmail(email, SUBJECT, createManagerNotificationEmailBody(employee));
                } catch (EmailFailureException e) {
                    System.out.println("failed to send email");
                    throw new RuntimeException(e);
                }
            });

        } else {
            throw new EmployeeDoesNotExistException();
        }
    }

    private String createManagerNotificationEmailBody(Employee employee) {
        return "Employee " + employee.getFirstName()+" "+ employee.getLastName() +
                " , has requested a vacation time.\n" +
                "Employee details\n" +
                "Username: " + employee.getUsername() +
                "Email address: " + employee.getEmail() +
                "Follow this link to validate this request" + url;
    }

    private void sendEmail(String recipientEmail, String subject, String body) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();

        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setTo(body);

        try {
            javaMailSender.send(message);
        } catch(MailException e) {
            System.out.println("Failed to send message");
            throw new EmailFailureException();
        }
    }

}
