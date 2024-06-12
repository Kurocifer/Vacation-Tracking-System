package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerDAO extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUsernameIgnoreCase(String username);

    Optional<Manager> findByEmailIgnoreCase(String email);
}
