package com.vts.vaccation_tracking_system.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.vts.vaccation_tracking_system.model.AbstractUser;
import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.HRClerk;
import com.vts.vaccation_tracking_system.model.Manager;
import com.vts.vaccation_tracking_system.model.dao.ClerkDAO;
import com.vts.vaccation_tracking_system.model.dao.EmployeeDAO;
import com.vts.vaccation_tracking_system.model.dao.ManagerDAO;
import com.vts.vaccation_tracking_system.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.weaver.NameMangler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private EmployeeDAO employeeDAO;
    private ManagerDAO managerDAO;
    private ClerkDAO clerkDAO;

    public JWTRequestFilter(JWTService jwtService, EmployeeDAO employeeDAO, ManagerDAO managerDAO, ClerkDAO clerkDAO) {
        this.jwtService = jwtService;
        this.employeeDAO = employeeDAO;
        this.managerDAO = managerDAO;
        this.clerkDAO = clerkDAO;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");

        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);  // remove "Bearer " with is attached to the beginning of the token.

            try {
                String username = jwtService.getUsername(token); // get the user's name from the jwt token

                // get the user's details based on their username
                // The first three letters of the user's username is an identifier for their role
                System.out.println("jwt request filter" +" "+ username +" "+ username.substring(0, 3));
                AbstractUser user = switch (username.substring(0, 3)) {
                    case "EMP" -> getEmployeeDetails(username);
                    case "MAN" -> getManagerDetails(username);
                    case "CLE" -> getClerkDetails(username);
                    default -> throw new IllegalArgumentException("Invalid username format");
                };

                if(user != null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch(JWTDecodeException ignored) {

            }
        }

       filterChain.doFilter(request, response);
    }

    private Employee getEmployeeDetails(String username) {
        Optional<Employee> optionalEmployee = employeeDAO.findByUsernameIgnoreCase(username);

        return optionalEmployee.orElse(null);
    }

    private Manager getManagerDetails(String username) {
        Optional<Manager> optionalManager = managerDAO.findByUsernameIgnoreCase(username);

        return optionalManager.orElse(null);
    }

    private HRClerk getClerkDetails(String username) {
        Optional<HRClerk> optionalClerk = clerkDAO.findByUsernameIgnoreCase(username);

        return optionalClerk.orElse(null);
    }
}
