package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDAO extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUsernameIgnoreCase(String username);

    Optional<Employee> findByEmailIgnoreCase(String email);


}
