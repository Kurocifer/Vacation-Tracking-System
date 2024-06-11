package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDAO extends JpaRepository<Employee, Long> {

}
