package com.vts.vaccation_tracking_system.api.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class RegistrationBody {
    private String firstName;
    private String lastName;
    private String password;
    private String email;

    private String userRole;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return "RegistrationBody{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
