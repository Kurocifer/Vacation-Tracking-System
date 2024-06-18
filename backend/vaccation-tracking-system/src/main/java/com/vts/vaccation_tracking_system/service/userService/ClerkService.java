package com.vts.vaccation_tracking_system.service.userService;

import com.vts.vaccation_tracking_system.api.model.auth.LoginBody;
import com.vts.vaccation_tracking_system.api.model.auth.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.EmailFailureException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.exception.UserNotVerifiedException;
import com.vts.vaccation_tracking_system.model.ClerkVerificationToken;
import com.vts.vaccation_tracking_system.model.HRClerk;
import com.vts.vaccation_tracking_system.model.dao.ClerkDAO;
import com.vts.vaccation_tracking_system.model.dao.ClerkVerificationTokenDAO;
import com.vts.vaccation_tracking_system.service.EmailService;
import com.vts.vaccation_tracking_system.service.EncryptionService;
import com.vts.vaccation_tracking_system.service.JWTService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ClerkService {

    private ClerkDAO clerkDAO;
    private JWTService jwtService;
    private EncryptionService encryptionService;
    private EmailService emailService;
    private ClerkVerificationTokenDAO clerkVerificationTokenDAO;

    public ClerkService(ClerkDAO clerkDAO, JWTService jwtService, EncryptionService encryptionService, EmailService emailService, ClerkVerificationTokenDAO clerkVerificationTokenDAO) {
        this.clerkDAO = clerkDAO;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
        this.emailService = emailService;
        this.clerkVerificationTokenDAO = clerkVerificationTokenDAO;
    }

    public void register(RegistrationBody registrationBody, String username) throws UserAlreadyExistsException, EmailFailureException {

        if(clerkDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        HRClerk clerk = new HRClerk();

        clerk.setFirstName(registrationBody.getFirstName());
        clerk.setLastName(registrationBody.getLastName());
        clerk.setEmail(registrationBody.getEmail());
        clerk.setUsername(username);

        String password = registrationBody.getPassword();
        clerk.setPassword(encryptionService.encryptPassword(password));
        clerkDAO.save(clerk);

        ClerkVerificationToken verificationToken = createVerificationToken(clerk);
        emailService.sendVerificationEmail(verificationToken);
        clerkVerificationTokenDAO.save(verificationToken);
        System.out.println(clerk);

        clerkDAO.save(clerk);
    }

    public String login(LoginBody loginBody) throws EmailFailureException, UserNotVerifiedException {
        Optional<HRClerk> opClerk = clerkDAO.findByUsernameIgnoreCase(loginBody.getUsername());

        if(opClerk.isPresent()) {
            HRClerk clerk = opClerk.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), clerk.getPassword())) {
                if(clerk.isEmailVerified()) {
                    return jwtService.generateJWT(clerk);
                } else {
                    List<ClerkVerificationToken> verificationTokens = clerk.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() ||
                            verificationTokens.get(0).getCreatedTimeStamp()
                                    .before(new Timestamp(System.currentTimeMillis() - (60 * 6 * 1000)));

                    if(resend) {
                        ClerkVerificationToken verificationToken = createVerificationToken(clerk);
                        emailService.sendVerificationEmail(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                        clerkVerificationTokenDAO.save(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    private ClerkVerificationToken createVerificationToken(HRClerk clerk) {
        ClerkVerificationToken verificationToken = new ClerkVerificationToken();

        verificationToken.setToken(jwtService.generateVerificationJWT(clerk));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setClerk(clerk);

        clerk.getVerificationTokens().add(verificationToken);

        return verificationToken;
    }

    public boolean verifyUser(String token) {
        Optional<ClerkVerificationToken> optionalToken = clerkVerificationTokenDAO.findByToken(token);

        if(optionalToken.isPresent()) {
            ClerkVerificationToken verificationToken = optionalToken.get();
            HRClerk clerk = (HRClerk) verificationToken.getUser();

            if (!clerk.isEmailVerified()) {
                clerk.setEmailVerified(true);
                clerkDAO.save(clerk);
                clerkVerificationTokenDAO.deleteByClerk(clerk);
                return true;
            }
        }
        return false;
    }
}
