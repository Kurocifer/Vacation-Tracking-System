package com.vts.vaccation_tracking_system.api.controller.auth;

import com.vts.vaccation_tracking_system.api.model.LoginBody;
import com.vts.vaccation_tracking_system.api.model.LoginResponse;
import com.vts.vaccation_tracking_system.api.model.RegistrationBody;
import com.vts.vaccation_tracking_system.exception.InvalidUserRoleException;
import com.vts.vaccation_tracking_system.exception.UserAlreadyExistsException;
import com.vts.vaccation_tracking_system.model.AbstractUser;
import com.vts.vaccation_tracking_system.service.UserService;
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
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (InvalidUserRoleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(jwt == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(jwt);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public AbstractUser getUserProfile(@AuthenticationPrincipal AbstractUser user) {
        return user;
    }
}
