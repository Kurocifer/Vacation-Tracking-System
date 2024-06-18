package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Employee;
import com.vts.vaccation_tracking_system.model.Grant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrantDAO extends JpaRepository<Grant, Long> {
    List<Grant> findByEmployee_UsernameIgnoreCase(String username);

    List<Grant> findByEmployee(Employee employee);
}
