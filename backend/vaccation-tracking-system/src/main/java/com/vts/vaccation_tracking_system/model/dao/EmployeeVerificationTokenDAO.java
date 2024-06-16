package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.EmployeeVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeVerificationTokenDAO extends JpaRepository<EmployeeVerificationToken, Long> {
    Optional<EmployeeVerificationToken> findByToken(String token);

    void deleteByEmployee(Employee employee);
}
