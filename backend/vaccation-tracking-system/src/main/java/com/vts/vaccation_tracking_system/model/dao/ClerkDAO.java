package com.vts.vaccation_tracking_system.model.dao;

import com.vts.vaccation_tracking_system.model.HRClerk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClerkDAO extends JpaRepository<HRClerk, Long> {
    Optional<HRClerk> findByUsernameIgnoreCase(String username);

    Optional<HRClerk> findByEmailIgnoreCase(String email);
}
