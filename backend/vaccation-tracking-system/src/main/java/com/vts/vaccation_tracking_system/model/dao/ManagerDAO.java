package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerDAO extends JpaRepository<Manager, Long> {
}
