package com.vts.vaccation_tracking_system.api.controller.auth;

import com.vts.vaccation_tracking_system.api.model.LoginBody;
import com.vts.vaccation_tracking_system.api.model.LoginResponse;
import com.vts.vaccation_tracking_system.api.model.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.EmailFailureException;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.exception.UserNotVerifiedException;
import com.vts.vaccation_tracking_system.model.AbstractUser;
import com.vts.vaccation_tracking_system.model.dto.UserDTO;
import com.vts.vaccation_tracking_system.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (InvalidUserRoleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        System.out.println("something");
        try {
            jwt = userService.loginUser(loginBody);
        } catch (InvalidUserRoleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotVerifiedException e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(e.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch(EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if(jwt == null) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setJwt(jwt);
            response.setFailureReason("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public UserDTO getUserProfile(@AuthenticationPrincipal AbstractUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        return userDTO;
    }

    @PostMapping("/verify")
        public ResponseEntity verifyEmail(@RequestParam String token) {
        try {
            if(userService.verifyUser(token)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (InvalidUserRoleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
